package com.seucantinho.api.feature.cliente.domain.service;

import com.seucantinho.api.feature.cliente.domain.Cliente;
import com.seucantinho.api.feature.cliente.domain.port.out.ClienteRepositoryPort;
import com.seucantinho.api.shared.domain.exception.DuplicateResourceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do ClienteUniquenessService")
class ClienteUniquenessServiceTest {

    @Mock
    private ClienteRepositoryPort clienteRepositoryPort;

    @InjectMocks
    private ClienteUniquenessService clienteUniquenessService;

    private Cliente cliente;

    @BeforeEach
    void setUp() {
        cliente = Cliente.builder()
                .id(1)
                .nome("João Silva")
                .email("joao@email.com")
                .senhaHash("hash123")
                .cpf("12345678901")
                .build();
    }

    @Test
    @DisplayName("Deve validar email único com sucesso")
    void deveValidarEmailUnicoComSucesso() {
        // Arrange
        String email = "novo@email.com";
        when(clienteRepositoryPort.findByEmail(email)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatCode(() -> clienteUniquenessService.validarEmailUnico(email))
                .doesNotThrowAnyException();
        verify(clienteRepositoryPort).findByEmail(email);
    }

    @Test
    @DisplayName("Deve lançar exceção quando email já existe")
    void deveLancarExcecaoQuandoEmailJaExiste() {
        // Arrange
        String email = "joao@email.com";
        when(clienteRepositoryPort.findByEmail(email)).thenReturn(Optional.of(cliente));

        // Act & Assert
        assertThatThrownBy(() -> clienteUniquenessService.validarEmailUnico(email))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("Email já cadastrado: " + email);
        verify(clienteRepositoryPort).findByEmail(email);
    }

    @Test
    @DisplayName("Deve validar email único para atualização quando é o mesmo cliente")
    void deveValidarEmailUnicoParaAtualizacaoQuandoEOMesmoCliente() {
        // Arrange
        String email = "joao@email.com";
        Integer clienteId = 1;
        when(clienteRepositoryPort.findByEmail(email)).thenReturn(Optional.of(cliente));

        // Act & Assert
        assertThatCode(() -> clienteUniquenessService.validarEmailUnicoParaAtualizacao(email, clienteId))
                .doesNotThrowAnyException();
        verify(clienteRepositoryPort).findByEmail(email);
    }

    @Test
    @DisplayName("Deve lançar exceção quando email pertence a outro cliente")
    void deveLancarExcecaoQuandoEmailPertenceAOutroCliente() {
        // Arrange
        String email = "joao@email.com";
        Integer clienteId = 2; // ID diferente do cliente existente
        when(clienteRepositoryPort.findByEmail(email)).thenReturn(Optional.of(cliente));

        // Act & Assert
        assertThatThrownBy(() -> clienteUniquenessService.validarEmailUnicoParaAtualizacao(email, clienteId))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("Email já cadastrado: " + email);
        verify(clienteRepositoryPort).findByEmail(email);
    }

    @Test
    @DisplayName("Deve validar email único para atualização quando email não existe")
    void deveValidarEmailUnicoParaAtualizacaoQuandoEmailNaoExiste() {
        // Arrange
        String email = "novo@email.com";
        Integer clienteId = 1;
        when(clienteRepositoryPort.findByEmail(email)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatCode(() -> clienteUniquenessService.validarEmailUnicoParaAtualizacao(email, clienteId))
                .doesNotThrowAnyException();
        verify(clienteRepositoryPort).findByEmail(email);
    }

    @Test
    @DisplayName("Deve validar CPF único com sucesso")
    void deveValidarCpfUnicoComSucesso() {
        // Arrange
        String cpf = "98765432100";
        when(clienteRepositoryPort.findByCpf(cpf)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatCode(() -> clienteUniquenessService.validarCpfUnico(cpf))
                .doesNotThrowAnyException();
        verify(clienteRepositoryPort).findByCpf(cpf);
    }

    @Test
    @DisplayName("Deve lançar exceção quando CPF já existe")
    void deveLancarExcecaoQuandoCpfJaExiste() {
        // Arrange
        String cpf = "12345678901";
        when(clienteRepositoryPort.findByCpf(cpf)).thenReturn(Optional.of(cliente));

        // Act & Assert
        assertThatThrownBy(() -> clienteUniquenessService.validarCpfUnico(cpf))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("CPF já cadastrado: " + cpf);
        verify(clienteRepositoryPort).findByCpf(cpf);
    }

    @Test
    @DisplayName("Deve permitir CPF nulo")
    void devePermitirCpfNulo() {
        // Arrange
        String cpf = null;

        // Act & Assert
        assertThatCode(() -> clienteUniquenessService.validarCpfUnico(cpf))
                .doesNotThrowAnyException();
        verify(clienteRepositoryPort, never()).findByCpf(anyString());
    }

    @Test
    @DisplayName("Deve validar múltiplos emails únicos sequencialmente")
    void deveValidarMultiplosEmailsUnicosSequencialmente() {
        // Arrange
        String email1 = "email1@test.com";
        String email2 = "email2@test.com";
        when(clienteRepositoryPort.findByEmail(email1)).thenReturn(Optional.empty());
        when(clienteRepositoryPort.findByEmail(email2)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatCode(() -> {
            clienteUniquenessService.validarEmailUnico(email1);
            clienteUniquenessService.validarEmailUnico(email2);
        }).doesNotThrowAnyException();
        
        verify(clienteRepositoryPort).findByEmail(email1);
        verify(clienteRepositoryPort).findByEmail(email2);
    }

    @Test
    @DisplayName("Deve validar email case sensitive")
    void deveValidarEmailCaseSensitive() {
        // Arrange
        String emailLower = "joao@email.com";
        String emailUpper = "JOAO@EMAIL.COM";
        when(clienteRepositoryPort.findByEmail(emailUpper)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatCode(() -> clienteUniquenessService.validarEmailUnico(emailUpper))
                .doesNotThrowAnyException();
        verify(clienteRepositoryPort).findByEmail(emailUpper);
    }
}
