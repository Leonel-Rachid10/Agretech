package com.agritech.dondo.config;

import com.agritech.dondo.model.Perfil;
import com.agritech.dondo.model.Utilizador;
import com.agritech.dondo.repository.LoteProducaoRepository;
import com.agritech.dondo.repository.ProdutorRepository;
import com.agritech.dondo.repository.UtilizadorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Order(2)
public class DataLoader implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataLoader.class);
    private static final String ADMIN_NOME = "leonel rachid";
    private static final String ADMIN_SENHA = "L852623034";

    private final UtilizadorRepository utilizadorRepository;
    private final ProdutorRepository produtorRepository;
    private final LoteProducaoRepository loteProducaoRepository;
    private final PasswordEncoder passwordEncoder;

    public DataLoader(UtilizadorRepository utilizadorRepository,
                      ProdutorRepository produtorRepository,
                      LoteProducaoRepository loteProducaoRepository,
                      PasswordEncoder passwordEncoder) {
        this.utilizadorRepository = utilizadorRepository;
        this.produtorRepository = produtorRepository;
        this.loteProducaoRepository = loteProducaoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // 1. Garantir Utilizador Administrador Real (Leonel Rachid)
        Utilizador gestor = utilizadorRepository.findByNome(ADMIN_NOME).orElse(new Utilizador());
        gestor.setNome(ADMIN_NOME);
        gestor.setTelemovel("841234567");
        gestor.setSenhaHash(passwordEncoder.encode(ADMIN_SENHA));
        gestor.setPerfil(Perfil.ADMIN);
        utilizadorRepository.save(gestor);

        // 2. Limpar qualquer utilizador ou dado fictício gerado anteriormente
        utilizadorRepository.findByNome("comprador").ifPresent(utilizadorRepository::delete);

        List<String> produtoresFicticios = List.of(
                "Mateus Sitoe", "Esperança Macamo", "António Cossa",
                "Fátima Machel", "Carlos Tembe", "Celso Mondlane",
                "Joaquim Macamo", "Ana Paula Sitoe", "Mateus Zandamela"
        );
        for (String nome : produtoresFicticios) {
            produtorRepository.findAll().stream()
                    .filter(p -> p.getNome().equalsIgnoreCase(nome))
                    .forEach(p -> {
                        loteProducaoRepository.deleteAll(loteProducaoRepository.findByProdutorId(p.getId()));
                        produtorRepository.delete(p);
                        log.info("Removido produtor fictício: {}", nome);
                    });
        }

        log.info("AgreTech inicializado sem dados fictícios. Base limpa para dados reais.");
    }
}
