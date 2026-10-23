package com.agritech.dondo.config;

import com.agritech.dondo.model.Cultura;
import com.agritech.dondo.model.Perfil;
import com.agritech.dondo.model.Utilizador;
import com.agritech.dondo.repository.CulturaRepository;
import com.agritech.dondo.repository.UtilizadorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.text.Normalizer;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Component
@Order(1)
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UtilizadorRepository utilizadorRepository;
    private final CulturaRepository culturaRepository;
    private final PasswordEncoder passwordEncoder;

    // Mapa de sinonimos / apelidos para evitar redundancia de cadastro
    private static final Map<String, String> ALIASES = Map.of(
            "sesamo", "gergelim",
            "batatacomum", "batatareno",
            "coqueiro", "coco"
    );

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

    /**
     * Normaliza nomes removendo acentos, espacos e caracteres especiais,
     * e converte sinonimos para a chave canonica.
     */
    public static String normalizar(String texto) {
        if (texto == null) return "";
        String nfd = Normalizer.normalize(texto, Normalizer.Form.NFD);
        String limpo = nfd.replaceAll("\\p{M}", "")
                          .replaceAll("[^a-zA-Z0-9]", "")
                          .toLowerCase(Locale.ROOT);
        return ALIASES.getOrDefault(limpo, limpo);
    }

    private void criarProdutosAgricolas() {
        String[][] produtos = {
                // 1. Cereais e Graos
                {"Milho", "Cereal", "KG"},
                {"Arroz", "Cereal", "KG"},
                {"Mapira", "Cereal", "KG"},
                {"Mexoeira", "Cereal", "KG"},

                // 2. Leguminosas e Oleaginosas
                {"Feijão", "Leguminosa", "KG"},
                {"Feijão Manteiga", "Leguminosa", "KG"},
                {"Feijão Nhemba", "Leguminosa", "KG"},
                {"Feijão Jogo", "Leguminosa", "KG"},
                {"Feijão Boer", "Leguminosa", "KG"},
                {"Soja", "Oleaginosa", "KG"},
                {"Gergelim", "Oleaginosa", "KG"}, // Cobre Gergelim e Sesamo
                {"Amendoim", "Leguminosa", "KG"},
                {"Girassol", "Oleaginosa", "KG"},

                // 3. Culturas Industriais e de Rendimento (Comerciais)
                {"Tabaco", "Industrial", "KG"},
                {"Algodão", "Industrial", "KG"},
                {"Chá", "Industrial", "KG"},
                {"Castanha de Caju", "Industrial", "KG"},
                {"Cana de Açúcar", "Industrial", "TON"},
                {"Café", "Industrial", "KG"},

                // 4. Tuberculos e Culturas de Subsistencia (Raizes e Tuberculos)
                {"Mandioca", "Raiz", "KG"},
                {"Batata Reno", "Raiz", "KG"}, // Cobre Batata-reno e Batata-comum
                {"Batata Doce", "Raiz", "KG"},
                {"Inhame", "Raiz", "KG"},

                // 5. Fruticultura
                {"Abacate", "Fruta", "KG"},
                {"Laranja", "Fruta", "KG"},   // Citrinos
                {"Tangerina", "Fruta", "KG"}, // Citrinos
                {"Limão", "Fruta", "KG"},     // Citrinos
                {"Lichia", "Fruta", "KG"},
                {"Manga", "Fruta", "KG"},
                {"Banana", "Fruta", "KG"},
                {"Ananás", "Fruta", "KG"},
                {"Papaia", "Fruta", "KG"},
                {"Coco", "Fruta", "UN"},      // Cobre Coco e Coqueiro

                // 6. Horticolas
                {"Tomate", "Horticola", "KG"},
                {"Repolho", "Horticola", "KG"},
                {"Cebola", "Horticola", "KG"},
                {"Cenoura", "Horticola", "KG"},
                {"Pimento", "Horticola", "KG"},
                {"Alface", "Horticola", "KG"},
                {"Couve", "Horticola", "KG"},
                {"Pepino", "Horticola", "KG"},
                {"Abóbora", "Horticola", "KG"},
                {"Quiabo", "Horticola", "KG"}
        };

        List<Cultura> existentes = culturaRepository.findAll();
        Map<String, Cultura> existentesPorChave = new HashMap<>();
        for (Cultura c : existentes) {
            existentesPorChave.put(normalizar(c.getNome()), c);
        }

        int adicionados = 0;
        int atualizados = 0;

        for (String[] p : produtos) {
            String nomePadrao = p[0];
            String categoria = p[1];
            String unidadeMedida = p[2];
            String chave = normalizar(nomePadrao);

            Cultura existente = existentesPorChave.get(chave);
            if (existente == null) {
                culturaRepository.save(new Cultura(nomePadrao, categoria, unidadeMedida));
                adicionados++;
            } else {
                boolean alterado = false;
                if (!existente.getNome().equals(nomePadrao)) {
                    existente.setNome(nomePadrao);
                    alterado = true;
                }
                if (!existente.getCategoria().equals(categoria)) {
                    existente.setCategoria(categoria);
                    alterado = true;
                }
                if (!existente.getUnidadeMedida().equals(unidadeMedida)) {
                    existente.setUnidadeMedida(unidadeMedida);
                    alterado = true;
                }
                if (alterado) {
                    culturaRepository.save(existente);
                    atualizados++;
                }
            }
        }

        log.info("Produtos agricolas sincronizados: {} adicionados, {} atualizados (total: {}).",
                adicionados, atualizados, culturaRepository.count());
    }
}
