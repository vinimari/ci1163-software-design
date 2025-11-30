package com.seucantinho.api.feature.reserva.application.service;

import com.seucantinho.api.feature.administrador.domain.Administrador;
import com.seucantinho.api.feature.cliente.domain.Cliente;
import com.seucantinho.api.feature.espaco.domain.Espaco;
import com.seucantinho.api.feature.espaco.domain.port.out.EspacoRepositoryPort;
import com.seucantinho.api.feature.espaco.domain.valueobject.Capacidade;
import com.seucantinho.api.feature.filial.domain.Filial;
import com.seucantinho.api.feature.funcionario.domain.Funcionario;
import com.seucantinho.api.feature.reserva.application.dto.ReservaRequestDTO;
import com.seucantinho.api.feature.reserva.application.dto.ReservaResponseDTO;
import com.seucantinho.api.feature.reserva.domain.Reserva;
import com.seucantinho.api.feature.reserva.domain.enums.StatusReservaEnum;
import com.seucantinho.api.feature.reserva.domain.port.out.ReservaRepositoryPort;
import com.seucantinho.api.feature.reserva.domain.service.ReservaAvailabilityService;
import com.seucantinho.api.feature.reserva.domain.valueobject.DataEvento;
import com.seucantinho.api.feature.reserva.infrastructure.mapper.ReservaMapper;
import com.seucantinho.api.feature.usuario.domain.Usuario;
import com.seucantinho.api.feature.usuario.domain.enums.PerfilUsuarioEnum;
import com.seucantinho.api.feature.usuario.domain.port.out.UsuarioRepositoryPort;
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
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do ReservaService")
class ReservaServiceTest {

    @Mock
    private ReservaRepositoryPort reservaRepositoryPort;

    @Mock
    private UsuarioRepositoryPort usuarioRepositoryPort;

    @Mock
    private EspacoRepositoryPort espacoRepositoryPort;

    @Mock
    private ReservaMapper reservaMapper;

    @Mock
    private ReservaAvailabilityService reservaAvailabilityService;

    @Mock
    private ReservaStatusService reservaStatusService;

    @InjectMocks
    private ReservaService reservaService;

    private Reserva reserva;
    private ReservaRequestDTO requestDTO;
    private ReservaResponseDTO responseDTO;
    private Cliente cliente;
    private Espaco espaco;
    private Filial filial;

    @BeforeEach
    void setUp() {
        filial = criarFilial();
        cliente = criarCliente();
        espaco = criarEspaco();
        reserva = criarReserva();
        requestDTO = criarRequestDTO();
        responseDTO = criarResponseDTO();
    }

    @Test
    @DisplayName("Deve retornar todas as reservas")
    void deveRetornarTodasAsReservas() {
        // Arrange
        List<Reserva> reservas = Arrays.asList(reserva);
        when(reservaRepositoryPort.findAll()).thenReturn(reservas);
        when(reservaMapper.toResponseDTO(any(Reserva.class))).thenReturn(responseDTO);

        // Act
        List<ReservaResponseDTO> resultado = reservaService.findAll();

        // Assert
        assertThat(resultado).hasSize(1);
        verify(reservaRepositoryPort).findAll();
        verify(reservaMapper).toResponseDTO(any(Reserva.class));
    }

    @Test
    @DisplayName("Deve retornar reserva por ID")
    void deveRetornarReservaPorId() {
        // Arrange
        Integer id = 1;
        when(reservaRepositoryPort.findById(id)).thenReturn(Optional.of(reserva));
        when(reservaMapper.toResponseDTO(reserva)).thenReturn(responseDTO);

        // Act
        ReservaResponseDTO resultado = reservaService.findById(id);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado).isEqualTo(responseDTO);
        verify(reservaRepositoryPort).findById(id);
        verify(reservaMapper).toResponseDTO(reserva);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar reserva por ID inexistente")
    void deveLancarExcecaoAoBuscarReservaPorIdInexistente() {
        // Arrange
        Integer id = 999;
        when(reservaRepositoryPort.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> reservaService.findById(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Reserva não encontrada com ID: " + id);
    }

    @Test
    @DisplayName("Deve retornar reservas por usuário ID")
    void deveRetornarReservasPorUsuarioId() {
        // Arrange
        Integer usuarioId = 1;
        List<Reserva> reservas = Arrays.asList(reserva);
        when(reservaRepositoryPort.findByUsuarioId(usuarioId)).thenReturn(reservas);
        when(reservaMapper.toResponseDTO(any(Reserva.class))).thenReturn(responseDTO);

        // Act
        List<ReservaResponseDTO> resultado = reservaService.findByUsuarioId(usuarioId);

        // Assert
        assertThat(resultado).hasSize(1);
        verify(reservaRepositoryPort).findByUsuarioId(usuarioId);
    }

    @Test
    @DisplayName("Deve retornar reservas por espaço ID")
    void deveRetornarReservasPorEspacoId() {
        // Arrange
        Integer espacoId = 1;
        List<Reserva> reservas = Arrays.asList(reserva);
        when(reservaRepositoryPort.findByEspacoId(espacoId)).thenReturn(reservas);
        when(reservaMapper.toResponseDTO(any(Reserva.class))).thenReturn(responseDTO);

        // Act
        List<ReservaResponseDTO> resultado = reservaService.findByEspacoId(espacoId);

        // Assert
        assertThat(resultado).hasSize(1);
        verify(reservaRepositoryPort).findByEspacoId(espacoId);
    }

    @Test
    @DisplayName("Deve criar reserva com sucesso")
    void deveCriarReservaComSucesso() {
        // Arrange
        when(usuarioRepositoryPort.findById(requestDTO.getUsuarioId())).thenReturn(Optional.of(cliente));
        when(espacoRepositoryPort.findById(requestDTO.getEspacoId())).thenReturn(Optional.of(espaco));
        when(reservaMapper.toEntity(requestDTO, cliente, espaco)).thenReturn(reserva);
        doNothing().when(reservaAvailabilityService).validarDisponibilidade(anyInt(), any(LocalDate.class), any());
        when(reservaRepositoryPort.save(reserva)).thenReturn(reserva);
        when(reservaMapper.toResponseDTO(reserva)).thenReturn(responseDTO);

        // Act
        ReservaResponseDTO resultado = reservaService.create(requestDTO);

        // Assert
        assertThat(resultado).isNotNull();
        verify(usuarioRepositoryPort).findById(requestDTO.getUsuarioId());
        verify(espacoRepositoryPort).findById(requestDTO.getEspacoId());
        verify(reservaAvailabilityService).validarDisponibilidade(requestDTO.getEspacoId(), requestDTO.getDataEvento(), null);
        verify(reservaRepositoryPort).save(reserva);
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar reserva com usuário inexistente")
    void deveLancarExcecaoAoCriarReservaComUsuarioInexistente() {
        // Arrange
        when(usuarioRepositoryPort.findById(requestDTO.getUsuarioId())).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> reservaService.create(requestDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Usuário não encontrado");
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar reserva com espaço inexistente")
    void deveLancarExcecaoAoCriarReservaComEspacoInexistente() {
        // Arrange
        when(usuarioRepositoryPort.findById(requestDTO.getUsuarioId())).thenReturn(Optional.of(cliente));
        when(espacoRepositoryPort.findById(requestDTO.getEspacoId())).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> reservaService.create(requestDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Espaço não encontrado");
    }

    @Test
    @DisplayName("Deve atualizar reserva com sucesso")
    void deveAtualizarReservaComSucesso() {
        // Arrange
        Integer id = 1;
        when(reservaRepositoryPort.findById(id)).thenReturn(Optional.of(reserva));
        doNothing().when(reservaAvailabilityService).validarDisponibilidade(anyInt(), any(LocalDate.class), anyInt());
        when(reservaRepositoryPort.save(reserva)).thenReturn(reserva);
        when(reservaMapper.toResponseDTO(reserva)).thenReturn(responseDTO);

        ReservaRequestDTO updateDTO = criarRequestDTO();
        updateDTO.setDataEvento(LocalDate.now().plusDays(20));

        // Act
        ReservaResponseDTO resultado = reservaService.update(id, updateDTO);

        // Assert
        assertThat(resultado).isNotNull();
        verify(reservaRepositoryPort).findById(id);
        verify(reservaAvailabilityService).validarDisponibilidade(anyInt(), any(LocalDate.class), eq(id));
        verify(reservaRepositoryPort).save(reserva);
    }

    @Test
    @DisplayName("Deve atualizar status da reserva para cancelada")
    void deveAtualizarStatusDaReservaParaCancelada() {
        // Arrange
        Integer id = 1;
        when(reservaRepositoryPort.findById(id)).thenReturn(Optional.of(reserva));
        doNothing().when(reservaStatusService).cancelReservation(reserva);
        when(reservaRepositoryPort.save(reserva)).thenReturn(reserva);
        when(reservaMapper.toResponseDTO(reserva)).thenReturn(responseDTO);

        // Act
        ReservaResponseDTO resultado = reservaService.updateStatus(id, StatusReservaEnum.CANCELADA);

        // Assert
        assertThat(resultado).isNotNull();
        verify(reservaStatusService).cancelReservation(reserva);
        verify(reservaRepositoryPort).save(reserva);
    }

    @Test
    @DisplayName("Deve atualizar status da reserva para outro status que não cancelada")
    void deveAtualizarStatusDaReservaParaOutroStatus() {
        // Arrange
        Integer id = 1;
        when(reservaRepositoryPort.findById(id)).thenReturn(Optional.of(reserva));
        when(reservaRepositoryPort.save(reserva)).thenReturn(reserva);
        when(reservaMapper.toResponseDTO(reserva)).thenReturn(responseDTO);

        // Act
        ReservaResponseDTO resultado = reservaService.updateStatus(id, StatusReservaEnum.CONFIRMADA);

        // Assert
        assertThat(resultado).isNotNull();
        verify(reservaRepositoryPort).findById(id);
        verify(reservaStatusService, never()).cancelReservation(any());
        verify(reservaRepositoryPort).save(reserva);
    }

    @Test
    @DisplayName("Deve deletar reserva com sucesso")
    void deveDeletarReservaComSucesso() {
        // Arrange
        Integer id = 1;
        when(reservaRepositoryPort.existsById(id)).thenReturn(true);
        doNothing().when(reservaRepositoryPort).deleteById(id);

        // Act
        reservaService.delete(id);

        // Assert
        verify(reservaRepositoryPort).existsById(id);
        verify(reservaRepositoryPort).deleteById(id);
    }

    @Test
    @DisplayName("Deve lançar exceção ao deletar reserva inexistente")
    void deveLancarExcecaoAoDeletarReservaInexistente() {
        // Arrange
        Integer id = 999;
        when(reservaRepositoryPort.existsById(id)).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> reservaService.delete(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Reserva não encontrada com ID: " + id);
    }

    @Test
    @DisplayName("Deve retornar todas reservas para administrador")
    void deveRetornarTodasReservasParaAdministrador() {
        // Arrange
        Administrador admin = new Administrador();
        admin.setId(2);
        admin.setNome("Admin Teste");
        admin.setEmail("admin@email.com");
        admin.setSenhaHash("hash");
        
        when(usuarioRepositoryPort.findByEmail("admin@email.com")).thenReturn(Optional.of(admin));
        List<Reserva> reservas = Arrays.asList(reserva);
        when(reservaRepositoryPort.findAll()).thenReturn(reservas);
        when(reservaMapper.toResponseDTO(any(Reserva.class))).thenReturn(responseDTO);

        // Act
        List<ReservaResponseDTO> resultado = reservaService.findByAcessoPorEmail("admin@email.com");

        // Assert
        assertThat(resultado).hasSize(1);
        verify(reservaRepositoryPort).findAll();
        verify(reservaMapper).toResponseDTO(any(Reserva.class));
    }

    @Test
    @DisplayName("Deve retornar reservas da filial para funcionário")
    void deveRetornarReservasDaFilialParaFuncionario() {
        // Arrange
        Funcionario funcionario = new Funcionario();
        funcionario.setId(2);
        funcionario.setNome("Funcionário Teste");
        funcionario.setEmail("func@email.com");
        funcionario.setSenhaHash("hash");
        funcionario.setFilial(filial);

        when(usuarioRepositoryPort.findByEmail("func@email.com")).thenReturn(Optional.of(funcionario));
        when(reservaRepositoryPort.findAll()).thenReturn(Arrays.asList(reserva));
        when(reservaMapper.toResponseDTO(any(Reserva.class))).thenReturn(responseDTO);

        // Act
        List<ReservaResponseDTO> resultado = reservaService.findByAcessoPorEmail("func@email.com");

        // Assert
        assertThat(resultado).hasSize(1);
        verify(reservaRepositoryPort).findAll();
    }

    @Test
    @DisplayName("Deve retornar lista vazia para funcionário sem filial")
    void deveRetornarListaVaziaParaFuncionarioSemFilial() {
        // Arrange
        Funcionario funcionario = new Funcionario();
        funcionario.setId(2);
        funcionario.setNome("Funcionário Teste");
        funcionario.setEmail("func@email.com");
        funcionario.setSenhaHash("hash");
        funcionario.setFilial(null);

        when(usuarioRepositoryPort.findByEmail("func@email.com")).thenReturn(Optional.of(funcionario));

        // Act
        List<ReservaResponseDTO> resultado = reservaService.findByAcessoPorEmail("func@email.com");

        // Assert
        assertThat(resultado).isEmpty();
    }

    @Test
    @DisplayName("Deve retornar reservas do próprio cliente")
    void deveRetornarReservasDoProprioCliente() {
        // Arrange
        when(usuarioRepositoryPort.findByEmail("cliente@email.com")).thenReturn(Optional.of(cliente));
        when(reservaRepositoryPort.findByUsuarioId(cliente.getId())).thenReturn(Arrays.asList(reserva));
        when(reservaMapper.toResponseDTO(any(Reserva.class))).thenReturn(responseDTO);

        // Act
        List<ReservaResponseDTO> resultado = reservaService.findByAcessoPorEmail("cliente@email.com");

        // Assert
        assertThat(resultado).hasSize(1);
        verify(reservaRepositoryPort).findByUsuarioId(cliente.getId());
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar por email inexistente")
    void deveLancarExcecaoAoBuscarPorEmailInexistente() {
        // Arrange
        when(usuarioRepositoryPort.findByEmail("naoexiste@email.com")).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> reservaService.findByAcessoPorEmail("naoexiste@email.com"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Usuário não encontrado com email");
    }

    // Métodos auxiliares
    private Filial criarFilial() {
        return Filial.builder()
                .id(1)
                .nome("Filial Centro")
                .cidade("Curitiba")
                .estado("PR")
                .build();
    }

    private Cliente criarCliente() {
        return Cliente.builder()
                .id(1)
                .nome("João Silva")
                .email("cliente@email.com")
                .senhaHash("hash123")
                .perfil(PerfilUsuarioEnum.CLIENTE)
                .build();
    }

    private Espaco criarEspaco() {
        return Espaco.builder()
                .id(1)
                .nome("Salão de Eventos")
                .capacidade(Capacidade.of(50))
                .precoDiaria(ValorMonetario.of("300.00"))
                .filial(filial)
                .ativo(true)
                .build();
    }

    private Reserva criarReserva() {
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

    private ReservaRequestDTO criarRequestDTO() {
        return ReservaRequestDTO.builder()
                .usuarioId(1)
                .espacoId(1)
                .dataEvento(LocalDate.now().plusDays(10))
                .valorTotal(new BigDecimal("300.00"))
                .observacoes("Teste")
                .build();
    }

    private ReservaResponseDTO criarResponseDTO() {
        return ReservaResponseDTO.builder()
                .id(1)
                .dataEvento(LocalDate.now().plusDays(10))
                .valorTotal(new BigDecimal("300.00"))
                .status(StatusReservaEnum.AGUARDANDO_SINAL)
                .build();
    }
}
