package com.seucantinho.api.feature.funcionario.infrastructure.adapter.in.web;

import com.seucantinho.api.feature.funcionario.application.dto.FuncionarioRequestDTO;
import com.seucantinho.api.feature.funcionario.application.dto.FuncionarioResponseDTO;
import com.seucantinho.api.feature.funcionario.domain.port.in.FuncionarioServicePort;
import com.seucantinho.api.shared.domain.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FuncionarioWebAdapterTest {

    @Mock
    private FuncionarioServicePort funcionarioService;

    @InjectMocks
    private FuncionarioWebAdapter funcionarioWebAdapter;

    private FuncionarioRequestDTO requestDTO;
    private FuncionarioResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        requestDTO = FuncionarioRequestDTO.builder()
                .nome("João Silva")
                .email("joao@example.com")
                .senha("senha123")
                .matricula("F001")
                .filialId(1)
                .ativo(true)
                .build();

        responseDTO = FuncionarioResponseDTO.builder()
                .id(1)
                .nome("João Silva")
                .email("joao@example.com")
                .matricula("F001")
                .ativo(true)
                .build();
    }

    @Test
    void findAll_SemFiltro_DeveRetornarTodosFuncionarios() {
        List<FuncionarioResponseDTO> funcionarios = Arrays.asList(responseDTO);
        when(funcionarioService.findAll()).thenReturn(funcionarios);

        ResponseEntity<List<FuncionarioResponseDTO>> response = funcionarioWebAdapter.findAll(null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(funcionarioService).findAll();
        verify(funcionarioService, never()).findByFilialId(any());
    }

    @Test
    void findAll_ComFiltroFilial_DeveRetornarFuncionariosDaFilial() {
        List<FuncionarioResponseDTO> funcionarios = Arrays.asList(responseDTO);
        when(funcionarioService.findByFilialId(1)).thenReturn(funcionarios);

        ResponseEntity<List<FuncionarioResponseDTO>> response = funcionarioWebAdapter.findAll(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(funcionarioService).findByFilialId(1);
        verify(funcionarioService, never()).findAll();
    }

    @Test
    void findById_QuandoFuncionarioExiste_DeveRetornarFuncionario() {
        when(funcionarioService.findById(1)).thenReturn(responseDTO);

        ResponseEntity<FuncionarioResponseDTO> response = funcionarioWebAdapter.findById(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("João Silva", response.getBody().getNome());
        verify(funcionarioService).findById(1);
    }

    @Test
    void findById_QuandoFuncionarioNaoExiste_DeveLancarExcecao() {
        when(funcionarioService.findById(999)).thenThrow(new ResourceNotFoundException("Funcionário não encontrado"));

        assertThrows(ResourceNotFoundException.class, () -> funcionarioWebAdapter.findById(999));
        verify(funcionarioService).findById(999);
    }

    @Test
    void create_QuandoDadosValidos_DeveCriarFuncionario() {
        when(funcionarioService.create(any(FuncionarioRequestDTO.class))).thenReturn(responseDTO);

        ResponseEntity<FuncionarioResponseDTO> response = funcionarioWebAdapter.create(requestDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("João Silva", response.getBody().getNome());
        verify(funcionarioService).create(requestDTO);
    }

    @Test
    void update_QuandoFuncionarioExiste_DeveAtualizar() {
        when(funcionarioService.update(eq(1), any(FuncionarioRequestDTO.class))).thenReturn(responseDTO);

        ResponseEntity<FuncionarioResponseDTO> response = funcionarioWebAdapter.update(1, requestDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(funcionarioService).update(1, requestDTO);
    }

    @Test
    void update_QuandoFuncionarioNaoExiste_DeveLancarExcecao() {
        when(funcionarioService.update(eq(999), any(FuncionarioRequestDTO.class)))
                .thenThrow(new ResourceNotFoundException("Funcionário não encontrado"));

        assertThrows(ResourceNotFoundException.class, () -> funcionarioWebAdapter.update(999, requestDTO));
        verify(funcionarioService).update(999, requestDTO);
    }

    @Test
    void delete_QuandoFuncionarioExiste_DeveDeletar() {
        doNothing().when(funcionarioService).delete(1);

        ResponseEntity<Void> response = funcionarioWebAdapter.delete(1);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(funcionarioService).delete(1);
    }

    @Test
    void delete_QuandoFuncionarioNaoExiste_DeveLancarExcecao() {
        doThrow(new ResourceNotFoundException("Funcionário não encontrado"))
                .when(funcionarioService).delete(999);

        assertThrows(ResourceNotFoundException.class, () -> funcionarioWebAdapter.delete(999));
        verify(funcionarioService).delete(999);
    }

    @Test
    void toggleAtivo_QuandoFuncionarioExiste_DeveAlterarStatus() {
        when(funcionarioService.toggleAtivo(1, false)).thenReturn(responseDTO);

        ResponseEntity<FuncionarioResponseDTO> response = funcionarioWebAdapter.toggleAtivo(1, false);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(funcionarioService).toggleAtivo(1, false);
    }

    @Test
    void toggleAtivo_QuandoFuncionarioNaoExiste_DeveLancarExcecao() {
        when(funcionarioService.toggleAtivo(999, true))
                .thenThrow(new ResourceNotFoundException("Funcionário não encontrado"));

        assertThrows(ResourceNotFoundException.class, () -> funcionarioWebAdapter.toggleAtivo(999, true));
        verify(funcionarioService).toggleAtivo(999, true);
    }

    @Test
    void trocarFilial_QuandoDadosValidos_DeveTrocarFilial() {
        when(funcionarioService.trocarFilial(1, 2)).thenReturn(responseDTO);

        ResponseEntity<FuncionarioResponseDTO> response = funcionarioWebAdapter.trocarFilial(1, 2);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(funcionarioService).trocarFilial(1, 2);
    }

    @Test
    void trocarFilial_QuandoFuncionarioNaoExiste_DeveLancarExcecao() {
        when(funcionarioService.trocarFilial(999, 2))
                .thenThrow(new ResourceNotFoundException("Funcionário não encontrado"));

        assertThrows(ResourceNotFoundException.class, () -> funcionarioWebAdapter.trocarFilial(999, 2));
        verify(funcionarioService).trocarFilial(999, 2);
    }

    @Test
    void trocarFilial_QuandoFilialNaoExiste_DeveLancarExcecao() {
        when(funcionarioService.trocarFilial(1, 999))
                .thenThrow(new ResourceNotFoundException("Filial não encontrada"));

        assertThrows(ResourceNotFoundException.class, () -> funcionarioWebAdapter.trocarFilial(1, 999));
        verify(funcionarioService).trocarFilial(1, 999);
    }
}
