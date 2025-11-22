package com.seucantinho.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seucantinho.api.dto.filial.FilialRequestDTO;
import com.seucantinho.api.dto.filial.FilialResponseDTO;
import com.seucantinho.api.exception.GlobalExceptionHandler;
import com.seucantinho.api.exception.ResourceNotFoundException;
import com.seucantinho.api.service.interfaces.IFilialService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class FilialControllerTest {

    @Mock
    private IFilialService filialService;

    @InjectMocks
    private FilialController filialController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(filialController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
    }

    @Test
    void shouldFindAllFiliais() throws Exception {
        // Given
        FilialResponseDTO filial1 = new FilialResponseDTO(1, "Filial Centro", "Curitiba", "PR", "Rua A, 123", "41999998888", null, 0);
        FilialResponseDTO filial2 = new FilialResponseDTO(2, "Filial Batel", "Curitiba", "PR", "Rua B, 456", "41988887777", null, 0);
        List<FilialResponseDTO> filiais = Arrays.asList(filial1, filial2);

        when(filialService.findAll()).thenReturn(filiais);

        // When & Then
        mockMvc.perform(get("/api/filiais"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nome").value("Filial Centro"))
                .andExpect(jsonPath("$[0].cidade").value("Curitiba"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].nome").value("Filial Batel"));

        verify(filialService).findAll();
    }

    @Test
    void shouldFindAllFiliais_WhenEmpty() throws Exception {
        // Given
        when(filialService.findAll()).thenReturn(Collections.emptyList());

        // When & Then
        mockMvc.perform(get("/api/filiais"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        verify(filialService).findAll();
    }

    @Test
    void shouldFindFilialById() throws Exception {
        // Given
        FilialResponseDTO filial = new FilialResponseDTO(1, "Filial Centro", "Curitiba", "PR", "Rua A, 123", "41999998888", null, 0);
        when(filialService.findById(1)).thenReturn(filial);

        // When & Then
        mockMvc.perform(get("/api/filiais/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Filial Centro"))
                .andExpect(jsonPath("$.cidade").value("Curitiba"))
                .andExpect(jsonPath("$.estado").value("PR"))
                .andExpect(jsonPath("$.endereco").value("Rua A, 123"))
                .andExpect(jsonPath("$.telefone").value("41999998888"));

        verify(filialService).findById(1);
    }

    @Test
    void shouldReturn404_WhenFilialNotFound() throws Exception {
        // Given
        when(filialService.findById(999)).thenThrow(new ResourceNotFoundException("Filial não encontrada"));

        // When & Then
        mockMvc.perform(get("/api/filiais/999"))
                .andExpect(status().isNotFound());

        verify(filialService).findById(999);
    }

    @Test
    void shouldCreateFilial() throws Exception {
        // Given
        FilialRequestDTO requestDTO = new FilialRequestDTO("Filial Nova", "Curitiba", "PR", "Rua Nova, 789", "41977776666");
        FilialResponseDTO responseDTO = new FilialResponseDTO(1, "Filial Nova", "Curitiba", "PR", "Rua Nova, 789", "41977776666", null, 0);

        when(filialService.create(any(FilialRequestDTO.class))).thenReturn(responseDTO);

        // When & Then
        mockMvc.perform(post("/api/filiais")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Filial Nova"))
                .andExpect(jsonPath("$.cidade").value("Curitiba"))
                .andExpect(jsonPath("$.endereco").value("Rua Nova, 789"))
                .andExpect(jsonPath("$.telefone").value("41977776666"));

        verify(filialService).create(any(FilialRequestDTO.class));
    }

    @Test
    void shouldReturn400_WhenCreateWithInvalidData() throws Exception {
        // Given - nome vazio
        FilialRequestDTO requestDTO = new FilialRequestDTO("", "Curitiba", "PR", "Rua Nova, 789", "41977776666");

        // When & Then
        mockMvc.perform(post("/api/filiais")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest());

        verify(filialService, never()).create(any(FilialRequestDTO.class));
    }

    @Test
    void shouldUpdateFilial() throws Exception {
        // Given
        FilialRequestDTO requestDTO = new FilialRequestDTO("Filial Atualizada", "Curitiba", "PR", "Rua Atualizada, 999", "41966665555");
        FilialResponseDTO responseDTO = new FilialResponseDTO(1, "Filial Atualizada", "Curitiba", "PR", "Rua Atualizada, 999", "41966665555", null, 0);

        when(filialService.update(eq(1), any(FilialRequestDTO.class))).thenReturn(responseDTO);

        // When & Then
        mockMvc.perform(put("/api/filiais/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Filial Atualizada"))
                .andExpect(jsonPath("$.cidade").value("Curitiba"))
                .andExpect(jsonPath("$.endereco").value("Rua Atualizada, 999"))
                .andExpect(jsonPath("$.telefone").value("41966665555"));

        verify(filialService).update(eq(1), any(FilialRequestDTO.class));
    }

    @Test
    void shouldReturn404_WhenUpdateNonExistentFilial() throws Exception {
        // Given
        FilialRequestDTO requestDTO = new FilialRequestDTO("Filial Atualizada", "Curitiba", "PR", "Rua Atualizada, 999", "41966665555");
        when(filialService.update(eq(999), any(FilialRequestDTO.class)))
                .thenThrow(new ResourceNotFoundException("Filial não encontrada"));

        // When & Then
        mockMvc.perform(put("/api/filiais/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isNotFound());

        verify(filialService).update(eq(999), any(FilialRequestDTO.class));
    }

    @Test
    void shouldDeleteFilial() throws Exception {
        // Given
        doNothing().when(filialService).delete(1);

        // When & Then
        mockMvc.perform(delete("/api/filiais/1"))
                .andExpect(status().isNoContent());

        verify(filialService).delete(1);
    }

    @Test
    void shouldReturn404_WhenDeleteNonExistentFilial() throws Exception {
        // Given
        doThrow(new ResourceNotFoundException("Filial não encontrada")).when(filialService).delete(999);

        // When & Then
        mockMvc.perform(delete("/api/filiais/999"))
                .andExpect(status().isNotFound());

        verify(filialService).delete(999);
    }
}
