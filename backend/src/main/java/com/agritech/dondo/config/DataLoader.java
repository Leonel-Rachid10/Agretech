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

        // Mock data removed per user request
    }
}
