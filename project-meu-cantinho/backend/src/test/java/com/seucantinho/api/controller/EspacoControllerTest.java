package com.seucantinho.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seucantinho.api.dto.espaco.EspacoRequestDTO;
import com.seucantinho.api.dto.espaco.EspacoResponseDTO;
import com.seucantinho.api.dto.filial.FilialResponseDTO;
import com.seucantinho.api.exception.GlobalExceptionHandler;
import com.seucantinho.api.exception.ResourceNotFoundException;
import com.seucantinho.api.service.interfaces.IEspacoService;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class EspacoControllerTest {

    @Mock
    private IEspacoService espacoService;

    @InjectMocks
    private EspacoController espacoController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private FilialResponseDTO filialDTO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(espacoController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

        filialDTO = new FilialResponseDTO(1, "Filial Centro", "Curitiba", "PR", "Rua A, 123", "41999998888", null, 0);
    }

    @Test
    void shouldFindAllEspacos() throws Exception {
        // Given
        EspacoResponseDTO espaco1 = new EspacoResponseDTO(1, "Salão Principal", "Espaço amplo", 100, new BigDecimal("500.00"), true, "foto1.jpg", filialDTO);
        EspacoResponseDTO espaco2 = new EspacoResponseDTO(2, "Sala Pequena", "Espaço intimista", 30, new BigDecimal("200.00"), true, "foto2.jpg", filialDTO);
        List<EspacoResponseDTO> espacos = Arrays.asList(espaco1, espaco2);

        when(espacoService.findAll()).thenReturn(espacos);

        // When & Then
        mockMvc.perform(get("/api/espacos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nome").value("Salão Principal"))
                .andExpect(jsonPath("$[0].capacidade").value(100))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].nome").value("Sala Pequena"));

        verify(espacoService).findAll();
    }

    @Test
    void shouldFindAllEspacos_WhenEmpty() throws Exception {
        // Given
        when(espacoService.findAll()).thenReturn(Collections.emptyList());

        // When & Then
        mockMvc.perform(get("/api/espacos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        verify(espacoService).findAll();
    }

    @Test
    void shouldFindEspacoById() throws Exception {
        // Given
        EspacoResponseDTO espaco = new EspacoResponseDTO(1, "Salão Principal", "Espaço amplo", 100, new BigDecimal("500.00"), true, "foto1.jpg", filialDTO);
        when(espacoService.findById(1)).thenReturn(espaco);

        // When & Then
        mockMvc.perform(get("/api/espacos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Salão Principal"))
                .andExpect(jsonPath("$.capacidade").value(100))
                .andExpect(jsonPath("$.precoDiaria").value(500.00))
                .andExpect(jsonPath("$.ativo").value(true));

        verify(espacoService).findById(1);
    }

    @Test
    void shouldReturn404_WhenEspacoNotFound() throws Exception {
        // Given
        when(espacoService.findById(999)).thenThrow(new ResourceNotFoundException("Espaço não encontrado"));

        // When & Then
        mockMvc.perform(get("/api/espacos/999"))
                .andExpect(status().isNotFound());

        verify(espacoService).findById(999);
    }

    @Test
    void shouldFindEspacosByFilialId() throws Exception {
        // Given
        EspacoResponseDTO espaco = new EspacoResponseDTO(1, "Salão Principal", "Espaço amplo", 100, new BigDecimal("500.00"), true, "foto1.jpg", filialDTO);
        when(espacoService.findByFilialId(1)).thenReturn(Arrays.asList(espaco));

        // When & Then
        mockMvc.perform(get("/api/espacos/filial/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nome").value("Salão Principal"));

        verify(espacoService).findByFilialId(1);
    }

    @Test
    void shouldFindEspacosAtivos() throws Exception {
        // Given
        EspacoResponseDTO espaco = new EspacoResponseDTO(1, "Salão Principal", "Espaço amplo", 100, new BigDecimal("500.00"), true, "foto1.jpg", filialDTO);
        when(espacoService.findAtivos()).thenReturn(Arrays.asList(espaco));

        // When & Then
        mockMvc.perform(get("/api/espacos/ativos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].ativo").value(true));

        verify(espacoService).findAtivos();
    }

    @Test
    void shouldFindEspacosDisponiveis() throws Exception {
        // Given
        LocalDate data = LocalDate.of(2024, 12, 25);
        EspacoResponseDTO espaco = new EspacoResponseDTO(1, "Salão Principal", "Espaço amplo", 100, new BigDecimal("500.00"), true, "foto1.jpg", filialDTO);
        when(espacoService.findDisponiveisPorData(data, 1)).thenReturn(Arrays.asList(espaco));

        // When & Then
        mockMvc.perform(get("/api/espacos/disponiveis")
                        .param("data", "2024-12-25")
                        .param("capacidadeMinima", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1));

        verify(espacoService).findDisponiveisPorData(data, 1);
    }

    @Test
    void shouldFindEspacosDisponiveis_WithDefaultCapacidade() throws Exception {
        // Given
        LocalDate data = LocalDate.of(2024, 12, 25);
        EspacoResponseDTO espaco = new EspacoResponseDTO(1, "Salão Principal", "Espaço amplo", 100, new BigDecimal("500.00"), true, "foto1.jpg", filialDTO);
        when(espacoService.findDisponiveisPorData(data, 1)).thenReturn(Arrays.asList(espaco));

        // When & Then
        mockMvc.perform(get("/api/espacos/disponiveis")
                        .param("data", "2024-12-25"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));

        verify(espacoService).findDisponiveisPorData(data, 1);
    }

    @Test
    void shouldCreateEspaco() throws Exception {
        // Given
        EspacoRequestDTO requestDTO = new EspacoRequestDTO("Salão Novo", "Descrição", 50, new BigDecimal("300.00"), true, "foto.jpg", 1);
        EspacoResponseDTO responseDTO = new EspacoResponseDTO(1, "Salão Novo", "Descrição", 50, new BigDecimal("300.00"), true, "foto.jpg", filialDTO);

        when(espacoService.create(any(EspacoRequestDTO.class))).thenReturn(responseDTO);

        // When & Then
        mockMvc.perform(post("/api/espacos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Salão Novo"))
                .andExpect(jsonPath("$.capacidade").value(50))
                .andExpect(jsonPath("$.precoDiaria").value(300.00));

        verify(espacoService).create(any(EspacoRequestDTO.class));
    }

    @Test
    void shouldReturn400_WhenCreateWithInvalidData() throws Exception {
        // Given - nome vazio
        EspacoRequestDTO requestDTO = new EspacoRequestDTO("", "Descrição", 50, new BigDecimal("300.00"), true, "foto.jpg", 1);

        // When & Then
        mockMvc.perform(post("/api/espacos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest());

        verify(espacoService, never()).create(any(EspacoRequestDTO.class));
    }

    @Test
    void shouldUpdateEspaco() throws Exception {
        // Given
        EspacoRequestDTO requestDTO = new EspacoRequestDTO("Salão Atualizado", "Nova descrição", 80, new BigDecimal("450.00"), true, "foto2.jpg", 1);
        EspacoResponseDTO responseDTO = new EspacoResponseDTO(1, "Salão Atualizado", "Nova descrição", 80, new BigDecimal("450.00"), true, "foto2.jpg", filialDTO);

        when(espacoService.update(eq(1), any(EspacoRequestDTO.class))).thenReturn(responseDTO);

        // When & Then
        mockMvc.perform(put("/api/espacos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Salão Atualizado"))
                .andExpect(jsonPath("$.capacidade").value(80))
                .andExpect(jsonPath("$.precoDiaria").value(450.00));

        verify(espacoService).update(eq(1), any(EspacoRequestDTO.class));
    }

    @Test
    void shouldReturn404_WhenUpdateNonExistentEspaco() throws Exception {
        // Given
        EspacoRequestDTO requestDTO = new EspacoRequestDTO("Salão Atualizado", "Nova descrição", 80, new BigDecimal("450.00"), true, "foto2.jpg", 1);
        when(espacoService.update(eq(999), any(EspacoRequestDTO.class)))
                .thenThrow(new ResourceNotFoundException("Espaço não encontrado"));

        // When & Then
        mockMvc.perform(put("/api/espacos/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isNotFound());

        verify(espacoService).update(eq(999), any(EspacoRequestDTO.class));
    }

    @Test
    void shouldDeleteEspaco() throws Exception {
        // Given
        doNothing().when(espacoService).delete(1);

        // When & Then
        mockMvc.perform(delete("/api/espacos/1"))
                .andExpect(status().isNoContent());

        verify(espacoService).delete(1);
    }

    @Test
    void shouldReturn404_WhenDeleteNonExistentEspaco() throws Exception {
        // Given
        doThrow(new ResourceNotFoundException("Espaço não encontrado")).when(espacoService).delete(999);

        // When & Then
        mockMvc.perform(delete("/api/espacos/999"))
                .andExpect(status().isNotFound());

        verify(espacoService).delete(999);
    }
}
