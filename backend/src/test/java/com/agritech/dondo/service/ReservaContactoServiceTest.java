package com.agritech.dondo.service;

import com.agritech.dondo.dto.ReservaContactoDTO;
import com.agritech.dondo.model.*;
import com.agritech.dondo.repository.LoteProducaoRepository;
import com.agritech.dondo.repository.ProdutorRepository;
import com.agritech.dondo.repository.ReservaContactoRepository;
import com.agritech.dondo.repository.UtilizadorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.access.AccessDeniedException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservaContactoServiceTest {

    @Mock
    private ReservaContactoRepository reservaContactoRepository;

    @Mock
    private LoteProducaoRepository loteProducaoRepository;

    @Mock
    private UtilizadorRepository utilizadorRepository;

    @Mock
    private ProdutorRepository produtorRepository;

    private ReservaContactoService reservaContactoService;

    private Utilizador admin;
    private Utilizador comprador;
    private Utilizador utilizadorProdutor;
    private Produtor produtor;
    private Cultura cultura;
    private LoteProducao lote;
    private ReservaContacto reserva;

    @BeforeEach
    void setUp() {
        reservaContactoService = new ReservaContactoService(
                reservaContactoRepository,
                loteProducaoRepository,
                utilizadorRepository,
                produtorRepository
        );

        admin = new Utilizador("leonel rachid", "841234567", "hash", Perfil.ADMIN);
        admin.setId(1L);

        comprador = new Utilizador("restaurante_beira", "849998877", "hash", Perfil.COMPRADOR);
        comprador.setId(2L);

        utilizadorProdutor = new Utilizador("produtor_dondo", "845556677", "hash", Perfil.PRODUTOR);
        utilizadorProdutor.setId(3L);

        produtor = new Produtor("Produtor Dondo", "845556677", null, "Campo");
        produtor.setId(10L);
        produtor.setProvincia("Sofala");
        produtor.setDistrito("Dondo");
        produtor.setBairro("Campo");
        produtor.setUtilizador(utilizadorProdutor);

        cultura = new Cultura("Tomate", "Horticola", "KG");
        cultura.setId(5L);

        lote = new LoteProducao(produtor, cultura, new BigDecimal("1200.00"),
                LocalDate.now().minusDays(30), LocalDate.now().plusDays(10),
                EstadoLote.PRONTO_PARA_COLHEITA, new BigDecimal("35.00"), "Tomate fresco");
        lote.setId(100L);

        reserva = new ReservaContacto(lote, comprador, EstadoNegocio.INICIADO);
        reserva.setId(50L);
        reserva.setDataContacto(LocalDateTime.now());
    }

    @Test
    @DisplayName("Deve registar contacto de reserva com sucesso")
    void deveRegistarContactoComSucesso() {
        ReservaContactoDTO dto = new ReservaContactoDTO();
        dto.setLoteId(100L);
        dto.setCompradorId(2L);

        when(loteProducaoRepository.findById(100L)).thenReturn(Optional.of(lote));
        when(utilizadorRepository.findById(2L)).thenReturn(Optional.of(comprador));
        when(reservaContactoRepository.save(any(ReservaContacto.class))).thenAnswer(invocation -> {
            ReservaContacto r = invocation.getArgument(0);
            r.setId(50L);
            return r;
        });

        ReservaContactoDTO resultado = reservaContactoService.registarContacto(dto, null);

        assertNotNull(resultado);
        assertEquals(100L, resultado.getLoteId());
        assertEquals("Tomate", resultado.getCulturaNome());
        assertEquals("restaurante_beira", resultado.getCompradorNome());
        assertEquals(EstadoNegocio.INICIADO, resultado.getEstadoNegocio());
        verify(reservaContactoRepository, times(1)).save(any(ReservaContacto.class));
    }

    @Test
    @DisplayName("Deve listar todas as reservas para Administrador")
    void deveListarTodasParaAdmin() {
        when(utilizadorRepository.findByNome("leonel rachid")).thenReturn(Optional.of(admin));
        when(reservaContactoRepository.findAllByOrderByDataContactoDesc()).thenReturn(List.of(reserva));

        List<ReservaContactoDTO> resultado = reservaContactoService.listarVisiveisPara("leonel rachid");

        assertEquals(1, resultado.size());
        assertEquals("Tomate", resultado.get(0).getCulturaNome());
        assertEquals("restaurante_beira", resultado.get(0).getCompradorNome());
        verify(reservaContactoRepository, times(1)).findAllByOrderByDataContactoDesc();
    }

    @Test
    @DisplayName("Deve listar reservas do comprador para conta COMPRADOR")
    void deveListarParaComprador() {
        when(utilizadorRepository.findByNome("restaurante_beira")).thenReturn(Optional.of(comprador));
        when(reservaContactoRepository.findByCompradorIdOrderByDataContactoDesc(2L)).thenReturn(List.of(reserva));

        List<ReservaContactoDTO> resultado = reservaContactoService.listarVisiveisPara("restaurante_beira");

        assertEquals(1, resultado.size());
        assertEquals(2L, resultado.get(0).getCompradorId());
        verify(reservaContactoRepository, times(1)).findByCompradorIdOrderByDataContactoDesc(2L);
    }

    @Test
    @DisplayName("Deve listar reservas do produtor para conta PRODUTOR")
    void deveListarParaProdutor() {
        when(utilizadorRepository.findByNome("produtor_dondo")).thenReturn(Optional.of(utilizadorProdutor));
        when(produtorRepository.findByUtilizadorId(3L)).thenReturn(Optional.of(produtor));
        when(reservaContactoRepository.findByLoteProdutorIdOrderByDataContactoDesc(10L)).thenReturn(List.of(reserva));

        List<ReservaContactoDTO> resultado = reservaContactoService.listarVisiveisPara("produtor_dondo");

        assertEquals(1, resultado.size());
        assertEquals("Produtor Dondo", resultado.get(0).getProdutorNome());
        verify(reservaContactoRepository, times(1)).findByLoteProdutorIdOrderByDataContactoDesc(10L);
    }

    @Test
    @DisplayName("Deve atualizar estado da negociação com sucesso")
    void deveAtualizarEstadoNegocioComSucesso() {
        when(reservaContactoRepository.findById(50L)).thenReturn(Optional.of(reserva));
        when(reservaContactoRepository.save(any(ReservaContacto.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ReservaContactoDTO atualizado = reservaContactoService.atualizarEstado(50L, EstadoNegocio.EM_NEGOCIACAO);

        assertNotNull(atualizado);
        assertEquals(EstadoNegocio.EM_NEGOCIACAO, atualizado.getEstadoNegocio());
        verify(reservaContactoRepository, times(1)).save(reserva);
    }

    @Test
    @DisplayName("Deve permitir ao comprador participante atualizar estado da negociação")
    void devePermitirCompradorAtualizarEstado() {
        when(reservaContactoRepository.findById(50L)).thenReturn(Optional.of(reserva));
        when(utilizadorRepository.findByNome("restaurante_beira")).thenReturn(Optional.of(comprador));
        when(reservaContactoRepository.save(any(ReservaContacto.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ReservaContactoDTO atualizado = reservaContactoService.atualizarEstado(50L, EstadoNegocio.CONCLUIDO, "restaurante_beira");

        assertNotNull(atualizado);
        assertEquals(EstadoNegocio.CONCLUIDO, atualizado.getEstadoNegocio());
        verify(reservaContactoRepository, times(1)).save(reserva);
    }

    @Test
    @DisplayName("Deve lançar AccessDeniedException quando utilizador não autorizado tenta alterar negociação")
    void deveLancarAccessDeniedExceptionParaUtilizadorNaoAutorizado() {
        Utilizador intruso = new Utilizador("comprador_estranho", "senha", "849999999", Perfil.COMPRADOR);
        intruso.setId(99L);

        when(reservaContactoRepository.findById(50L)).thenReturn(Optional.of(reserva));
        when(utilizadorRepository.findByNome("comprador_estranho")).thenReturn(Optional.of(intruso));

        assertThrows(AccessDeniedException.class, () ->
                reservaContactoService.atualizarEstado(50L, EstadoNegocio.CANCELADO, "comprador_estranho"));
    }

    @Test
    @DisplayName("Deve lançar exceção ao registar contacto de lote inexistente")
    void deveLancarExcecaoQuandoLoteInexistente() {
        ReservaContactoDTO dto = new ReservaContactoDTO();
        dto.setLoteId(999L);
        dto.setCompradorId(2L);

        when(loteProducaoRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> reservaContactoService.registarContacto(dto, null));
    }
}
