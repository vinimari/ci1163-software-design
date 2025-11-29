package com.seucantinho.api.feature.funcionario.domain.service;

import com.seucantinho.api.feature.funcionario.domain.Funcionario;
import com.seucantinho.api.feature.funcionario.domain.port.out.FuncionarioRepositoryPort;
import com.seucantinho.api.feature.usuario.domain.port.out.UsuarioRepositoryPort;
import com.seucantinho.api.shared.domain.exception.DuplicateResourceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FuncionarioUniquenessServiceTest {

    @Mock
    private FuncionarioRepositoryPort funcionarioRepositoryPort;

    @Mock
    private UsuarioRepositoryPort usuarioRepositoryPort;

    @InjectMocks
    private FuncionarioUniquenessService funcionarioUniquenessService;

    private static final String EMAIL_EXISTENTE = "funcionario@example.com";
    private static final String EMAIL_NOVO = "novo@example.com";
    private static final String MATRICULA_EXISTENTE = "F001";
    private static final String MATRICULA_NOVA = "F002";
    private static final Integer FUNCIONARIO_ID = 1;

    @BeforeEach
    void setUp() {
        reset(funcionarioRepositoryPort, usuarioRepositoryPort);
    }

    @Test
    void validarEmailUnico_QuandoEmailNaoExiste_DevePassar() {
        when(usuarioRepositoryPort.findByEmail(EMAIL_NOVO)).thenReturn(Optional.empty());

        assertDoesNotThrow(() -> funcionarioUniquenessService.validarEmailUnico(EMAIL_NOVO));

        verify(usuarioRepositoryPort).findByEmail(EMAIL_NOVO);
    }

    @Test
    void validarEmailUnico_QuandoEmailJaExiste_DeveLancarExcecao() {
        Funcionario usuarioExistente = new Funcionario();
        when(usuarioRepositoryPort.findByEmail(EMAIL_EXISTENTE)).thenReturn(Optional.of(usuarioExistente));

        DuplicateResourceException exception = assertThrows(DuplicateResourceException.class,
                () -> funcionarioUniquenessService.validarEmailUnico(EMAIL_EXISTENTE));

        assertEquals("Email já cadastrado no sistema", exception.getMessage());
        verify(usuarioRepositoryPort).findByEmail(EMAIL_EXISTENTE);
    }

    @Test
    void validarEmailUnicoParaAtualizacao_QuandoEmailNaoMuda_DevePassar() {
        Funcionario usuario = new Funcionario();
        usuario.setId(FUNCIONARIO_ID);
        when(usuarioRepositoryPort.findByEmail(EMAIL_EXISTENTE)).thenReturn(Optional.of(usuario));

        assertDoesNotThrow(() -> funcionarioUniquenessService.validarEmailUnicoParaAtualizacao(
                EMAIL_EXISTENTE, FUNCIONARIO_ID));

        verify(usuarioRepositoryPort).findByEmail(EMAIL_EXISTENTE);
    }

    @Test
    void validarEmailUnicoParaAtualizacao_QuandoEmailNovoNaoExiste_DevePassar() {
        when(usuarioRepositoryPort.findByEmail(EMAIL_NOVO)).thenReturn(Optional.empty());

        assertDoesNotThrow(() -> funcionarioUniquenessService.validarEmailUnicoParaAtualizacao(
                EMAIL_NOVO, FUNCIONARIO_ID));

        verify(usuarioRepositoryPort).findByEmail(EMAIL_NOVO);
    }

    @Test
    void validarEmailUnicoParaAtualizacao_QuandoEmailPertenceAOutroUsuario_DeveLancarExcecao() {
        Funcionario outroUsuario = new Funcionario();
        outroUsuario.setId(999);
        when(usuarioRepositoryPort.findByEmail(EMAIL_EXISTENTE)).thenReturn(Optional.of(outroUsuario));

        DuplicateResourceException exception = assertThrows(DuplicateResourceException.class,
                () -> funcionarioUniquenessService.validarEmailUnicoParaAtualizacao(
                        EMAIL_EXISTENTE, FUNCIONARIO_ID));

        assertEquals("Email já cadastrado no sistema", exception.getMessage());
        verify(usuarioRepositoryPort).findByEmail(EMAIL_EXISTENTE);
    }

    @Test
    void validarMatriculaUnica_QuandoMatriculaNaoExiste_DevePassar() {
        when(funcionarioRepositoryPort.findByMatricula(MATRICULA_NOVA)).thenReturn(Optional.empty());

        assertDoesNotThrow(() -> funcionarioUniquenessService.validarMatriculaUnica(MATRICULA_NOVA));

        verify(funcionarioRepositoryPort).findByMatricula(MATRICULA_NOVA);
    }

    @Test
    void validarMatriculaUnica_QuandoMatriculaJaExiste_DeveLancarExcecao() {
        Funcionario funcionarioExistente = new Funcionario();
        when(funcionarioRepositoryPort.findByMatricula(MATRICULA_EXISTENTE))
                .thenReturn(Optional.of(funcionarioExistente));

        DuplicateResourceException exception = assertThrows(DuplicateResourceException.class,
                () -> funcionarioUniquenessService.validarMatriculaUnica(MATRICULA_EXISTENTE));

        assertEquals("Matrícula já cadastrada", exception.getMessage());
        verify(funcionarioRepositoryPort).findByMatricula(MATRICULA_EXISTENTE);
    }

    @Test
    void validarMatriculaUnicaParaAtualizacao_QuandoMatriculaNaoMuda_DevePassar() {
        Funcionario funcionario = new Funcionario();
        funcionario.setId(FUNCIONARIO_ID);
        when(funcionarioRepositoryPort.findByMatricula(MATRICULA_EXISTENTE))
                .thenReturn(Optional.of(funcionario));

        assertDoesNotThrow(() -> funcionarioUniquenessService.validarMatriculaUnicaParaAtualizacao(
                MATRICULA_EXISTENTE, FUNCIONARIO_ID));

        verify(funcionarioRepositoryPort).findByMatricula(MATRICULA_EXISTENTE);
    }

    @Test
    void validarMatriculaUnicaParaAtualizacao_QuandoMatriculaNovaNaoExiste_DevePassar() {
        when(funcionarioRepositoryPort.findByMatricula(MATRICULA_NOVA)).thenReturn(Optional.empty());

        assertDoesNotThrow(() -> funcionarioUniquenessService.validarMatriculaUnicaParaAtualizacao(
                MATRICULA_NOVA, FUNCIONARIO_ID));

        verify(funcionarioRepositoryPort).findByMatricula(MATRICULA_NOVA);
    }

    @Test
    void validarMatriculaUnicaParaAtualizacao_QuandoMatriculaPertenceAOutroFuncionario_DeveLancarExcecao() {
        Funcionario outroFuncionario = new Funcionario();
        outroFuncionario.setId(999);
        when(funcionarioRepositoryPort.findByMatricula(MATRICULA_EXISTENTE))
                .thenReturn(Optional.of(outroFuncionario));

        DuplicateResourceException exception = assertThrows(DuplicateResourceException.class,
                () -> funcionarioUniquenessService.validarMatriculaUnicaParaAtualizacao(
                        MATRICULA_EXISTENTE, FUNCIONARIO_ID));

        assertEquals("Matrícula já cadastrada", exception.getMessage());
        verify(funcionarioRepositoryPort).findByMatricula(MATRICULA_EXISTENTE);
    }
}
