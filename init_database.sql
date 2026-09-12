CREATE DATABASE IF NOT EXISTS agritech_dondo_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE agritech_dondo_db;

-- 1. TABELA DE UTILIZADORES
CREATE TABLE IF NOT EXISTS utilizador (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    telemovel VARCHAR(20) NOT NULL UNIQUE,
    senha_hash VARCHAR(255) NOT NULL,
    perfil ENUM('ADMIN', 'GESTOR_ASSOCIACAO', 'PRODUTOR', 'COMPRADOR') NOT NULL,
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- 2. TABELA DE ASSOCIAÇÕES AGRÍCOLAS DO DONDO
CREATE TABLE IF NOT EXISTS associacao (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    localidade VARCHAR(100) NOT NULL,
    povoado VARCHAR(100),
    contacto_principal VARCHAR(20),
    gestor_id BIGINT,
    ponto_focal_nome VARCHAR(100),
    ponto_focal_telemovel VARCHAR(20),
    data_registo TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_associacao_gestor FOREIGN KEY (gestor_id) REFERENCES utilizador(id) ON DELETE SET NULL
) ENGINE=InnoDB;

-- 3. TABELA DE PRODUTORES
CREATE TABLE IF NOT EXISTS produtor (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    telemovel VARCHAR(20) NOT NULL,
    associacao_id BIGINT NOT NULL,
    localizacao_detalhada TEXT,
    data_registo TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_produtor_associacao FOREIGN KEY (associacao_id) REFERENCES associacao(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 4. TABELA DE CULTURAS AGRÍCOLAS
CREATE TABLE IF NOT EXISTS cultura (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL UNIQUE,
    categoria VARCHAR(50) NOT NULL,
    unidade_medida VARCHAR(20) NOT NULL DEFAULT 'KG'
) ENGINE=InnoDB;

-- 5. TABELA DE LOTES DE PRODUÇÃO (OFERTA)
CREATE TABLE IF NOT EXISTS lote_producao (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    produtor_id BIGINT NOT NULL,
    cultura_id BIGINT NOT NULL,
    quantidade_estimada DECIMAL(10,2) NOT NULL,
    data_sementeira DATE,
    data_colheita_prevista DATE NOT NULL,
    estado ENUM('EM_CRESCIMENTO', 'PRONTO_PARA_COLHEITA', 'RESERVADO', 'VENDIDO') NOT NULL DEFAULT 'EM_CRESCIMENTO',
    preco_por_unidade DECIMAL(10,2),
    observacoes TEXT,
    data_atualizacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_lote_produtor FOREIGN KEY (produtor_id) REFERENCES produtor(id) ON DELETE CASCADE,
    CONSTRAINT fk_lote_cultura FOREIGN KEY (cultura_id) REFERENCES cultura(id) ON DELETE RESTRICT
) ENGINE=InnoDB;

-- 6. TABELA DE REGISTO DE CONTACTOS E INTERAÇÕES DE NEGÓCIO
CREATE TABLE IF NOT EXISTS reserva_contacto (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    lote_id BIGINT NOT NULL,
    comprador_id BIGINT NOT NULL,
    data_contacto TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    estado_negocio ENUM('INICIADO', 'EM_NEGOCIACAO', 'CONCLUIDO', 'CANCELADO') DEFAULT 'INICIADO',
    CONSTRAINT fk_reserva_lote FOREIGN KEY (lote_id) REFERENCES lote_producao(id) ON DELETE CASCADE,
    CONSTRAINT fk_reserva_comprador FOREIGN KEY (comprador_id) REFERENCES utilizador(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- DADOS INICIAIS DE CULTURAS
INSERT IGNORE INTO cultura (nome, categoria, unidade_medida) VALUES
('Tomate', 'Hortícola', 'KG'),
('Cebola', 'Hortícola', 'KG'),
('Repolho', 'Hortícola', 'KG'),
('Milho', 'Cereal', 'KG'),
('Feijão Nhemba', 'Leguminosa', 'KG'),
('Mandioca', 'Tubérculo', 'KG'),
('Pimento', 'Hortícola', 'KG'),
('Alface', 'Hortícola', 'KG');
