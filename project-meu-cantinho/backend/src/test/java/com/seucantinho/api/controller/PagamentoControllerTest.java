package com.seucantinho.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seucantinho.api.domain.enums.TipoPagamentoEnum;
import com.seucantinho.api.dto.pagamento.PagamentoRequestDTO;
import com.seucantinho.api.dto.pagamento.PagamentoResponseDTO;
import com.seucantinho.api.exception.GlobalExceptionHandler;
import com.seucantinho.api.exception.ResourceNotFoundException;
import com.seucantinho.api.service.interfaces.IPagamentoService;
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
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class PagamentoControllerTest {

    @Mock
    private IPagamentoService pagamentoService;

    @InjectMocks
    private PagamentoController pagamentoController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(pagamentoController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
    }

    @Test
    void shouldFindAllPagamentos() throws Exception {
        // Given
        PagamentoResponseDTO pag1 = new PagamentoResponseDTO(1, LocalDateTime.now(), new BigDecimal("500.00"), TipoPagamentoEnum.SINAL, "PIX", "TRX123", 1);
        PagamentoResponseDTO pag2 = new PagamentoResponseDTO(2, LocalDateTime.now(), new BigDecimal("1500.00"), TipoPagamentoEnum.TOTAL, "CARTAO", "TRX456", 2);
        List<PagamentoResponseDTO> pagamentos = Arrays.asList(pag1, pag2);

        when(pagamentoService.findAll()).thenReturn(pagamentos);

        // When & Then
        mockMvc.perform(get("/api/pagamentos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].valor").value(500.00))
                .andExpect(jsonPath("$[0].tipo").value("SINAL"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].valor").value(1500.00))
                .andExpect(jsonPath("$[1].tipo").value("TOTAL"));

        verify(pagamentoService).findAll();
    }

    @Test
    void shouldFindAllPagamentos_WhenEmpty() throws Exception {
        // Given
        when(pagamentoService.findAll()).thenReturn(Collections.emptyList());

        // When & Then
        mockMvc.perform(get("/api/pagamentos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        verify(pagamentoService).findAll();
    }

    @Test
    void shouldFindPagamentoById() throws Exception {
        // Given
        PagamentoResponseDTO pagamento = new PagamentoResponseDTO(1, LocalDateTime.now(), new BigDecimal("500.00"), TipoPagamentoEnum.SINAL, "PIX", "TRX123", 1);
        when(pagamentoService.findById(1)).thenReturn(pagamento);

        // When & Then
        mockMvc.perform(get("/api/pagamentos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.valor").value(500.00))
                .andExpect(jsonPath("$.tipo").value("SINAL"))
                .andExpect(jsonPath("$.formaPagamento").value("PIX"))
                .andExpect(jsonPath("$.reservaId").value(1));

        verify(pagamentoService).findById(1);
    }

    @Test
    void shouldReturn404_WhenPagamentoNotFound() throws Exception {
        // Given
        when(pagamentoService.findById(999)).thenThrow(new ResourceNotFoundException("Pagamento não encontrado"));

        // When & Then
        mockMvc.perform(get("/api/pagamentos/999"))
                .andExpect(status().isNotFound());

        verify(pagamentoService).findById(999);
    }

    @Test
    void shouldFindPagamentosByReservaId() throws Exception {
        // Given
        PagamentoResponseDTO pagamento = new PagamentoResponseDTO(1, LocalDateTime.now(), new BigDecimal("500.00"), TipoPagamentoEnum.SINAL, "PIX", "TRX123", 1);
        when(pagamentoService.findByReservaId(1)).thenReturn(Arrays.asList(pagamento));

        // When & Then
        mockMvc.perform(get("/api/pagamentos/reserva/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].reservaId").value(1))
                .andExpect(jsonPath("$[0].valor").value(500.00));

        verify(pagamentoService).findByReservaId(1);
    }

    @Test
    void shouldCreatePagamento() throws Exception {
        // Given
        PagamentoRequestDTO requestDTO = new PagamentoRequestDTO(new BigDecimal("500.00"), TipoPagamentoEnum.SINAL, "PIX", "TRX123", 1);
        PagamentoResponseDTO responseDTO = new PagamentoResponseDTO(1, LocalDateTime.now(), new BigDecimal("500.00"), TipoPagamentoEnum.SINAL, "PIX", "TRX123", 1);

        when(pagamentoService.create(any(PagamentoRequestDTO.class))).thenReturn(responseDTO);

        // When & Then
        mockMvc.perform(post("/api/pagamentos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.valor").value(500.00))
                .andExpect(jsonPath("$.tipo").value("SINAL"))
                .andExpect(jsonPath("$.formaPagamento").value("PIX"))
                .andExpect(jsonPath("$.reservaId").value(1));

        verify(pagamentoService).create(any(PagamentoRequestDTO.class));
    }

    @Test
    void shouldReturn400_WhenCreateWithInvalidData() throws Exception {
        // Given - valor nulo
        PagamentoRequestDTO requestDTO = new PagamentoRequestDTO(null, TipoPagamentoEnum.SINAL, "PIX", "TRX123", 1);

        // When & Then
        mockMvc.perform(post("/api/pagamentos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest());

        verify(pagamentoService, never()).create(any(PagamentoRequestDTO.class));
    }
}
