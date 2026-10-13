package com.agritech.dondo.config;

import com.agritech.dondo.model.Cultura;
import com.agritech.dondo.model.Perfil;
import com.agritech.dondo.model.Utilizador;
import com.agritech.dondo.repository.CulturaRepository;
import com.agritech.dondo.repository.UtilizadorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UtilizadorRepository utilizadorRepository;
    private final CulturaRepository culturaRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UtilizadorRepository utilizadorRepository,
                           CulturaRepository culturaRepository,
                           PasswordEncoder passwordEncoder) {
        this.utilizadorRepository = utilizadorRepository;
        this.culturaRepository = culturaRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        criarAdminPadrao();
        criarProdutosAgricolas();
    }

    private void criarAdminPadrao() {
        if (!utilizadorRepository.existsByNome("leonel rachid")) {
            Utilizador admin = new Utilizador(
                    "leonel rachid",
                    "leonel rachid",
                    passwordEncoder.encode("L852623034"),
                    Perfil.ADMIN
            );
            utilizadorRepository.save(admin);
            log.info("Admin padrao criado: leonel rachid");
        } else {
            log.info("Admin padrao ja existe, ignorando.");
        }
    }

    private void criarProdutosAgricolas() {
        if (culturaRepository.count() > 0) {
            log.info("Produtos agricolas ja existem ({} encontrados), ignorando.", culturaRepository.count());
            return;
        }

        String[][] produtos = {
                // Horticolas
                {"Tomate", "Horticola", "KG"},
                {"Cebola", "Horticola", "KG"},
                {"Repolho", "Horticola", "KG"},
                {"Alface", "Horticola", "KG"},
                {"Couve", "Horticola", "KG"},
                {"Pepino", "Horticola", "KG"},
                {"Pimento", "Horticola", "KG"},
                {"Cenoura", "Horticola", "KG"},
                {"Abobora", "Horticola", "KG"},
                {"Quiabo", "Horticola", "KG"},
                // Cereais e Leguminosas
                {"Milho", "Cereal", "KG"},
                {"Arroz", "Cereal", "KG"},
                {"Feijao", "Leguminosa", "KG"},
                {"Feijao-Nhemba", "Leguminosa", "KG"},
                {"Amendoim", "Leguminosa", "KG"},
                {"Gergelim", "Cereal", "KG"},
                {"Mapira", "Cereal", "KG"},
                {"Mexoeira", "Cereal", "KG"},
                // Raizes e Tuberculos
                {"Mandioca", "Raiz", "KG"},
                {"Batata-Doce", "Raiz", "KG"},
                {"Batata-Reno", "Raiz", "KG"},
                // Frutas
                {"Banana", "Fruta", "KG"},
                {"Manga", "Fruta", "KG"},
                {"Papaia", "Fruta", "KG"},
                {"Ananás", "Fruta", "KG"},
                {"Coco", "Fruta", "UN"},
                {"Laranja", "Fruta", "KG"},
                {"Limao", "Fruta", "KG"},
                // Outros
                {"Cana-de-Acucar", "Industrial", "TON"},
                {"Algodao", "Industrial", "KG"},
        };

        for (String[] p : produtos) {
            culturaRepository.save(new Cultura(p[0], p[1], p[2]));
        }
        log.info("Criados {} produtos agricolas com sucesso.", produtos.length);
    }
}
