package com.seucantinho.api.validator;

import com.seucantinho.api.domain.entity.Cliente;
import com.seucantinho.api.exception.DuplicateResourceException;
import com.seucantinho.api.repository.ClienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClienteValidatorTest {

    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private ClienteValidator clienteValidator;

    private Cliente cliente;

    @BeforeEach
    void setUp() {
        cliente = Cliente.builder()
                .id(1)
                .nome("João Silva")
                .email("joao@email.com")
                .cpf("12345678900")
                .build();
    }

    @Test
    void shouldValidateEmailUnique_WhenEmailDoesNotExist() {
        // Given
        String email = "novo@email.com";
        when(clienteRepository.findByEmail(email)).thenReturn(Optional.empty());

        // When & Then
        assertThatCode(() -> clienteValidator.validateEmailUnique(email))
                .doesNotThrowAnyException();

        verify(clienteRepository, times(1)).findByEmail(email);
    }

    @Test
    void shouldThrowException_WhenEmailAlreadyExists() {
        // Given
        String email = "joao@email.com";
        when(clienteRepository.findByEmail(email)).thenReturn(Optional.of(cliente));

        // When & Then
        assertThatThrownBy(() -> clienteValidator.validateEmailUnique(email))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("Email já cadastrado: joao@email.com");

        verify(clienteRepository, times(1)).findByEmail(email);
    }

    @Test
    void shouldValidateEmailUniqueForUpdate_WhenSameClient() {
        // Given
        String email = "joao@email.com";
        Integer clienteId = 1;
        when(clienteRepository.findByEmail(email)).thenReturn(Optional.of(cliente));

        // When & Then
        assertThatCode(() -> clienteValidator.validateEmailUniqueForUpdate(email, clienteId))
                .doesNotThrowAnyException();

        verify(clienteRepository, times(1)).findByEmail(email);
    }

    @Test
    void shouldValidateEmailUniqueForUpdate_WhenEmailDoesNotExist() {
        // Given
        String email = "novo@email.com";
        Integer clienteId = 1;
        when(clienteRepository.findByEmail(email)).thenReturn(Optional.empty());

        // When & Then
        assertThatCode(() -> clienteValidator.validateEmailUniqueForUpdate(email, clienteId))
                .doesNotThrowAnyException();

        verify(clienteRepository, times(1)).findByEmail(email);
    }

    @Test
    void shouldThrowException_WhenEmailBelongsToDifferentClient() {
        // Given
        String email = "joao@email.com";
        Integer differentClienteId = 2;
        when(clienteRepository.findByEmail(email)).thenReturn(Optional.of(cliente));

        // When & Then
        assertThatThrownBy(() -> clienteValidator.validateEmailUniqueForUpdate(email, differentClienteId))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("Email já cadastrado: joao@email.com");

        verify(clienteRepository, times(1)).findByEmail(email);
    }

    @Test
    void shouldValidateCpfUnique_WhenCpfDoesNotExist() {
        // Given
        String cpf = "98765432100";
        when(clienteRepository.findByCpf(cpf)).thenReturn(Optional.empty());

        // When & Then
        assertThatCode(() -> clienteValidator.validateCpfUnique(cpf))
                .doesNotThrowAnyException();

        verify(clienteRepository, times(1)).findByCpf(cpf);
    }

    @Test
    void shouldValidateCpfUnique_WhenCpfIsNull() {
        // Given
        String cpf = null;

        // When & Then
        assertThatCode(() -> clienteValidator.validateCpfUnique(cpf))
                .doesNotThrowAnyException();

        verify(clienteRepository, never()).findByCpf(any());
    }

    @Test
    void shouldThrowException_WhenCpfAlreadyExists() {
        // Given
        String cpf = "12345678900";
        when(clienteRepository.findByCpf(cpf)).thenReturn(Optional.of(cliente));

        // When & Then
        assertThatThrownBy(() -> clienteValidator.validateCpfUnique(cpf))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("CPF já cadastrado: 12345678900");

        verify(clienteRepository, times(1)).findByCpf(cpf);
    }
}
