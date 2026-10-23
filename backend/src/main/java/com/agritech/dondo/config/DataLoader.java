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
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@Component
@Order(2)
public class DataLoader implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataLoader.class);
    private static final String ADMIN_NOME = "leonel rachid";
    private static final String ADMIN_SENHA = "L852623034";

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
        // 1. Garantir Utilizador Leonel Rachid
        Utilizador gestor = utilizadorRepository.findByNome(ADMIN_NOME).orElse(new Utilizador());
        gestor.setNome(ADMIN_NOME);
        gestor.setTelemovel("841234567");
        gestor.setSenhaHash(passwordEncoder.encode(ADMIN_SENHA));
        gestor.setPerfil(Perfil.ADMIN);
        gestor = utilizadorRepository.save(gestor);

        if (utilizadorRepository.findByNome("comprador").isEmpty()) {
            Utilizador comprador = new Utilizador(
                    "comprador",
                    "000000000",
                    passwordEncoder.encode("comprador123"),
                    Perfil.COMPRADOR
            );
            utilizadorRepository.save(comprador);
        }

        // 2. Criar Agricultores e Produtos Iniciais caso a base de dados esteja vazia
        criarProdutoresELotesIniciais();
    }

    private void criarProdutoresELotesIniciais() {
        if (produtorRepository.count() == 0) {
            Produtor p1 = new Produtor();
            p1.setNome("Mateus Sitoe");
            p1.setTelemovel("841234567");
            p1.setProvincia("Sofala");
            p1.setDistrito("Dondo");
            p1.setBairro("Campo");
            p1.setLocalizacaoDetalhada("Zona agrícola do Dondo e Mafambisse");
            produtorRepository.save(p1);

            Produtor p2 = new Produtor();
            p2.setNome("Esperança Macamo");
            p2.setTelemovel("849876543");
            p2.setProvincia("Zambézia");
            p2.setDistrito("Gurúè");
            p2.setBairro("Bairro Central");
            p2.setLocalizacaoDetalhada("Planalto de Gurúè");
            produtorRepository.save(p2);

            Produtor p3 = new Produtor();
            p3.setNome("António Cossa");
            p3.setTelemovel("823456789");
            p3.setProvincia("Manica");
            p3.setDistrito("Chimoio");
            p3.setBairro("Vanduzi");
            p3.setLocalizacaoDetalhada("Corredor da Beira em Vanduzi");
            produtorRepository.save(p3);

            Produtor p4 = new Produtor();
            p4.setNome("Fátima Machel");
            p4.setTelemovel("876543210");
            p4.setProvincia("Tete");
            p4.setDistrito("Angónia");
            p4.setBairro("Dômuè");
            p4.setLocalizacaoDetalhada("Planalto fértil de Angónia");
            produtorRepository.save(p4);

            Produtor p5 = new Produtor();
            p5.setNome("Carlos Tembe");
            p5.setTelemovel("843332211");
            p5.setProvincia("Manica");
            p5.setDistrito("Sussundenga");
            p5.setBairro("Bairro Central");
            p5.setLocalizacaoDetalhada("Zona de Sussundenga");
            produtorRepository.save(p5);

            Produtor p6 = new Produtor();
            p6.setNome("Celso Mondlane");
            p6.setTelemovel("855554433");
            p6.setProvincia("Sofala");
            p6.setDistrito("Beira");
            p6.setBairro("Alto da Manga");
            p6.setLocalizacaoDetalhada("Cintura verde da Beira");
            produtorRepository.save(p6);
        }

        if (loteProducaoRepository.count() == 0) {
            java.util.List<Produtor> produtores = produtorRepository.findAll();
            if (!produtores.isEmpty()) {
                Produtor pMateus = produtores.get(0);
                Produtor pEsperanca = produtores.size() > 1 ? produtores.get(1) : pMateus;
                Produtor pAntonio = produtores.size() > 2 ? produtores.get(2) : pMateus;
                Produtor pFatima = produtores.size() > 3 ? produtores.get(3) : pMateus;
                Produtor pCarlos = produtores.size() > 4 ? produtores.get(4) : pMateus;
                Produtor pCelso = produtores.size() > 5 ? produtores.get(5) : pMateus;

                salvarLoteSeExistirCultura("Milho", pMateus, "2500", "35.00", EstadoLote.PRONTO_PARA_COLHEITA, 15, "Milho de alta qualidade, pronto para entrega imediata.");
                salvarLoteSeExistirCultura("Arroz", pEsperanca, "5000", "45.00", EstadoLote.PRONTO_PARA_COLHEITA, 20, "Arroz nacional colhido e seco.");
                salvarLoteSeExistirCultura("Feijão Manteiga", pFatima, "1800", "60.00", EstadoLote.EM_CRESCIMENTO, 45, "Feijão manteiga de Angónia em fase final.");
                salvarLoteSeExistirCultura("Soja", pCarlos, "4000", "40.00", EstadoLote.EM_CRESCIMENTO, 60, "Soja de Manica para comércio e indústria.");
                salvarLoteSeExistirCultura("Mandioca", pEsperanca, "3200", "20.00", EstadoLote.PRONTO_PARA_COLHEITA, 10, "Mandioca fresca dos campos da Zambézia.");
                salvarLoteSeExistirCultura("Batata Reno", pFatima, "2000", "50.00", EstadoLote.RESERVADO, 30, "Batata-reno comum de Angónia.");
                salvarLoteSeExistirCultura("Abacate", pAntonio, "1500", "55.00", EstadoLote.PRONTO_PARA_COLHEITA, 5, "Abacate tipo Hass e Fuerte para comercialização.");
                salvarLoteSeExistirCultura("Tomate", pCelso, "1200", "30.00", EstadoLote.PRONTO_PARA_COLHEITA, 7, "Tomate fresco da cintura verde.");
                salvarLoteSeExistirCultura("Gergelim", pAntonio, "1000", "85.00", EstadoLote.EM_CRESCIMENTO, 50, "Gergelim branco de primeira qualidade.");
                salvarLoteSeExistirCultura("Amendoim", pCarlos, "1500", "70.00", EstadoLote.PRONTO_PARA_COLHEITA, 12, "Amendoim graúdo para consumo e torrefação.");
                salvarLoteSeExistirCultura("Castanha de Caju", pEsperanca, "800", "95.00", EstadoLote.PRONTO_PARA_COLHEITA, 25, "Castanha de caju selecionada.");
                salvarLoteSeExistirCultura("Batata Doce", pFatima, "1600", "25.00", EstadoLote.PRONTO_PARA_COLHEITA, 14, "Batata-doce de polpa alaranjada e branca.");
            }
        }
    }

    private void salvarLoteSeExistirCultura(String culturaNome, Produtor produtor, String quantidade, String preco, EstadoLote estado, int diasAteColheita, String obs) {
        String nomeNormalizado = culturaNome.replace('-', ' ').trim();
        Optional<Cultura> culturaOpt = culturaRepository.findByNomeIgnoreCase(culturaNome);
        if (culturaOpt.isEmpty()) {
            culturaOpt = culturaRepository.findByNomeIgnoreCase(nomeNormalizado);
        }
        if (culturaOpt.isEmpty()) {
            culturaOpt = culturaRepository.findAll().stream()
                    .filter(c -> c.getNome().equalsIgnoreCase(nomeNormalizado) ||
                                 c.getNome().toLowerCase().contains(nomeNormalizado.toLowerCase()) ||
                                 c.getNome().toLowerCase().contains(culturaNome.toLowerCase()))
                    .findFirst();
        }
        if (culturaOpt.isPresent()) {
            Cultura cultura = culturaOpt.get();
            LocalDate sementeira = LocalDate.now().minusDays(60);
            LocalDate colheita = LocalDate.now().plusDays(diasAteColheita);
            LoteProducao lote = new LoteProducao(
                    produtor,
                    cultura,
                    new BigDecimal(quantidade),
                    sementeira,
                    colheita,
                    estado,
                    new BigDecimal(preco),
                    obs
            );
            loteProducaoRepository.save(lote);
        } else {
            log.warn("Cultura não encontrada para o lote inicial: {}", culturaNome);
        }
    }
}
