package com.agritech.dondo.service;

import com.agritech.dondo.model.Cultura;
import com.agritech.dondo.repository.CulturaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CulturaServiceTest {

    @Mock
    private CulturaRepository culturaRepository;

    private CulturaService culturaService;

    @BeforeEach
    void setUp() {
        culturaService = new CulturaService(culturaRepository);
    }

    @Test
    @DisplayName("Deve listar todas as culturas por ordem alfabética")
    void deveListarTodas() {
        when(culturaRepository.findAllByOrderByNomeAsc()).thenReturn(List.of(
                new Cultura("Abacate", "Fruta", "KG"),
                new Cultura("Milho", "Cereal", "KG")
        ));

        List<Cultura> resultado = culturaService.listarTodas();
        assertEquals(2, resultado.size());
        assertEquals("Abacate", resultado.get(0).getNome());
        verify(culturaRepository, times(1)).findAllByOrderByNomeAsc();
    }

    @Test
    @DisplayName("Deve buscar cultura por ID com sucesso")
    void deveBuscarPorId() {
        Cultura c = new Cultura("Milho", "Cereal", "KG");
        c.setId(10L);
        when(culturaRepository.findById(10L)).thenReturn(Optional.of(c));

        Cultura resultado = culturaService.buscarPorId(10L);
        assertNotNull(resultado);
        assertEquals("Milho", resultado.getNome());
    }

    @Test
    @DisplayName("Deve lançar exceção quando cultura não for encontrada")
    void deveLancarExcecaoQuandoNaoEncontrada() {
        when(culturaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> culturaService.buscarPorId(99L));
    }
}
