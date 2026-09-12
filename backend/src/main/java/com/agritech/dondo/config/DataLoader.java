package com.agritech.dondo.config;

import com.agritech.dondo.model.Associacao;
import com.agritech.dondo.model.Cultura;
import com.agritech.dondo.model.EstadoLote;
import com.agritech.dondo.model.LoteProducao;
import com.agritech.dondo.model.Perfil;
import com.agritech.dondo.model.Produtor;
import com.agritech.dondo.model.Utilizador;
import com.agritech.dondo.repository.AssociacaoRepository;
import com.agritech.dondo.repository.CulturaRepository;
import com.agritech.dondo.repository.LoteProducaoRepository;
import com.agritech.dondo.repository.ProdutorRepository;
import com.agritech.dondo.repository.UtilizadorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@Component
public class DataLoader implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataLoader.class);
    private static final String RESPONSAVEL_SISTEMA = "Leonel Rachid";
    private static final String ADMIN_TELEMOVEL = "841234567";
    private static final String GESTOR_TELEMOVEL = "842345678";
    private static final String SENHA_GESTAO = "1004";

    private final UtilizadorRepository utilizadorRepository;
    private final AssociacaoRepository associacaoRepository;
    private final ProdutorRepository produtorRepository;
    private final CulturaRepository culturaRepository;
    private final LoteProducaoRepository loteProducaoRepository;
    private final PasswordEncoder passwordEncoder;

    public DataLoader(UtilizadorRepository utilizadorRepository,
                      AssociacaoRepository associacaoRepository,
                      ProdutorRepository produtorRepository,
                      CulturaRepository culturaRepository,
                      LoteProducaoRepository loteProducaoRepository,
                      PasswordEncoder passwordEncoder) {
        this.utilizadorRepository = utilizadorRepository;
        this.associacaoRepository = associacaoRepository;
        this.produtorRepository = produtorRepository;
        this.culturaRepository = culturaRepository;
        this.loteProducaoRepository = loteProducaoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // 1. Garantir Utilizadores Iniciais (Leonel Rachid - Admin e Gestor)
        garantirUtilizadorGestao(ADMIN_TELEMOVEL, Perfil.ADMIN);
        Utilizador gestor = garantirUtilizadorGestao(GESTOR_TELEMOVEL, Perfil.GESTOR_ASSOCIACAO);

        if (utilizadorRepository.findByTelemovel("843456789").isEmpty()) {
            Utilizador comprador = new Utilizador(
                    "Hotel e Restaurante Beira Mar",
                    "843456789",
                    passwordEncoder.encode("comprador123"),
                    Perfil.COMPRADOR
            );
            utilizadorRepository.save(comprador);
        }

        // 2. Garantir Associações do Dondo
        if (associacaoRepository.count() == 0) {
            log.info("Carregando associações agrícolas piloto do Distrito do Dondo...");
            // Reutiliza gestor Leonel Rachid para as associações
            Associacao assMafambisse = new Associacao(
                    "Associação dos Camponeses de Mafambisse",
                    "Mafambisse",
                    "Bloco 3, Zona Baixa",
                    "+258849876543",
                    gestor,
                    "Alberto Sithole",
                    "+258849876543"
            );
            associacaoRepository.save(assMafambisse);

            Associacao assChinamacondo = new Associacao(
                    "Cooperativa Agropecuária de Chinamacondo",
                    "Chinamacondo",
                    "Bairro Central",
                    "+258861234567",
                    gestor,
                    "Maria Cumbana",
                    "+258861234567"
            );
            associacaoRepository.save(assChinamacondo);

            Associacao assDondoSede = new Associacao(
                    "União dos Produtores de Dondo Sede",
                    "Dondo Sede",
                    "Zona Periférica 2",
                    "+258872345678",
                    gestor,
                    "Zacarias Tembe",
                    "+258872345678"
            );
            associacaoRepository.save(assDondoSede);
        }

        // 3. Garantir Produtores
        if (produtorRepository.count() == 0) {
            log.info("Carregando produtores locais do Dondo...");

            Associacao maf = associacaoRepository.findByLocalidadeContainingIgnoreCase("Mafambisse").stream().findFirst().orElse(null);
            Associacao chin = associacaoRepository.findByLocalidadeContainingIgnoreCase("Chinamacondo").stream().findFirst().orElse(null);
            Associacao sede = associacaoRepository.findByLocalidadeContainingIgnoreCase("Dondo").stream().findFirst().orElse(null);

            if (maf != null) {
                produtorRepository.save(new Produtor("António Macamo", "+258841112233", maf, "Parcela 14, Regadio de Mafambisse"));
                produtorRepository.save(new Produtor("Rosita Sengulane", "+258842223344", maf, "Machamba do Rio Púnguè"));
            }
            if (chin != null) {
                produtorRepository.save(new Produtor("Rosa Mabote", "+258852223344", chin, "Zona Alta, Chinamacondo"));
                produtorRepository.save(new Produtor("Domingos Chiconela", "+258853334455", chin, "Próximo à Escola Primária"));
            }
            if (sede != null) {
                produtorRepository.save(new Produtor("Mateus Zandamela", "+258863334455", sede, "Bairro Central, Dondo"));
            }
        }

        // 4. Garantir Lotes de Produção (Ofertas reais no Catálogo)
        if (loteProducaoRepository.count() == 0) {
            log.info("Carregando lotes de produção agrícolas de teste no Dondo...");

            Optional<Produtor> p1 = produtorRepository.findByTelemovel("+258841112233");
            Optional<Produtor> p2 = produtorRepository.findByTelemovel("+258852223344");
            Optional<Produtor> p3 = produtorRepository.findByTelemovel("+258863334455");

            Optional<Cultura> cTomate = culturaRepository.findByNomeIgnoreCase("Tomate");
            Optional<Cultura> cCebola = culturaRepository.findByNomeIgnoreCase("Cebola");
            Optional<Cultura> cRepolho = culturaRepository.findByNomeIgnoreCase("Repolho");
            Optional<Cultura> cMilho = culturaRepository.findByNomeIgnoreCase("Milho");
            Optional<Cultura> cFeijao = culturaRepository.findByNomeIgnoreCase("Feijão Nhemba");

            if (p1.isPresent() && cTomate.isPresent()) {
                LoteProducao lote1 = new LoteProducao(
                        p1.get(),
                        cTomate.get(),
                        new BigDecimal("1500.00"),
                        LocalDate.now().minusMonths(2),
                        LocalDate.now().plusDays(5),
                        EstadoLote.PRONTO_PARA_COLHEITA,
                        new BigDecimal("45.00"),
                        "Tomate redondo de primeira qualidade, colheita iminente no regadio."
                );
                loteProducaoRepository.save(lote1);
            }

            if (p2.isPresent() && cCebola.isPresent()) {
                LoteProducao lote2 = new LoteProducao(
                        p2.get(),
                        cCebola.get(),
                        new BigDecimal("2200.00"),
                        LocalDate.now().minusMonths(3),
                        LocalDate.now().plusDays(20),
                        EstadoLote.EM_CRESCIMENTO,
                        new BigDecimal("60.00"),
                        "Cebola roxa média/grande, secagem tradicional garantida."
                );
                loteProducaoRepository.save(lote2);
            }

            if (p1.isPresent() && cRepolho.isPresent()) {
                LoteProducao lote3 = new LoteProducao(
                        p1.get(),
                        cRepolho.get(),
                        new BigDecimal("800.00"),
                        LocalDate.now().minusMonths(2),
                        LocalDate.now().plusDays(2),
                        EstadoLote.PRONTO_PARA_COLHEITA,
                        new BigDecimal("35.00"),
                        "Cabeças firmes e frescas prontas para restaurantes da Beira."
                );
                loteProducaoRepository.save(lote3);
            }

            if (p3.isPresent() && cMilho.isPresent()) {
                LoteProducao lote4 = new LoteProducao(
                        p3.get(),
                        cMilho.get(),
                        new BigDecimal("5000.00"),
                        LocalDate.now().minusMonths(4),
                        LocalDate.now().plusDays(35),
                        EstadoLote.EM_CRESCIMENTO,
                        new BigDecimal("25.00"),
                        "Milho branco para farinha ou consumo em espiga."
                );
                loteProducaoRepository.save(lote4);
            }

            if (p2.isPresent() && cFeijao.isPresent()) {
                LoteProducao lote5 = new LoteProducao(
                        p2.get(),
                        cFeijao.get(),
                        new BigDecimal("1200.00"),
                        LocalDate.now().minusMonths(3),
                        LocalDate.now().minusDays(1),
                        EstadoLote.RESERVADO,
                        new BigDecimal("80.00"),
                        "Feijão nhemba seco, saco de 50kg, lote reservado por comprador da Beira."
                );
                loteProducaoRepository.save(lote5);
            }
        }

        log.info("AgriTech Dondo inicializado com sucesso! Pronto para operar no Distrito do Dondo.");
    }

    private Utilizador garantirUtilizadorGestao(String telemovel, Perfil perfil) {
        Utilizador utilizador = utilizadorRepository.findByTelemovel(telemovel).orElse(new Utilizador());
        utilizador.setNome(RESPONSAVEL_SISTEMA);
        utilizador.setTelemovel(telemovel);
        utilizador.setSenhaHash(passwordEncoder.encode(SENHA_GESTAO));
        utilizador.setPerfil(perfil);
        return utilizadorRepository.save(utilizador);
    }
}
