package com.seucantinho.api.service;

import com.seucantinho.api.domain.entity.Cliente;
import com.seucantinho.api.domain.entity.Espaco;
import com.seucantinho.api.domain.entity.Pagamento;
import com.seucantinho.api.domain.entity.Reserva;
import com.seucantinho.api.domain.enums.StatusReservaEnum;
import com.seucantinho.api.domain.enums.TipoPagamentoEnum;
import com.seucantinho.api.dto.pagamento.PagamentoRequestDTO;
import com.seucantinho.api.dto.pagamento.PagamentoResponseDTO;
import com.seucantinho.api.exception.BusinessException;
import com.seucantinho.api.exception.ResourceNotFoundException;
import com.seucantinho.api.mapper.PagamentoMapper;
import com.seucantinho.api.repository.PagamentoRepository;
import com.seucantinho.api.repository.ReservaRepository;
import com.seucantinho.api.validator.PagamentoValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PagamentoServiceTest {

    @Mock
    private PagamentoRepository pagamentoRepository;

    @Mock
    private ReservaRepository reservaRepository;

    @Mock
    private PagamentoMapper pagamentoMapper;

    @Mock
    private PagamentoValidator pagamentoValidator;

    @InjectMocks
    private PagamentoService pagamentoService;

    private Pagamento pagamento;
    private Reserva reserva;
    private Cliente cliente;
    private Espaco espaco;
    private PagamentoRequestDTO requestDTO;
    private PagamentoResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        cliente = Cliente.builder()
                .id(1)
                .nome("João Silva")
                .email("joao@email.com")
                .build();

        espaco = Espaco.builder()
                .id(1)
                .nome("Sala de Reunião A")
                .precoDiaria(new BigDecimal("100.00"))
                .build();

        reserva = Reserva.builder()
                .id(1)
                .usuario(cliente)
                .espaco(espaco)
                .dataEvento(LocalDate.now().plusDays(7))
                .valorTotal(new BigDecimal("800.00"))
                .status(StatusReservaEnum.AGUARDANDO_SINAL)
                .pagamentos(new ArrayList<>())
                .build();

        pagamento = Pagamento.builder()
                .id(1)
                .reserva(reserva)
                .valor(new BigDecimal("400.00"))
                .tipo(TipoPagamentoEnum.SINAL)
                .dataPagamento(LocalDateTime.now())
                .build();

        requestDTO = PagamentoRequestDTO.builder()
                .reservaId(1)
                .valor(new BigDecimal("400.00"))
                .tipo(TipoPagamentoEnum.SINAL)
                .build();

        responseDTO = PagamentoResponseDTO.builder()
                .id(1)
                .reservaId(1)
                .valor(new BigDecimal("400.00"))
                .tipo(TipoPagamentoEnum.SINAL)
                .dataPagamento(LocalDateTime.now())
                .build();
    }

    @Test
    void shouldFindAllPagamentos() {
        // Given
        Pagamento pagamento2 = Pagamento.builder()
                .id(2)
                .valor(new BigDecimal("400.00"))
                .build();
        
        PagamentoResponseDTO responseDTO2 = PagamentoResponseDTO.builder()
                .id(2)
                .build();

        when(pagamentoRepository.findAll()).thenReturn(Arrays.asList(pagamento, pagamento2));
        when(pagamentoMapper.toResponseDTO(pagamento)).thenReturn(responseDTO);
        when(pagamentoMapper.toResponseDTO(pagamento2)).thenReturn(responseDTO2);

        // When
        List<PagamentoResponseDTO> result = pagamentoService.findAll();

        // Then
        assertThat(result).hasSize(2);
        verify(pagamentoRepository, times(1)).findAll();
        verify(pagamentoMapper, times(2)).toResponseDTO(any(Pagamento.class));
    }

    @Test
    void shouldFindAllPagamentos_WhenEmpty() {
        // Given
        when(pagamentoRepository.findAll()).thenReturn(Collections.emptyList());

        // When
        List<PagamentoResponseDTO> result = pagamentoService.findAll();

        // Then
        assertThat(result).isEmpty();
        verify(pagamentoRepository, times(1)).findAll();
    }

    @Test
    void shouldFindPagamentoById() {
        // Given
        when(pagamentoRepository.findById(1)).thenReturn(Optional.of(pagamento));
        when(pagamentoMapper.toResponseDTO(pagamento)).thenReturn(responseDTO);

        // When
        PagamentoResponseDTO result = pagamentoService.findById(1);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getValor()).isEqualByComparingTo(new BigDecimal("400.00"));
        assertThat(result.getTipo()).isEqualTo(TipoPagamentoEnum.SINAL);
        verify(pagamentoRepository, times(1)).findById(1);
        verify(pagamentoMapper, times(1)).toResponseDTO(pagamento);
    }

    @Test
    void shouldThrowExceptionWhenPagamentoNotFound() {
        // Given
        when(pagamentoRepository.findById(999)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> pagamentoService.findById(999))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Pagamento não encontrado com ID: 999");

        verify(pagamentoRepository, times(1)).findById(999);
        verify(pagamentoMapper, never()).toResponseDTO(any());
    }

    @Test
    void shouldFindByReservaId() {
        // Given
        when(pagamentoRepository.findByReservaId(1)).thenReturn(Arrays.asList(pagamento));
        when(pagamentoMapper.toResponseDTO(pagamento)).thenReturn(responseDTO);

        // When
        List<PagamentoResponseDTO> result = pagamentoService.findByReservaId(1);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getReservaId()).isEqualTo(1);
        verify(pagamentoRepository, times(1)).findByReservaId(1);
    }

    @Test
    void shouldFindByReservaId_WhenNoPagamentos() {
        // Given
        when(pagamentoRepository.findByReservaId(999)).thenReturn(Collections.emptyList());

        // When
        List<PagamentoResponseDTO> result = pagamentoService.findByReservaId(999);

        // Then
        assertThat(result).isEmpty();
        verify(pagamentoRepository, times(1)).findByReservaId(999);
    }

    @Test
    void shouldCreatePagamento() {
        // Given
        when(reservaRepository.findByIdWithPagamentos(1)).thenReturn(Optional.of(reserva));
        doNothing().when(pagamentoValidator).validateValorPagamento(requestDTO.getValor(), reserva);
        when(pagamentoMapper.toEntity(requestDTO, reserva)).thenReturn(pagamento);
        when(pagamentoRepository.save(pagamento)).thenReturn(pagamento);
        when(pagamentoMapper.toResponseDTO(pagamento)).thenReturn(responseDTO);

        // When
        PagamentoResponseDTO result = pagamentoService.create(requestDTO);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getValor()).isEqualByComparingTo(new BigDecimal("400.00"));
        verify(reservaRepository, times(1)).findByIdWithPagamentos(1);
        verify(pagamentoValidator, times(1)).validateValorPagamento(requestDTO.getValor(), reserva);
        verify(pagamentoMapper, times(1)).toEntity(requestDTO, reserva);
        verify(pagamentoRepository, times(1)).save(pagamento);
    }

    @Test
    void shouldCreatePagamento_WithDifferentTipos() {
        // Given
        TipoPagamentoEnum[] tipos = {
            TipoPagamentoEnum.SINAL,
            TipoPagamentoEnum.QUITACAO,
            TipoPagamentoEnum.TOTAL
        };

        for (TipoPagamentoEnum tipo : tipos) {
            PagamentoRequestDTO dto = PagamentoRequestDTO.builder()
                    .reservaId(1)
                    .valor(new BigDecimal("400.00"))
                    .tipo(tipo)
                    .build();

            when(reservaRepository.findByIdWithPagamentos(1)).thenReturn(Optional.of(reserva));
            doNothing().when(pagamentoValidator).validateValorPagamento(dto.getValor(), reserva);
            when(pagamentoMapper.toEntity(dto, reserva)).thenReturn(pagamento);
            when(pagamentoRepository.save(pagamento)).thenReturn(pagamento);
            when(pagamentoMapper.toResponseDTO(pagamento)).thenReturn(responseDTO);

            // When
            PagamentoResponseDTO result = pagamentoService.create(dto);

            // Then
            assertThat(result).isNotNull();
        }

        verify(pagamentoRepository, times(3)).save(any(Pagamento.class));
    }

    @Test
    void shouldThrowExceptionWhenCreateWithInvalidReserva() {
        // Given
        when(reservaRepository.findByIdWithPagamentos(999)).thenReturn(Optional.empty());

        // When & Then
        PagamentoRequestDTO invalidDTO = PagamentoRequestDTO.builder()
                .reservaId(999)
                .valor(requestDTO.getValor())
                .tipo(requestDTO.getTipo())
                .build();
        
        assertThatThrownBy(() -> pagamentoService.create(invalidDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Reserva não encontrada com ID: 999");

        verify(reservaRepository, times(1)).findByIdWithPagamentos(999);
        verify(pagamentoValidator, never()).validateValorPagamento(any(), any());
        verify(pagamentoRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenCreateWithExcessiveValue() {
        // Given
        when(reservaRepository.findByIdWithPagamentos(1)).thenReturn(Optional.of(reserva));
        doThrow(new BusinessException("Valor do pagamento excede o saldo da reserva"))
                .when(pagamentoValidator).validateValorPagamento(requestDTO.getValor(), reserva);

        // When & Then
        assertThatThrownBy(() -> pagamentoService.create(requestDTO))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Valor do pagamento excede o saldo");

        verify(reservaRepository, times(1)).findByIdWithPagamentos(1);
        verify(pagamentoValidator, times(1)).validateValorPagamento(requestDTO.getValor(), reserva);
        verify(pagamentoRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenCreateExceedingTotalValue() {
        // Given
        // Adicionar pagamento que já esgota 50% do valor
        Pagamento pagamentoExistente = Pagamento.builder()
                .id(2)
                .valor(new BigDecimal("400.00"))
                .build();
        reserva.getPagamentos().add(pagamentoExistente);

        // Tentar adicionar mais 60% (excede o total)
        PagamentoRequestDTO excessiveDTO = PagamentoRequestDTO.builder()
                .reservaId(1)
                .valor(new BigDecimal("480.00"))
                .tipo(TipoPagamentoEnum.QUITACAO)
                .build();

        when(reservaRepository.findByIdWithPagamentos(1)).thenReturn(Optional.of(reserva));
        doThrow(new BusinessException("Valor do pagamento excede o saldo da reserva"))
                .when(pagamentoValidator).validateValorPagamento(excessiveDTO.getValor(), reserva);

        // When & Then
        assertThatThrownBy(() -> pagamentoService.create(excessiveDTO))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Valor do pagamento excede o saldo");

        verify(pagamentoValidator, times(1)).validateValorPagamento(excessiveDTO.getValor(), reserva);
        verify(pagamentoRepository, never()).save(any());
    }

    @Test
    void shouldCreateMultiplePagamentos_ForSameReserva() {
        // Given - Primeiro pagamento (50%)
        PagamentoRequestDTO primeiroDTO = PagamentoRequestDTO.builder()
                .reservaId(1)
                .valor(new BigDecimal("400.00"))
                .tipo(TipoPagamentoEnum.SINAL)
                .build();

        when(reservaRepository.findByIdWithPagamentos(1)).thenReturn(Optional.of(reserva));
        doNothing().when(pagamentoValidator).validateValorPagamento(primeiroDTO.getValor(), reserva);
        when(pagamentoMapper.toEntity(primeiroDTO, reserva)).thenReturn(pagamento);
        when(pagamentoRepository.save(pagamento)).thenReturn(pagamento);
        when(pagamentoMapper.toResponseDTO(pagamento)).thenReturn(responseDTO);

        // When - Criar primeiro pagamento
        PagamentoResponseDTO resultado1 = pagamentoService.create(primeiroDTO);

        // Then
        assertThat(resultado1).isNotNull();

        // Given - Segundo pagamento (50% restante)
        reserva.getPagamentos().add(pagamento);
        PagamentoRequestDTO segundoDTO = PagamentoRequestDTO.builder()
                .reservaId(1)
                .valor(new BigDecimal("400.00"))
                .tipo(TipoPagamentoEnum.QUITACAO)
                .build();

        Pagamento pagamento2 = Pagamento.builder()
                .id(2)
                .reserva(reserva)
                .valor(new BigDecimal("400.00"))
                .tipo(TipoPagamentoEnum.QUITACAO)
                .build();

        PagamentoResponseDTO responseDTO2 = PagamentoResponseDTO.builder()
                .id(2)
                .valor(new BigDecimal("400.00"))
                .build();

        when(reservaRepository.findByIdWithPagamentos(1)).thenReturn(Optional.of(reserva));
        doNothing().when(pagamentoValidator).validateValorPagamento(segundoDTO.getValor(), reserva);
        when(pagamentoMapper.toEntity(segundoDTO, reserva)).thenReturn(pagamento2);
        when(pagamentoRepository.save(pagamento2)).thenReturn(pagamento2);
        when(pagamentoMapper.toResponseDTO(pagamento2)).thenReturn(responseDTO2);

        // When - Criar segundo pagamento
        PagamentoResponseDTO resultado2 = pagamentoService.create(segundoDTO);

        // Then
        assertThat(resultado2).isNotNull();
        verify(pagamentoRepository, times(2)).save(any(Pagamento.class));
    }

    @Test
    void shouldDeletePagamento() {
        // Given
        when(pagamentoRepository.existsById(1)).thenReturn(true);
        doNothing().when(pagamentoRepository).deleteById(1);

        // When
        pagamentoService.delete(1);

        // Then
        verify(pagamentoRepository, times(1)).existsById(1);
        verify(pagamentoRepository, times(1)).deleteById(1);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistentPagamento() {
        // Given
        when(pagamentoRepository.existsById(999)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> pagamentoService.delete(999))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Pagamento não encontrado com ID: 999");

        verify(pagamentoRepository, times(1)).existsById(999);
        verify(pagamentoRepository, never()).deleteById(any());
    }

    @Test
    void shouldHandleZeroValue() {
        // Given
        PagamentoRequestDTO zeroDTO = PagamentoRequestDTO.builder()
                .reservaId(1)
                .valor(BigDecimal.ZERO)
                .tipo(TipoPagamentoEnum.SINAL)
                .build();

        when(reservaRepository.findByIdWithPagamentos(1)).thenReturn(Optional.of(reserva));
        doNothing().when(pagamentoValidator).validateValorPagamento(BigDecimal.ZERO, reserva);
        when(pagamentoMapper.toEntity(zeroDTO, reserva)).thenReturn(pagamento);
        when(pagamentoRepository.save(pagamento)).thenReturn(pagamento);
        when(pagamentoMapper.toResponseDTO(pagamento)).thenReturn(responseDTO);

        // When
        PagamentoResponseDTO result = pagamentoService.create(zeroDTO);

        // Then
        assertThat(result).isNotNull();
        verify(pagamentoValidator, times(1)).validateValorPagamento(BigDecimal.ZERO, reserva);
    }
}
