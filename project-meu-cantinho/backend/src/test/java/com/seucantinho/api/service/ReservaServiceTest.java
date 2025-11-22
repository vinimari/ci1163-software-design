package com.seucantinho.api.service;

import com.seucantinho.api.domain.entity.Cliente;
import com.seucantinho.api.domain.entity.Espaco;
import com.seucantinho.api.domain.entity.Filial;
import com.seucantinho.api.domain.entity.Reserva;
import com.seucantinho.api.domain.enums.StatusReservaEnum;
import com.seucantinho.api.dto.reserva.ReservaRequestDTO;
import com.seucantinho.api.dto.reserva.ReservaResponseDTO;
import com.seucantinho.api.exception.BusinessException;
import com.seucantinho.api.exception.ResourceNotFoundException;
import com.seucantinho.api.mapper.ReservaMapper;
import com.seucantinho.api.repository.EspacoRepository;
import com.seucantinho.api.repository.ReservaRepository;
import com.seucantinho.api.repository.UsuarioRepository;
import com.seucantinho.api.validator.ReservaValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservaServiceTest {

    @Mock
    private ReservaRepository reservaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private EspacoRepository espacoRepository;

    @Mock
    private ReservaMapper reservaMapper;

    @Mock
    private ReservaValidator reservaValidator;

    @InjectMocks
    private ReservaService reservaService;

    private Reserva reserva;
    private Cliente cliente;
    private Espaco espaco;
    private Filial filial;
    private ReservaRequestDTO requestDTO;
    private ReservaResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        filial = Filial.builder()
                .id(1)
                .nome("Filial Centro")
                .build();

        cliente = Cliente.builder()
                .id(1)
                .nome("João Silva")
                .email("joao@email.com")
                .build();

        espaco = Espaco.builder()
                .id(1)
                .nome("Sala de Reunião A")
                .capacidade(20)
                .precoDiaria(new BigDecimal("100.00"))
                .ativo(true)
                .filial(filial)
                .build();

        reserva = Reserva.builder()
                .id(1)
                .usuario(cliente)
                .espaco(espaco)
                .dataEvento(LocalDate.now().plusDays(7))
                .valorTotal(new BigDecimal("800.00"))
                .status(StatusReservaEnum.AGUARDANDO_SINAL)
                .dataCriacao(LocalDateTime.now())
                .build();

        requestDTO = ReservaRequestDTO.builder()
                .usuarioId(1)
                .espacoId(1)
                .dataEvento(LocalDate.now().plusDays(7))
                .valorTotal(new BigDecimal("800.00"))
                .observacoes("Evento corporativo")
                .build();

        responseDTO = ReservaResponseDTO.builder()
                .id(1)
                .dataEvento(LocalDate.now().plusDays(7))
                .valorTotal(new BigDecimal("800.00"))
                .status(StatusReservaEnum.AGUARDANDO_SINAL)
                .build();
    }

    @Test
    void shouldFindAllReservas() {
        // Given
        Reserva reserva2 = Reserva.builder()
                .id(2)
                .dataEvento(LocalDate.now().plusDays(10))
                .build();

        ReservaResponseDTO responseDTO2 = ReservaResponseDTO.builder()
                .id(2)
                .build();

        when(reservaRepository.findAll()).thenReturn(Arrays.asList(reserva, reserva2));
        when(reservaMapper.toResponseDTO(reserva)).thenReturn(responseDTO);
        when(reservaMapper.toResponseDTO(reserva2)).thenReturn(responseDTO2);

        // When
        List<ReservaResponseDTO> result = reservaService.findAll();

        // Then
        assertThat(result).hasSize(2);
        verify(reservaRepository, times(1)).findAll();
    }

    @Test
    void shouldFindAllReservas_WhenEmpty() {
        // Given
        when(reservaRepository.findAll()).thenReturn(Collections.emptyList());

        // When
        List<ReservaResponseDTO> result = reservaService.findAll();

        // Then
        assertThat(result).isEmpty();
        verify(reservaRepository, times(1)).findAll();
    }

    @Test
    void shouldFindReservaById() {
        // Given
        when(reservaRepository.findByIdWithDetails(1)).thenReturn(Optional.of(reserva));
        when(reservaMapper.toResponseDTO(reserva)).thenReturn(responseDTO);

        // When
        ReservaResponseDTO result = reservaService.findById(1);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getValorTotal()).isEqualByComparingTo(new BigDecimal("800.00"));
        verify(reservaRepository, times(1)).findByIdWithDetails(1);
    }

    @Test
    void shouldThrowExceptionWhenReservaNotFound() {
        // Given
        when(reservaRepository.findByIdWithDetails(999)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> reservaService.findById(999))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Reserva não encontrada com ID: 999");

        verify(reservaRepository, times(1)).findByIdWithDetails(999);
    }

    @Test
    void shouldFindByUsuarioId() {
        // Given
        when(reservaRepository.findByUsuarioId(1)).thenReturn(Arrays.asList(reserva));
        when(reservaMapper.toResponseDTO(reserva)).thenReturn(responseDTO);

        // When
        List<ReservaResponseDTO> result = reservaService.findByUsuarioId(1);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(1);
        verify(reservaRepository, times(1)).findByUsuarioId(1);
    }

    @Test
    void shouldFindByUsuarioId_WhenNoReservas() {
        // Given
        when(reservaRepository.findByUsuarioId(999)).thenReturn(Collections.emptyList());

        // When
        List<ReservaResponseDTO> result = reservaService.findByUsuarioId(999);

        // Then
        assertThat(result).isEmpty();
        verify(reservaRepository, times(1)).findByUsuarioId(999);
    }

    @Test
    void shouldFindByEspacoId() {
        // Given
        when(reservaRepository.findByEspacoId(1)).thenReturn(Arrays.asList(reserva));
        when(reservaMapper.toResponseDTO(reserva)).thenReturn(responseDTO);

        // When
        List<ReservaResponseDTO> result = reservaService.findByEspacoId(1);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(1);
        verify(reservaRepository, times(1)).findByEspacoId(1);
    }

    @Test
    void shouldFindByEspacoId_WhenNoReservas() {
        // Given
        when(reservaRepository.findByEspacoId(999)).thenReturn(Collections.emptyList());

        // When
        List<ReservaResponseDTO> result = reservaService.findByEspacoId(999);

        // Then
        assertThat(result).isEmpty();
        verify(reservaRepository, times(1)).findByEspacoId(999);
    }

    @Test
    void shouldCreateReserva() {
        // Given
        when(usuarioRepository.findById(1)).thenReturn(Optional.of(cliente));
        when(espacoRepository.findById(1)).thenReturn(Optional.of(espaco));
        doNothing().when(reservaValidator).validateEspacoAtivo(espaco);
        doNothing().when(reservaValidator).validateDisponibilidade(1, requestDTO.getDataEvento(), null);
        when(reservaMapper.toEntity(requestDTO, cliente, espaco)).thenReturn(reserva);
        when(reservaRepository.save(reserva)).thenReturn(reserva);
        when(reservaMapper.toResponseDTO(reserva)).thenReturn(responseDTO);

        // When
        ReservaResponseDTO result = reservaService.create(requestDTO);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(StatusReservaEnum.AGUARDANDO_SINAL);
        verify(usuarioRepository, times(1)).findById(1);
        verify(espacoRepository, times(1)).findById(1);
        verify(reservaValidator, times(1)).validateEspacoAtivo(espaco);
        verify(reservaValidator, times(1)).validateDisponibilidade(1, requestDTO.getDataEvento(), null);
        verify(reservaRepository, times(1)).save(reserva);
    }

    @Test
    void shouldThrowExceptionWhenCreateWithInvalidUsuario() {
        // Given
        when(usuarioRepository.findById(999)).thenReturn(Optional.empty());

        // When & Then
        ReservaRequestDTO invalidDTO = ReservaRequestDTO.builder()
                .usuarioId(999)
                .espacoId(requestDTO.getEspacoId())
                .dataEvento(requestDTO.getDataEvento())
                .valorTotal(requestDTO.getValorTotal())
                .build();

        assertThatThrownBy(() -> reservaService.create(invalidDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Usuário não encontrado com ID: 999");

        verify(usuarioRepository, times(1)).findById(999);
        verify(espacoRepository, never()).findById(any());
        verify(reservaRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenCreateWithInvalidEspaco() {
        // Given
        when(usuarioRepository.findById(1)).thenReturn(Optional.of(cliente));
        when(espacoRepository.findById(999)).thenReturn(Optional.empty());

        // When & Then
        ReservaRequestDTO invalidDTO = ReservaRequestDTO.builder()
                .usuarioId(requestDTO.getUsuarioId())
                .espacoId(999)
                .dataEvento(requestDTO.getDataEvento())
                .valorTotal(requestDTO.getValorTotal())
                .build();

        assertThatThrownBy(() -> reservaService.create(invalidDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Espaço não encontrado com ID: 999");

        verify(usuarioRepository, times(1)).findById(1);
        verify(espacoRepository, times(1)).findById(999);
        verify(reservaRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenCreateWithInactiveEspaco() {
        // Given
        espaco.setAtivo(false);
        when(usuarioRepository.findById(1)).thenReturn(Optional.of(cliente));
        when(espacoRepository.findById(1)).thenReturn(Optional.of(espaco));
        doThrow(new BusinessException("Espaço não está ativo para reservas"))
                .when(reservaValidator).validateEspacoAtivo(espaco);

        // When & Then
        assertThatThrownBy(() -> reservaService.create(requestDTO))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Espaço não está ativo");

        verify(reservaValidator, times(1)).validateEspacoAtivo(espaco);
        verify(reservaRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenCreateWithConflictingReserva() {
        // Given
        when(usuarioRepository.findById(1)).thenReturn(Optional.of(cliente));
        when(espacoRepository.findById(1)).thenReturn(Optional.of(espaco));
        doNothing().when(reservaValidator).validateEspacoAtivo(espaco);
        doThrow(new BusinessException("Espaço já possui reserva ativa para esta data"))
                .when(reservaValidator).validateDisponibilidade(1, requestDTO.getDataEvento(), null);

        // When & Then
        assertThatThrownBy(() -> reservaService.create(requestDTO))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Espaço já possui reserva");

        verify(reservaValidator, times(1)).validateDisponibilidade(1, requestDTO.getDataEvento(), null);
        verify(reservaRepository, never()).save(any());
    }

    @Test
    void shouldUpdateReserva_WithoutChangingEspacoOrData() {
        // Given
        ReservaRequestDTO updateDTO = ReservaRequestDTO.builder()
                .usuarioId(1)
                .espacoId(1)
                .dataEvento(reserva.getDataEvento())
                .valorTotal(new BigDecimal("900.00"))
                .observacoes("Observação atualizada")
                .status(StatusReservaEnum.CONFIRMADA)
                .build();

        when(reservaRepository.findById(1)).thenReturn(Optional.of(reserva));
        when(reservaRepository.save(reserva)).thenReturn(reserva);
        when(reservaMapper.toResponseDTO(reserva)).thenReturn(responseDTO);

        // When
        ReservaResponseDTO result = reservaService.update(1, updateDTO);

        // Then
        assertThat(result).isNotNull();
        verify(reservaRepository, times(1)).findById(1);
        verify(reservaValidator, never()).validateDisponibilidade(any(), any(), any());
        verify(reservaRepository, times(1)).save(reserva);
    }

    @Test
    void shouldUpdateReserva_WithChangingData() {
        // Given
        LocalDate novaData = LocalDate.now().plusDays(10);
        ReservaRequestDTO updateDTO = ReservaRequestDTO.builder()
                .usuarioId(1)
                .espacoId(1)
                .dataEvento(novaData)
                .valorTotal(new BigDecimal("900.00"))
                .observacoes("Data alterada")
                .build();

        when(reservaRepository.findById(1)).thenReturn(Optional.of(reserva));
        doNothing().when(reservaValidator).validateDisponibilidade(1, novaData, 1);
        when(reservaRepository.save(reserva)).thenReturn(reserva);
        when(reservaMapper.toResponseDTO(reserva)).thenReturn(responseDTO);

        // When
        ReservaResponseDTO result = reservaService.update(1, updateDTO);

        // Then
        assertThat(result).isNotNull();
        verify(reservaValidator, times(1)).validateDisponibilidade(1, novaData, 1);
        verify(reservaRepository, times(1)).save(reserva);
    }

    @Test
    void shouldUpdateReserva_WithChangingEspaco() {
        // Given
        ReservaRequestDTO updateDTO = ReservaRequestDTO.builder()
                .usuarioId(1)
                .espacoId(2)
                .dataEvento(reserva.getDataEvento())
                .valorTotal(new BigDecimal("900.00"))
                .observacoes("Espaço alterado")
                .build();

        when(reservaRepository.findById(1)).thenReturn(Optional.of(reserva));
        doNothing().when(reservaValidator).validateDisponibilidade(2, reserva.getDataEvento(), 1);
        when(reservaRepository.save(reserva)).thenReturn(reserva);
        when(reservaMapper.toResponseDTO(reserva)).thenReturn(responseDTO);

        // When
        ReservaResponseDTO result = reservaService.update(1, updateDTO);

        // Then
        assertThat(result).isNotNull();
        verify(reservaValidator, times(1)).validateDisponibilidade(2, reserva.getDataEvento(), 1);
        verify(reservaRepository, times(1)).save(reserva);
    }

    @Test
    void shouldThrowExceptionWhenUpdateNonExistentReserva() {
        // Given
        when(reservaRepository.findById(999)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> reservaService.update(999, requestDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Reserva não encontrada com ID: 999");

        verify(reservaRepository, times(1)).findById(999);
        verify(reservaRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenUpdateWithConflict() {
        // Given
        LocalDate novaData = LocalDate.now().plusDays(10);
        ReservaRequestDTO updateDTO = ReservaRequestDTO.builder()
                .usuarioId(1)
                .espacoId(1)
                .dataEvento(novaData)
                .valorTotal(new BigDecimal("900.00"))
                .build();

        when(reservaRepository.findById(1)).thenReturn(Optional.of(reserva));
        doThrow(new BusinessException("Espaço já possui reserva ativa para esta data"))
                .when(reservaValidator).validateDisponibilidade(1, novaData, 1);

        // When & Then
        assertThatThrownBy(() -> reservaService.update(1, updateDTO))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Espaço já possui reserva");

        verify(reservaValidator, times(1)).validateDisponibilidade(1, novaData, 1);
        verify(reservaRepository, never()).save(any());
    }

    @Test
    void shouldUpdateStatus() {
        // Given
        when(reservaRepository.findById(1)).thenReturn(Optional.of(reserva));
        when(reservaRepository.save(reserva)).thenReturn(reserva);
        when(reservaMapper.toResponseDTO(reserva)).thenReturn(responseDTO);

        // When
        ReservaResponseDTO result = reservaService.updateStatus(1, StatusReservaEnum.CONFIRMADA);

        // Then
        assertThat(result).isNotNull();
        verify(reservaRepository, times(1)).findById(1);
        verify(reservaRepository, times(1)).save(reserva);
    }

    @Test
    void shouldUpdateStatus_ToCancelled() {
        // Given
        when(reservaRepository.findById(1)).thenReturn(Optional.of(reserva));
        when(reservaRepository.save(reserva)).thenReturn(reserva);
        when(reservaMapper.toResponseDTO(reserva)).thenReturn(responseDTO);

        // When
        ReservaResponseDTO result = reservaService.updateStatus(1, StatusReservaEnum.CANCELADA);

        // Then
        assertThat(result).isNotNull();
        verify(reservaRepository, times(1)).findById(1);
        verify(reservaRepository, times(1)).save(reserva);
    }

    @Test
    void shouldThrowExceptionWhenUpdateStatusOfNonExistentReserva() {
        // Given
        when(reservaRepository.findById(999)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> reservaService.updateStatus(999, StatusReservaEnum.CONFIRMADA))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Reserva não encontrada com ID: 999");

        verify(reservaRepository, times(1)).findById(999);
        verify(reservaRepository, never()).save(any());
    }

    @Test
    void shouldDeleteReserva() {
        // Given
        when(reservaRepository.existsById(1)).thenReturn(true);
        doNothing().when(reservaRepository).deleteById(1);

        // When
        reservaService.delete(1);

        // Then
        verify(reservaRepository, times(1)).existsById(1);
        verify(reservaRepository, times(1)).deleteById(1);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistentReserva() {
        // Given
        when(reservaRepository.existsById(999)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> reservaService.delete(999))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Reserva não encontrada com ID: 999");

        verify(reservaRepository, times(1)).existsById(999);
        verify(reservaRepository, never()).deleteById(any());
    }
}
