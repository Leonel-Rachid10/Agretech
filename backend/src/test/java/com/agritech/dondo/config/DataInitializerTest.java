package com.agritech.dondo.config;

import com.agritech.dondo.model.Cultura;
import com.agritech.dondo.repository.CulturaRepository;
import com.agritech.dondo.repository.UtilizadorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DataInitializerTest {

    @Mock
    private UtilizadorRepository utilizadorRepository;

    @Mock
    private CulturaRepository culturaRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private DataInitializer dataInitializer;

    @BeforeEach
    void setUp() {
        dataInitializer = new DataInitializer(utilizadorRepository, culturaRepository, passwordEncoder);
    }

    @Test
    @DisplayName("Normalização deve tratar acentos, espaços, hífens e maiúsculas")
    void deveNormalizarNomesCorretamente() {
        assertEquals("feijaonhemba", DataInitializer.normalizar("Feijão Nhemba"));
        assertEquals("feijaonhemba", DataInitializer.normalizar("feijao-nhemba"));
        assertEquals("limao", DataInitializer.normalizar("Limão"));
        assertEquals("canadeacucar", DataInitializer.normalizar("Cana de Açúcar"));
        assertEquals("abobora", DataInitializer.normalizar("Abóbora"));
        assertEquals("", DataInitializer.normalizar(null));
    }

    @Test
    @DisplayName("Normalização deve mapear sinónimos para chaves canónicas para evitar redundância")
    void deveMapearSinonimosParaEvitarRedundancia() {
        assertEquals("gergelim", DataInitializer.normalizar("Sésamo"));
        assertEquals("gergelim", DataInitializer.normalizar("sesamo"));
        assertEquals("batatareno", DataInitializer.normalizar("Batata-comum"));
        assertEquals("batatareno", DataInitializer.normalizar("Batata Comum"));
        assertEquals("coco", DataInitializer.normalizar("Coqueiro"));
    }

    @Test
    @DisplayName("Deve inicializar todos os 43 produtos agrícolas quando a base de dados estiver vazia")
    void deveCriarProdutosQuandoBaseVazia() {
        when(utilizadorRepository.existsByNome("leonel rachid")).thenReturn(true);
        when(culturaRepository.findAll()).thenReturn(new ArrayList<>());
        when(culturaRepository.count()).thenReturn(43L);

        dataInitializer.run();

        // Verifica que foram guardados 43 produtos
        verify(culturaRepository, times(43)).save(any(Cultura.class));
    }

    @Test
    @DisplayName("Deve atualizar nomes sem acento e não duplicar produtos já existentes")
    void deveAtualizarSemDuplicarProdutosExistentes() {
        when(utilizadorRepository.existsByNome("leonel rachid")).thenReturn(true);

        // Simula base existente com produtos sem acento
        List<Cultura> existentes = new ArrayList<>();
        Cultura c1 = new Cultura("Feijao Nhemba", "Leguminosa", "KG");
        c1.setId(1L);
        Cultura c2 = new Cultura("Limao", "Fruta", "KG");
        c2.setId(2L);
        existentes.add(c1);
        existentes.add(c2);

        when(culturaRepository.findAll()).thenReturn(existentes);
        when(culturaRepository.count()).thenReturn(43L);

        dataInitializer.run();

        // c1 e c2 devem ser atualizados com acento ("Feijão Nhemba", "Limão")
        assertEquals("Feijão Nhemba", c1.getNome());
        assertEquals("Limão", c2.getNome());

        // Deve salvar as 2 atualizações + os 41 novos produtos = 43 invocações de save
        verify(culturaRepository, times(43)).save(any(Cultura.class));
    }
}
