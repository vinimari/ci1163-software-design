package com.seucantinho.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seucantinho.api.domain.enums.PerfilUsuarioEnum;
import com.seucantinho.api.domain.enums.StatusReservaEnum;
import com.seucantinho.api.dto.espaco.EspacoResponseDTO;
import com.seucantinho.api.dto.filial.FilialResponseDTO;
import com.seucantinho.api.dto.reserva.ReservaRequestDTO;
import com.seucantinho.api.dto.reserva.ReservaResponseDTO;
import com.seucantinho.api.dto.usuario.ClienteResponseDTO;
import com.seucantinho.api.exception.GlobalExceptionHandler;
import com.seucantinho.api.exception.ResourceNotFoundException;
import com.seucantinho.api.service.interfaces.IReservaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ReservaControllerTest {

    @Mock
    private IReservaService reservaService;

    @InjectMocks
    private ReservaController reservaController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private ClienteResponseDTO clienteDTO;
    private EspacoResponseDTO espacoDTO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(reservaController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

        clienteDTO = ClienteResponseDTO.builder()
                .id(1)
                .nome("João Silva")
                .email("joao@email.com")
                .cpf("12345678900")
                .telefone("41999998888")
                .ativo(true)
                .dataCadastro(LocalDateTime.now())
                .quantidadeReservas(0)
                .build();

        FilialResponseDTO filialDTO = new FilialResponseDTO(1, "Filial Centro", "Curitiba", "PR", "Rua A, 123", "41999998888", null, 0);
        espacoDTO = new EspacoResponseDTO(1, "Salão Principal", "Espaço amplo", 100, new BigDecimal("500.00"), true, "foto1.jpg", filialDTO);
    }

    @Test
    void shouldFindAllReservas() throws Exception {
        // Given
        ReservaResponseDTO res1 = ReservaResponseDTO.builder()
                .id(1)
                .dataCriacao(LocalDateTime.now())
                .dataEvento(LocalDate.now().plusDays(30))
                .valorTotal(new BigDecimal("500.00"))
                .status(StatusReservaEnum.AGUARDANDO_SINAL)
                .usuario(clienteDTO)
                .espaco(espacoDTO)
                .totalPago(BigDecimal.ZERO)
                .saldo(new BigDecimal("500.00"))
                .build();

        ReservaResponseDTO res2 = ReservaResponseDTO.builder()
                .id(2)
                .dataCriacao(LocalDateTime.now())
                .dataEvento(LocalDate.now().plusDays(60))
                .valorTotal(new BigDecimal("1000.00"))
                .status(StatusReservaEnum.CONFIRMADA)
                .usuario(clienteDTO)
                .espaco(espacoDTO)
                .totalPago(new BigDecimal("500.00"))
                .saldo(new BigDecimal("500.00"))
                .build();

        List<ReservaResponseDTO> reservas = Arrays.asList(res1, res2);

        when(reservaService.findAll()).thenReturn(reservas);

        // When & Then
        mockMvc.perform(get("/api/reservas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].valorTotal").value(500.00))
                .andExpect(jsonPath("$[0].status").value("AGUARDANDO_SINAL"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].status").value("CONFIRMADA"));

        verify(reservaService).findAll();
    }

    @Test
    void shouldFindAllReservas_WhenEmpty() throws Exception {
        // Given
        when(reservaService.findAll()).thenReturn(Collections.emptyList());

        // When & Then
        mockMvc.perform(get("/api/reservas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        verify(reservaService).findAll();
    }

    @Test
    void shouldFindReservaById() throws Exception {
        // Given
        ReservaResponseDTO reserva = ReservaResponseDTO.builder()
                .id(1)
                .dataCriacao(LocalDateTime.now())
                .dataEvento(LocalDate.now().plusDays(30))
                .valorTotal(new BigDecimal("500.00"))
                .status(StatusReservaEnum.AGUARDANDO_SINAL)
                .usuario(clienteDTO)
                .espaco(espacoDTO)
                .totalPago(BigDecimal.ZERO)
                .saldo(new BigDecimal("500.00"))
                .build();

        when(reservaService.findById(1)).thenReturn(reserva);

        // When & Then
        mockMvc.perform(get("/api/reservas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.valorTotal").value(500.00))
                .andExpect(jsonPath("$.status").value("AGUARDANDO_SINAL"))
                .andExpect(jsonPath("$.usuario.id").value(1))
                .andExpect(jsonPath("$.espaco.id").value(1));

        verify(reservaService).findById(1);
    }

    @Test
    void shouldReturn404_WhenReservaNotFound() throws Exception {
        // Given
        when(reservaService.findById(999)).thenThrow(new ResourceNotFoundException("Reserva não encontrada"));

        // When & Then
        mockMvc.perform(get("/api/reservas/999"))
                .andExpect(status().isNotFound());

        verify(reservaService).findById(999);
    }

    @Test
    void shouldFindReservasByUsuarioId() throws Exception {
        // Given
        ReservaResponseDTO reserva = ReservaResponseDTO.builder()
                .id(1)
                .dataCriacao(LocalDateTime.now())
                .dataEvento(LocalDate.now().plusDays(30))
                .valorTotal(new BigDecimal("500.00"))
                .status(StatusReservaEnum.AGUARDANDO_SINAL)
                .usuario(clienteDTO)
                .espaco(espacoDTO)
                .totalPago(BigDecimal.ZERO)
                .saldo(new BigDecimal("500.00"))
                .build();

        when(reservaService.findByUsuarioId(1)).thenReturn(Arrays.asList(reserva));

        // When & Then
        mockMvc.perform(get("/api/reservas/usuario/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].usuario.id").value(1));

        verify(reservaService).findByUsuarioId(1);
    }

    @Test
    void shouldFindReservasByEspacoId() throws Exception {
        // Given
        ReservaResponseDTO reserva = ReservaResponseDTO.builder()
                .id(1)
                .dataCriacao(LocalDateTime.now())
                .dataEvento(LocalDate.now().plusDays(30))
                .valorTotal(new BigDecimal("500.00"))
                .status(StatusReservaEnum.AGUARDANDO_SINAL)
                .usuario(clienteDTO)
                .espaco(espacoDTO)
                .totalPago(BigDecimal.ZERO)
                .saldo(new BigDecimal("500.00"))
                .build();

        when(reservaService.findByEspacoId(1)).thenReturn(Arrays.asList(reserva));

        // When & Then
        mockMvc.perform(get("/api/reservas/espaco/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].espaco.id").value(1));

        verify(reservaService).findByEspacoId(1);
    }

    @Test
    void shouldCreateReserva() throws Exception {
        // Given
        LocalDate futureDate = LocalDate.now().plusDays(30);
        ReservaRequestDTO requestDTO = new ReservaRequestDTO(futureDate, new BigDecimal("500.00"), "Observação", StatusReservaEnum.AGUARDANDO_SINAL, 1, 1);

        ReservaResponseDTO responseDTO = ReservaResponseDTO.builder()
                .id(1)
                .dataCriacao(LocalDateTime.now())
                .dataEvento(futureDate)
                .valorTotal(new BigDecimal("500.00"))
                .observacoes("Observação")
                .status(StatusReservaEnum.AGUARDANDO_SINAL)
                .usuario(clienteDTO)
                .espaco(espacoDTO)
                .totalPago(BigDecimal.ZERO)
                .saldo(new BigDecimal("500.00"))
                .build();

        when(reservaService.create(any(ReservaRequestDTO.class))).thenReturn(responseDTO);

        // When & Then
        mockMvc.perform(post("/api/reservas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.valorTotal").value(500.00))
                .andExpect(jsonPath("$.status").value("AGUARDANDO_SINAL"));

        verify(reservaService).create(any(ReservaRequestDTO.class));
    }

    @Test
    void shouldReturn400_WhenCreateWithInvalidData() throws Exception {
        // Given - valorTotal nulo
        LocalDate futureDate = LocalDate.now().plusDays(30);
        ReservaRequestDTO requestDTO = new ReservaRequestDTO(futureDate, null, "Observação", StatusReservaEnum.AGUARDANDO_SINAL, 1, 1);

        // When & Then
        mockMvc.perform(post("/api/reservas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest());

        verify(reservaService, never()).create(any(ReservaRequestDTO.class));
    }

    @Test
    void shouldUpdateReserva() throws Exception {
        // Given
        LocalDate futureDate = LocalDate.now().plusDays(30);
        ReservaRequestDTO requestDTO = new ReservaRequestDTO(futureDate, new BigDecimal("600.00"), "Nova observação", StatusReservaEnum.CONFIRMADA, 1, 1);

        ReservaResponseDTO responseDTO = ReservaResponseDTO.builder()
                .id(1)
                .dataCriacao(LocalDateTime.now())
                .dataEvento(futureDate)
                .valorTotal(new BigDecimal("600.00"))
                .observacoes("Nova observação")
                .status(StatusReservaEnum.CONFIRMADA)
                .usuario(clienteDTO)
                .espaco(espacoDTO)
                .totalPago(BigDecimal.ZERO)
                .saldo(new BigDecimal("600.00"))
                .build();

        when(reservaService.update(eq(1), any(ReservaRequestDTO.class))).thenReturn(responseDTO);

        // When & Then
        mockMvc.perform(put("/api/reservas/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.valorTotal").value(600.00))
                .andExpect(jsonPath("$.status").value("CONFIRMADA"));

        verify(reservaService).update(eq(1), any(ReservaRequestDTO.class));
    }

    @Test
    void shouldReturn404_WhenUpdateNonExistentReserva() throws Exception {
        // Given
        LocalDate futureDate = LocalDate.now().plusDays(30);
        ReservaRequestDTO requestDTO = new ReservaRequestDTO(futureDate, new BigDecimal("600.00"), "Nova observação", StatusReservaEnum.CONFIRMADA, 1, 1);

        when(reservaService.update(eq(999), any(ReservaRequestDTO.class)))
                .thenThrow(new ResourceNotFoundException("Reserva não encontrada"));

        // When & Then
        mockMvc.perform(put("/api/reservas/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isNotFound());

        verify(reservaService).update(eq(999), any(ReservaRequestDTO.class));
    }

    @Test
    void shouldUpdateReservaStatus() throws Exception {
        // Given
        ReservaResponseDTO responseDTO = ReservaResponseDTO.builder()
                .id(1)
                .dataCriacao(LocalDateTime.now())
                .dataEvento(LocalDate.now().plusDays(30))
                .valorTotal(new BigDecimal("500.00"))
                .status(StatusReservaEnum.CANCELADA)
                .usuario(clienteDTO)
                .espaco(espacoDTO)
                .totalPago(BigDecimal.ZERO)
                .saldo(new BigDecimal("500.00"))
                .build();

        when(reservaService.updateStatus(1, StatusReservaEnum.CANCELADA)).thenReturn(responseDTO);

        // When & Then
        mockMvc.perform(patch("/api/reservas/1/status")
                        .param("status", "CANCELADA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("CANCELADA"));

        verify(reservaService).updateStatus(1, StatusReservaEnum.CANCELADA);
    }

    @Test
    void shouldReturn404_WhenUpdateStatusOfNonExistentReserva() throws Exception {
        // Given
        when(reservaService.updateStatus(999, StatusReservaEnum.CANCELADA))
                .thenThrow(new ResourceNotFoundException("Reserva não encontrada"));

        // When & Then
        mockMvc.perform(patch("/api/reservas/999/status")
                        .param("status", "CANCELADA"))
                .andExpect(status().isNotFound());

        verify(reservaService).updateStatus(999, StatusReservaEnum.CANCELADA);
    }

    @Test
    void shouldDeleteReserva() throws Exception {
        // Given
        doNothing().when(reservaService).delete(1);

        // When & Then
        mockMvc.perform(delete("/api/reservas/1"))
                .andExpect(status().isNoContent());

        verify(reservaService).delete(1);
    }

    @Test
    void shouldReturn404_WhenDeleteNonExistentReserva() throws Exception {
        // Given
        doThrow(new ResourceNotFoundException("Reserva não encontrada")).when(reservaService).delete(999);

        // When & Then
        mockMvc.perform(delete("/api/reservas/999"))
                .andExpect(status().isNotFound());

        verify(reservaService).delete(999);
    }
}
