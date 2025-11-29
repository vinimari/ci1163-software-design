package com.seucantinho.api.feature.cliente.application.validator;

import com.seucantinho.api.feature.cliente.domain.Cliente;
import com.seucantinho.api.feature.cliente.domain.port.out.ClienteRepositoryPort;
import com.seucantinho.api.shared.domain.exception.DuplicateResourceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do ClienteValidator")
class ClienteValidatorTest {

    @Mock
    private ClienteRepositoryPort clienteRepositoryPort;

    private ClienteValidator validator;

    @BeforeEach
    void setUp() {
        validator = new ClienteValidator(clienteRepositoryPort);
    }

    @Test
    @DisplayName("Deve validar email único com sucesso quando email não existe")
    void deveValidarEmailUnicoComSucesso() {
        String email = "novo@email.com";
        when(clienteRepositoryPort.findByEmail(email)).thenReturn(Optional.empty());

        assertDoesNotThrow(() -> validator.validateEmailUnique(email));
    }

    @Test
    @DisplayName("Deve lançar exceção quando email já existe")
    void deveLancarExcecaoQuandoEmailJaExiste() {
        String email = "existente@email.com";
        Cliente cliente = Cliente.builder()
                .id(1)
                .email(email)
                .build();

        when(clienteRepositoryPort.findByEmail(email)).thenReturn(Optional.of(cliente));

        DuplicateResourceException exception = assertThrows(
            DuplicateResourceException.class,
            () -> validator.validateEmailUnique(email)
        );

        assertTrue(exception.getMessage().contains("Email já cadastrado"));
    }

    @Test
    @DisplayName("Deve validar email único para atualização quando é do mesmo cliente")
    void deveValidarEmailParaAtualizacaoDoMesmoCliente() {
        String email = "cliente@email.com";
        Integer clienteId = 1;
        Cliente cliente = Cliente.builder()
                .id(1)
                .email(email)
                .build();

        when(clienteRepositoryPort.findByEmail(email)).thenReturn(Optional.of(cliente));

        assertDoesNotThrow(() -> validator.validateEmailUniqueForUpdate(email, clienteId));
    }

    @Test
    @DisplayName("Deve lançar exceção quando email já existe para outro cliente na atualização")
    void deveLancarExcecaoQuandoEmailExisteParaOutroCliente() {
        String email = "cliente@email.com";
        Integer clienteId = 1;
        Cliente outroCliente = Cliente.builder()
                .id(2)
                .email(email)
                .build();

        when(clienteRepositoryPort.findByEmail(email)).thenReturn(Optional.of(outroCliente));

        DuplicateResourceException exception = assertThrows(
            DuplicateResourceException.class,
            () -> validator.validateEmailUniqueForUpdate(email, clienteId)
        );

        assertTrue(exception.getMessage().contains("Email já cadastrado"));
    }

    @Test
    @DisplayName("Deve validar CPF único com sucesso quando CPF não existe")
    void deveValidarCpfUnicoComSucesso() {
        String cpf = "12345678900";
        when(clienteRepositoryPort.findByCpf(cpf)).thenReturn(Optional.empty());

        assertDoesNotThrow(() -> validator.validateCpfUnique(cpf));
    }

    @Test
    @DisplayName("Deve lançar exceção quando CPF já existe")
    void deveLancarExcecaoQuandoCpfJaExiste() {
        String cpf = "12345678900";
        Cliente cliente = Cliente.builder()
                .id(1)
                .cpf(cpf)
                .build();

        when(clienteRepositoryPort.findByCpf(cpf)).thenReturn(Optional.of(cliente));

        DuplicateResourceException exception = assertThrows(
            DuplicateResourceException.class,
            () -> validator.validateCpfUnique(cpf)
        );

        assertTrue(exception.getMessage().contains("CPF já cadastrado"));
    }

    @Test
    @DisplayName("Deve permitir CPF null na validação")
    void devePermitirCpfNull() {
        assertDoesNotThrow(() -> validator.validateCpfUnique(null));
    }
}

