package com.agritech.dondo.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Locale;

/**
 * Mantém instalações criadas antes da remoção da associação obrigatória
 * compatíveis com o cadastro independente de agricultores.
 */
@Component
@Order(0)
public class SchemaCompatibilityMigration implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(SchemaCompatibilityMigration.class);

    private final DataSource dataSource;

    public SchemaCompatibilityMigration(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void run(String... args) {
        try (Connection connection = dataSource.getConnection()) {
            ColumnInfo associacaoId = findAssociacaoIdColumn(connection);
            if (associacaoId == null || associacaoId.nullable()) {
                return;
            }

            String database = connection.getMetaData().getDatabaseProductName().toLowerCase(Locale.ROOT);
            try (Statement statement = connection.createStatement()) {
                if (database.contains("mysql") || database.contains("mariadb")) {
                    statement.execute("ALTER TABLE produtor MODIFY COLUMN associacao_id BIGINT NULL");
                } else if (database.contains("h2")) {
                    statement.execute("ALTER TABLE produtor ALTER COLUMN associacao_id DROP NOT NULL");
                } else {
                    log.warn("A coluna produtor.associacao_id continua obrigatória; aplique uma migração para permitir valores nulos em {}.", database);
                    return;
                }
            }

            log.info("Migração concluída: produtor.associacao_id agora é opcional.");
        } catch (SQLException exception) {
            log.warn("Não foi possível verificar a compatibilidade do cadastro de agricultores: {}", exception.getMessage());
        }
    }

    private ColumnInfo findAssociacaoIdColumn(Connection connection) throws SQLException {
        DatabaseMetaData metadata = connection.getMetaData();
        try (ResultSet columns = metadata.getColumns(connection.getCatalog(), null, "%", "%")) {
            while (columns.next()) {
                String table = columns.getString("TABLE_NAME");
                String column = columns.getString("COLUMN_NAME");
                if ("produtor".equalsIgnoreCase(table) && "associacao_id".equalsIgnoreCase(column)) {
                    return new ColumnInfo(columns.getInt("NULLABLE") != DatabaseMetaData.columnNoNulls);
                }
            }
        }
        return null;
    }

    private record ColumnInfo(boolean nullable) {
    }
}
