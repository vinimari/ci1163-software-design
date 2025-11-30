package com.seucantinho.api.feature.pagamento.application.service;

import com.seucantinho.api.feature.espaco.domain.Espaco;
import com.seucantinho.api.feature.espaco.domain.valueobject.Capacidade;
import com.seucantinho.api.feature.filial.domain.Filial;
import com.seucantinho.api.feature.pagamento.application.dto.PagamentoRequestDTO;
import com.seucantinho.api.feature.pagamento.application.dto.PagamentoResponseDTO;
import com.seucantinho.api.feature.pagamento.domain.Pagamento;
import com.seucantinho.api.feature.pagamento.domain.enums.TipoPagamentoEnum;
import com.seucantinho.api.feature.pagamento.domain.port.out.PagamentoRepositoryPort;
import com.seucantinho.api.feature.pagamento.infrastructure.mapper.PagamentoMapper;
import com.seucantinho.api.feature.reserva.application.service.ReservaStatusService;
import com.seucantinho.api.feature.reserva.domain.Reserva;
import com.seucantinho.api.feature.reserva.domain.enums.StatusReservaEnum;
import com.seucantinho.api.feature.reserva.domain.port.out.ReservaRepositoryPort;
import com.seucantinho.api.feature.reserva.domain.valueobject.DataEvento;
import com.seucantinho.api.feature.cliente.domain.Cliente;
import com.seucantinho.api.shared.domain.exception.ResourceNotFoundException;
import com.seucantinho.api.shared.domain.valueobject.ValorMonetario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do PagamentoService")
class PagamentoServiceTest {

    @Mock
    private PagamentoRepositoryPort pagamentoRepositoryPort;

    @Mock
    private ReservaRepositoryPort reservaRepositoryPort;

    @Mock
    private PagamentoMapper pagamentoMapper;

    @Mock
    private ReservaStatusService reservaStatusService;

    @InjectMocks
    private PagamentoService pagamentoService;

    private Reserva reserva;
    private Pagamento pagamento;
    private PagamentoRequestDTO requestDTO;
    private PagamentoResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        reserva = criarReserva();
        pagamento = criarPagamento();
        requestDTO = criarRequestDTO();
        responseDTO = criarResponseDTO();
    }

    @Test
    @DisplayName("Deve retornar todos os pagamentos")
    void deveRetornarTodosPagamentos() {
        // Arrange
        List<Pagamento> pagamentos = Arrays.asList(pagamento);
        when(pagamentoRepositoryPort.findAll()).thenReturn(pagamentos);
        when(pagamentoMapper.toResponseDTO(any(Pagamento.class))).thenReturn(responseDTO);

        // Act
        List<PagamentoResponseDTO> resultado = pagamentoService.findAll();

        // Assert
        assertThat(resultado).hasSize(1);
        verify(pagamentoRepositoryPort).findAll();
        verify(pagamentoMapper).toResponseDTO(any(Pagamento.class));
    }

    @Test
    @DisplayName("Deve retornar pagamento por ID")
    void deveRetornarPagamentoPorId() {
        // Arrange
        Integer id = 1;
        when(pagamentoRepositoryPort.findById(id)).thenReturn(Optional.of(pagamento));
        when(pagamentoMapper.toResponseDTO(pagamento)).thenReturn(responseDTO);

        // Act
        PagamentoResponseDTO resultado = pagamentoService.findById(id);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado).isEqualTo(responseDTO);
        verify(pagamentoRepositoryPort).findById(id);
        verify(pagamentoMapper).toResponseDTO(pagamento);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar pagamento por ID inexistente")
    void deveLancarExcecaoAoBuscarPagamentoPorIdInexistente() {
        // Arrange
        Integer id = 999;
        when(pagamentoRepositoryPort.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> pagamentoService.findById(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Pagamento não encontrado com ID: " + id);
        verify(pagamentoRepositoryPort).findById(id);
        verify(pagamentoMapper, never()).toResponseDTO(any());
    }

    @Test
    @DisplayName("Deve retornar pagamentos por reserva ID")
    void deveRetornarPagamentosPorReservaId() {
        // Arrange
        Integer reservaId = 1;
        List<Pagamento> pagamentos = Arrays.asList(pagamento);
        when(pagamentoRepositoryPort.findByReservaId(reservaId)).thenReturn(pagamentos);
        when(pagamentoMapper.toResponseDTO(any(Pagamento.class))).thenReturn(responseDTO);

        // Act
        List<PagamentoResponseDTO> resultado = pagamentoService.findByReservaId(reservaId);

        // Assert
        assertThat(resultado).hasSize(1);
        verify(pagamentoRepositoryPort).findByReservaId(reservaId);
        verify(pagamentoMapper).toResponseDTO(any(Pagamento.class));
    }

    @Test
    @DisplayName("Deve criar pagamento com sucesso")
    void deveCriarPagamentoComSucesso() {
        // Arrange
        when(reservaRepositoryPort.findByIdWithPagamentos(requestDTO.getReservaId()))
                .thenReturn(Optional.of(reserva));
        when(pagamentoMapper.toEntity(requestDTO, reserva)).thenReturn(pagamento);
        when(pagamentoRepositoryPort.save(pagamento)).thenReturn(pagamento);
        when(pagamentoMapper.toResponseDTO(pagamento)).thenReturn(responseDTO);
        doNothing().when(reservaStatusService).updateStatusAfterPayment(reserva, pagamento);
        when(reservaRepositoryPort.save(reserva)).thenReturn(reserva);

        // Act
        PagamentoResponseDTO resultado = pagamentoService.create(requestDTO);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado).isEqualTo(responseDTO);
        verify(reservaRepositoryPort).findByIdWithPagamentos(requestDTO.getReservaId());
        verify(pagamentoMapper).toEntity(requestDTO, reserva);
        verify(pagamentoRepositoryPort).save(pagamento);
        verify(reservaStatusService).updateStatusAfterPayment(reserva, pagamento);
        verify(reservaRepositoryPort).save(reserva);
        verify(pagamentoMapper).toResponseDTO(pagamento);
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar pagamento com reserva inexistente")
    void deveLancarExcecaoAoCriarPagamentoComReservaInexistente() {
        // Arrange
        when(reservaRepositoryPort.findByIdWithPagamentos(requestDTO.getReservaId()))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> pagamentoService.create(requestDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Reserva não encontrada com ID: " + requestDTO.getReservaId());
        verify(reservaRepositoryPort).findByIdWithPagamentos(requestDTO.getReservaId());
        verify(pagamentoRepositoryPort, never()).save(any());
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não há pagamentos")
    void deveRetornarListaVaziaQuandoNaoHaPagamentos() {
        // Arrange
        when(pagamentoRepositoryPort.findAll()).thenReturn(Arrays.asList());

        // Act
        List<PagamentoResponseDTO> resultado = pagamentoService.findAll();

        // Assert
        assertThat(resultado).isEmpty();
        verify(pagamentoRepositoryPort).findAll();
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não há pagamentos para a reserva")
    void deveRetornarListaVaziaQuandoNaoHaPagamentosParaReserva() {
        // Arrange
        Integer reservaId = 1;
        when(pagamentoRepositoryPort.findByReservaId(reservaId)).thenReturn(Arrays.asList());

        // Act
        List<PagamentoResponseDTO> resultado = pagamentoService.findByReservaId(reservaId);

        // Assert
        assertThat(resultado).isEmpty();
        verify(pagamentoRepositoryPort).findByReservaId(reservaId);
    }

    // Métodos auxiliares
    private Reserva criarReserva() {
        Cliente cliente = Cliente.builder()
                .id(1)
                .nome("João Silva")
                .email("joao@email.com")
                .senhaHash("hash123")
                .build();

        Filial filial = Filial.builder()
                .id(1)
                .nome("Filial Centro")
                .cidade("Curitiba")
                .estado("PR")
                .build();

        Espaco espaco = Espaco.builder()
                .id(1)
                .nome("Salão de Eventos")
                .capacidade(Capacidade.of(50))
                .precoDiaria(ValorMonetario.of("300.00"))
                .filial(filial)
                .ativo(true)
                .build();

        return Reserva.builder()
                .id(1)
                .usuario(cliente)
                .espaco(espaco)
                .dataEvento(DataEvento.of(LocalDate.now().plusDays(10)))
                .valorTotal(ValorMonetario.of("300.00"))
                .status(StatusReservaEnum.AGUARDANDO_SINAL)
                .pagamentos(new ArrayList<>())
                .build();
    }

    private Pagamento criarPagamento() {
        return Pagamento.builder()
                .id(1)
                .reserva(reserva)
                .valor(ValorMonetario.of("150.00"))
                .tipo(TipoPagamentoEnum.SINAL)
                .formaPagamento("PIX")
                .build();
    }

    private PagamentoRequestDTO criarRequestDTO() {
        return PagamentoRequestDTO.builder()
                .reservaId(1)
                .valor(new BigDecimal("150.00"))
                .tipo(TipoPagamentoEnum.SINAL)
                .formaPagamento("PIX")
                .build();
    }

    private PagamentoResponseDTO criarResponseDTO() {
        return PagamentoResponseDTO.builder()
                .id(1)
                .reservaId(1)
                .valor(new BigDecimal("150.00"))
                .tipo(TipoPagamentoEnum.SINAL)
                .formaPagamento("PIX")
                .build();
    }
}
