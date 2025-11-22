package com.seucantinho.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seucantinho.api.dto.usuario.ClienteRequestDTO;
import com.seucantinho.api.dto.usuario.ClienteResponseDTO;
import com.seucantinho.api.exception.DuplicateResourceException;
import com.seucantinho.api.exception.GlobalExceptionHandler;
import com.seucantinho.api.exception.ResourceNotFoundException;
import com.seucantinho.api.service.interfaces.IClienteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ClienteControllerTest {

    private MockMvc mockMvc;

    @Mock
    private IClienteService clienteService;

    @InjectMocks
    private ClienteController clienteController;

    private ObjectMapper objectMapper;
    private ClienteResponseDTO responseDTO;
    private ClienteRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(clienteController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

        responseDTO = ClienteResponseDTO.builder()
                .id(1)
                .nome("João Silva")
                .email("joao@example.com")
                .cpf("12345678901")
                .telefone("11999999999")
                .ativo(true)
                .dataCadastro(LocalDateTime.now())
                .quantidadeReservas(0)
                .build();

        requestDTO = ClienteRequestDTO.builder()
                .nome("João Silva")
                .email("joao@example.com")
                .senha("senha123")
                .cpf("12345678901")
                .telefone("11999999999")
                .ativo(true)
                .build();
    }

    @Test
    void shouldFindAllClientes() throws Exception {
        // Given
        ClienteResponseDTO cliente2 = ClienteResponseDTO.builder()
                .id(2)
                .nome("Maria Santos")
                .email("maria@example.com")
                .cpf("98765432100")
                .build();

        List<ClienteResponseDTO> clientes = Arrays.asList(responseDTO, cliente2);
        when(clienteService.findAll()).thenReturn(clientes);

        // When & Then
        mockMvc.perform(get("/api/clientes"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[1].id", is(2)));

        verify(clienteService, times(1)).findAll();
    }

    @Test
    void shouldFindAllClientes_WhenEmpty() throws Exception {
        // Given
        when(clienteService.findAll()).thenReturn(Collections.emptyList());

        // When & Then
        mockMvc.perform(get("/api/clientes"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));

        verify(clienteService, times(1)).findAll();
    }

    @Test
    void shouldFindClienteById() throws Exception {
        // Given
        when(clienteService.findById(1)).thenReturn(responseDTO);

        // When & Then
        mockMvc.perform(get("/api/clientes/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nome", is("João Silva")));

        verify(clienteService, times(1)).findById(1);
    }

    @Test
    void shouldReturn404_WhenClienteNotFound() throws Exception {
        // Given
        when(clienteService.findById(999)).thenThrow(new ResourceNotFoundException("Cliente não encontrado"));

        // When & Then
        mockMvc.perform(get("/api/clientes/999"))
                .andExpect(status().isNotFound());

        verify(clienteService, times(1)).findById(999);
    }

    @Test
    void shouldCreateCliente() throws Exception {
        // Given
        when(clienteService.create(any(ClienteRequestDTO.class))).thenReturn(responseDTO);

        // When & Then
        mockMvc.perform(post("/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nome", is("João Silva")));

        verify(clienteService, times(1)).create(any(ClienteRequestDTO.class));
    }

    @Test
    void shouldReturn400_WhenCreateWithInvalidData() throws Exception {
        // Given
        ClienteRequestDTO invalidDTO = ClienteRequestDTO.builder()
                .nome("")
                .email("invalid-email")
                .build();

        // When & Then
        mockMvc.perform(post("/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(clienteService, never()).create(any(ClienteRequestDTO.class));
    }

    @Test
    void shouldReturn409_WhenCreateWithDuplicateEmail() throws Exception {
        // Given
        when(clienteService.create(any(ClienteRequestDTO.class)))
                .thenThrow(new DuplicateResourceException("Email já cadastrado"));

        // When & Then
        mockMvc.perform(post("/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isConflict());

        verify(clienteService, times(1)).create(any(ClienteRequestDTO.class));
    }

    @Test
    void shouldUpdateCliente() throws Exception {
        // Given
        ClienteResponseDTO updatedResponse = ClienteResponseDTO.builder()
                .id(1)
                .nome("João Silva Updated")
                .email("joao@example.com")
                .cpf("12345678901")
                .build();

        when(clienteService.update(eq(1), any(ClienteRequestDTO.class))).thenReturn(updatedResponse);

        // When & Then
        mockMvc.perform(put("/api/clientes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.nome", is("João Silva Updated")));

        verify(clienteService, times(1)).update(eq(1), any(ClienteRequestDTO.class));
    }

    @Test
    void shouldReturn404_WhenUpdateNonExistentCliente() throws Exception {
        // Given
        when(clienteService.update(eq(999), any(ClienteRequestDTO.class)))
                .thenThrow(new ResourceNotFoundException("Cliente não encontrado"));

        // When & Then
        mockMvc.perform(put("/api/clientes/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isNotFound());

        verify(clienteService, times(1)).update(eq(999), any(ClienteRequestDTO.class));
    }

    @Test
    void shouldDeleteCliente() throws Exception {
        // Given
        doNothing().when(clienteService).delete(1);

        // When & Then
        mockMvc.perform(delete("/api/clientes/1"))
                .andExpect(status().isNoContent());

        verify(clienteService, times(1)).delete(1);
    }

    @Test
    void shouldReturn404_WhenDeleteNonExistentCliente() throws Exception {
        // Given
        doThrow(new ResourceNotFoundException("Cliente não encontrado"))
                .when(clienteService).delete(999);

        // When & Then
        mockMvc.perform(delete("/api/clientes/999"))
                .andExpect(status().isNotFound());

        verify(clienteService, times(1)).delete(999);
    }

    @Test
    void shouldToggleAtivoToFalse() throws Exception {
        // Given
        ClienteResponseDTO inactiveResponse = ClienteResponseDTO.builder()
                .id(1)
                .nome("João Silva")
                .email("joao@example.com")
                .cpf("12345678901")
                .ativo(false)
                .build();

        when(clienteService.toggleAtivo(1, false)).thenReturn(inactiveResponse);

        // When & Then
        mockMvc.perform(patch("/api/clientes/1/ativo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"ativo\": false}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.ativo", is(false)));

        verify(clienteService, times(1)).toggleAtivo(1, false);
    }

    @Test
    void shouldToggleAtivoToTrue() throws Exception {
        // Given
        ClienteResponseDTO activeResponse = ClienteResponseDTO.builder()
                .id(1)
                .nome("João Silva")
                .email("joao@example.com")
                .cpf("12345678901")
                .ativo(true)
                .build();

        when(clienteService.toggleAtivo(1, true)).thenReturn(activeResponse);

        // When & Then
        mockMvc.perform(patch("/api/clientes/1/ativo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"ativo\": true}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.ativo", is(true)));

        verify(clienteService, times(1)).toggleAtivo(1, true);
    }

    @Test
    void shouldReturn400_WhenToggleAtivoWithoutAtivoField() throws Exception {
        // When & Then
        mockMvc.perform(patch("/api/clientes/1/ativo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());

        verify(clienteService, never()).toggleAtivo(any(), any());
    }

    @Test
    void shouldReturn404_WhenToggleAtivoForNonExistentCliente() throws Exception {
        // Given
        when(clienteService.toggleAtivo(999, true))
                .thenThrow(new ResourceNotFoundException("Cliente não encontrado"));

        // When & Then
        mockMvc.perform(patch("/api/clientes/999/ativo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"ativo\": true}"))
                .andExpect(status().isNotFound());

        verify(clienteService, times(1)).toggleAtivo(999, true);
    }
}
