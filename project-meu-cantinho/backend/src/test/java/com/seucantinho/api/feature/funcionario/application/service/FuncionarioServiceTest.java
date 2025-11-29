package com.seucantinho.api.feature.funcionario.application.service;

import com.seucantinho.api.feature.filial.domain.Filial;
import com.seucantinho.api.feature.filial.domain.port.out.FilialRepositoryPort;
import com.seucantinho.api.feature.funcionario.application.dto.FuncionarioRequestDTO;
import com.seucantinho.api.feature.funcionario.application.dto.FuncionarioResponseDTO;
import com.seucantinho.api.feature.funcionario.domain.Funcionario;
import com.seucantinho.api.feature.funcionario.domain.port.out.FuncionarioRepositoryPort;
import com.seucantinho.api.feature.funcionario.domain.service.FuncionarioUniquenessService;
import com.seucantinho.api.feature.funcionario.infrastructure.mapper.FuncionarioMapper;
import com.seucantinho.api.shared.domain.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FuncionarioServiceTest {

    @Mock
    private FuncionarioRepositoryPort funcionarioRepositoryPort;

    @Mock
    private FilialRepositoryPort filialRepositoryPort;

    @Mock
    private FuncionarioMapper funcionarioMapper;

    @Mock
    private FuncionarioUniquenessService funcionarioUniquenessService;

    @InjectMocks
    private FuncionarioService funcionarioService;

    private Funcionario funcionario;
    private FuncionarioRequestDTO requestDTO;
    private FuncionarioResponseDTO responseDTO;
    private Filial filial;

    @BeforeEach
    void setUp() {
        filial = new Filial();
        filial.setId(1);
        filial.setNome("Filial Centro");

        funcionario = new Funcionario();
        funcionario.setId(1);
        funcionario.setNome("João Silva");
        funcionario.setEmail("joao@example.com");
        funcionario.setMatricula("F001");
        funcionario.setFilial(filial);
        funcionario.setAtivo(true);
        funcionario.setDataCadastro(LocalDateTime.now());

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
    void findAll_DeveRetornarListaDeFuncionarios() {
        List<Funcionario> funcionarios = Arrays.asList(funcionario);
        when(funcionarioRepositoryPort.findAll()).thenReturn(funcionarios);
        when(funcionarioMapper.toResponseDTO(funcionario)).thenReturn(responseDTO);

        List<FuncionarioResponseDTO> result = funcionarioService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("João Silva", result.get(0).getNome());
        verify(funcionarioRepositoryPort).findAll();
        verify(funcionarioMapper).toResponseDTO(funcionario);
    }

    @Test
    void findByFilialId_DeveRetornarFuncionariosDaFilial() {
        List<Funcionario> funcionarios = Arrays.asList(funcionario);
        when(funcionarioRepositoryPort.findByFilialId(1)).thenReturn(funcionarios);
        when(funcionarioMapper.toResponseDTO(funcionario)).thenReturn(responseDTO);

        List<FuncionarioResponseDTO> result = funcionarioService.findByFilialId(1);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(funcionarioRepositoryPort).findByFilialId(1);
    }

    @Test
    void findById_QuandoFuncionarioExiste_DeveRetornarFuncionario() {
        when(funcionarioRepositoryPort.findById(1)).thenReturn(Optional.of(funcionario));
        when(funcionarioMapper.toResponseDTO(funcionario)).thenReturn(responseDTO);

        FuncionarioResponseDTO result = funcionarioService.findById(1);

        assertNotNull(result);
        assertEquals("João Silva", result.getNome());
        verify(funcionarioRepositoryPort).findById(1);
        verify(funcionarioMapper).toResponseDTO(funcionario);
    }

    @Test
    void findById_QuandoFuncionarioNaoExiste_DeveLancarExcecao() {
        when(funcionarioRepositoryPort.findById(999)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> funcionarioService.findById(999));

        assertEquals("Funcionário não encontrado com ID: 999", exception.getMessage());
        verify(funcionarioRepositoryPort).findById(999);
    }

    @Test
    void create_QuandoDadosValidos_DeveCriarFuncionario() {
        when(filialRepositoryPort.findById(1)).thenReturn(Optional.of(filial));
        when(funcionarioMapper.toEntity(requestDTO)).thenReturn(funcionario);
        when(funcionarioRepositoryPort.save(any(Funcionario.class))).thenReturn(funcionario);
        when(funcionarioMapper.toResponseDTO(funcionario)).thenReturn(responseDTO);

        FuncionarioResponseDTO result = funcionarioService.create(requestDTO);

        assertNotNull(result);
        assertEquals("João Silva", result.getNome());
        verify(funcionarioUniquenessService).validarEmailUnico(requestDTO.getEmail());
        verify(funcionarioUniquenessService).validarMatriculaUnica(requestDTO.getMatricula());
        verify(filialRepositoryPort).findById(1);
        verify(funcionarioRepositoryPort).save(any(Funcionario.class));
    }

    @Test
    void create_QuandoFilialNaoExiste_DeveLancarExcecao() {
        when(filialRepositoryPort.findById(999)).thenReturn(Optional.empty());

        requestDTO.setFilialId(999);
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> funcionarioService.create(requestDTO));

        assertTrue(exception.getMessage().contains("Filial não encontrada"));
        verify(filialRepositoryPort).findById(999);
        verify(funcionarioRepositoryPort, never()).save(any());
    }

    @Test
    void update_QuandoDadosValidos_DeveAtualizarFuncionario() {
        when(funcionarioRepositoryPort.findById(1)).thenReturn(Optional.of(funcionario));
        doNothing().when(funcionarioMapper).updateEntityFromDTO(funcionario, requestDTO);
        when(funcionarioRepositoryPort.save(funcionario)).thenReturn(funcionario);
        when(funcionarioMapper.toResponseDTO(funcionario)).thenReturn(responseDTO);

        FuncionarioResponseDTO result = funcionarioService.update(1, requestDTO);

        assertNotNull(result);
        verify(funcionarioUniquenessService).validarEmailUnicoParaAtualizacao(requestDTO.getEmail(), 1);
        verify(funcionarioUniquenessService).validarMatriculaUnicaParaAtualizacao(requestDTO.getMatricula(), 1);
        verify(funcionarioRepositoryPort).save(funcionario);
    }

    @Test
    void update_QuandoFuncionarioNaoExiste_DeveLancarExcecao() {
        when(funcionarioRepositoryPort.findById(999)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> funcionarioService.update(999, requestDTO));

        assertEquals("Funcionário não encontrado com ID: 999", exception.getMessage());
        verify(funcionarioRepositoryPort, never()).save(any());
    }

    @Test
    void delete_QuandoFuncionarioExiste_DeveDeletar() {
        when(funcionarioRepositoryPort.findById(1)).thenReturn(Optional.of(funcionario));

        funcionarioService.delete(1);

        verify(funcionarioRepositoryPort).findById(1);
        verify(funcionarioRepositoryPort).deleteById(1);
    }

    @Test
    void delete_QuandoFuncionarioNaoExiste_DeveLancarExcecao() {
        when(funcionarioRepositoryPort.findById(999)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> funcionarioService.delete(999));

        assertEquals("Funcionário não encontrado com ID: 999", exception.getMessage());
        verify(funcionarioRepositoryPort, never()).deleteById(any());
    }

    @Test
    void toggleAtivo_QuandoFuncionarioExiste_DeveAlterarStatus() {
        when(funcionarioRepositoryPort.findById(1)).thenReturn(Optional.of(funcionario));
        when(funcionarioRepositoryPort.save(funcionario)).thenReturn(funcionario);
        when(funcionarioMapper.toResponseDTO(funcionario)).thenReturn(responseDTO);

        FuncionarioResponseDTO result = funcionarioService.toggleAtivo(1, false);

        assertNotNull(result);
        verify(funcionarioRepositoryPort).save(funcionario);
    }

    @Test
    void toggleAtivo_QuandoFuncionarioNaoExiste_DeveLancarExcecao() {
        when(funcionarioRepositoryPort.findById(999)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> funcionarioService.toggleAtivo(999, true));

        assertEquals("Funcionário não encontrado com ID: 999", exception.getMessage());
    }

    @Test
    void trocarFilial_QuandoDadosValidos_DeveTrocarFilial() {
        Filial novaFilial = new Filial();
        novaFilial.setId(2);
        novaFilial.setNome("Filial Norte");

        when(funcionarioRepositoryPort.findById(1)).thenReturn(Optional.of(funcionario));
        when(filialRepositoryPort.findById(2)).thenReturn(Optional.of(novaFilial));
        when(funcionarioRepositoryPort.save(funcionario)).thenReturn(funcionario);
        when(funcionarioMapper.toResponseDTO(funcionario)).thenReturn(responseDTO);

        FuncionarioResponseDTO result = funcionarioService.trocarFilial(1, 2);

        assertNotNull(result);
        verify(funcionarioRepositoryPort).save(funcionario);
    }

    @Test
    void trocarFilial_QuandoFuncionarioNaoExiste_DeveLancarExcecao() {
        when(funcionarioRepositoryPort.findById(999)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> funcionarioService.trocarFilial(999, 2));

        assertEquals("Funcionário não encontrado com ID: 999", exception.getMessage());
        verify(filialRepositoryPort, never()).findById(any());
    }

    @Test
    void trocarFilial_QuandoNovaFilialNaoExiste_DeveLancarExcecao() {
        when(funcionarioRepositoryPort.findById(1)).thenReturn(Optional.of(funcionario));
        when(filialRepositoryPort.findById(999)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> funcionarioService.trocarFilial(1, 999));

        assertTrue(exception.getMessage().contains("Filial não encontrada"));
        verify(funcionarioRepositoryPort, never()).save(any());
    }
}
