package com.seucantinho.api.feature.auth.infrastructure.adapter.out;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do PasswordEncoderAdapter")
class PasswordEncoderAdapterTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    private PasswordEncoderAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new PasswordEncoderAdapter(passwordEncoder);
    }

    @Test
    @DisplayName("Deve codificar senha usando passwordEncoder")
    void deveCodificarSenha() {
        String rawPassword = "senha123";
        String encodedPassword = "$2a$10$encodedPassword";

        when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);

        String result = adapter.encode(rawPassword);

        assertEquals(encodedPassword, result);
        verify(passwordEncoder).encode(rawPassword);
    }

    @Test
    @DisplayName("Deve verificar se senhas correspondem usando passwordEncoder")
    void deveVerificarSenhasCorrespondem() {
        String rawPassword = "senha123";
        String encodedPassword = "$2a$10$encodedPassword";

        when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(true);

        boolean result = adapter.matches(rawPassword, encodedPassword);

        assertTrue(result);
        verify(passwordEncoder).matches(rawPassword, encodedPassword);
    }

    @Test
    @DisplayName("Deve retornar false quando senhas não correspondem")
    void deveRetornarFalseQuandoSenhasNaoCorrespondem() {
        String rawPassword = "senha123";
        String encodedPassword = "$2a$10$encodedPassword";

        when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(false);

        boolean result = adapter.matches(rawPassword, encodedPassword);

        assertFalse(result);
        verify(passwordEncoder).matches(rawPassword, encodedPassword);
    }
}

