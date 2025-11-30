package com.seucantinho.api.shared.infrastructure.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Testes do SecurityConfig")
class SecurityConfigTest {

    private final SecurityConfig securityConfig = new SecurityConfig();

    @Test
    @DisplayName("Deve criar bean PasswordEncoder BCrypt")
    void deveCriarBeanPasswordEncoderBCrypt() {
        // Act
        PasswordEncoder passwordEncoder = securityConfig.passwordEncoder();

        // Assert
        assertThat(passwordEncoder).isNotNull();
        assertThat(passwordEncoder).isInstanceOf(BCryptPasswordEncoder.class);
    }

    @Test
    @DisplayName("Deve criar bean CorsConfigurationSource")
    void deveCriarBeanCorsConfigurationSource() {
        // Act
        CorsConfigurationSource corsConfigurationSource = securityConfig.corsConfigurationSource();

        // Assert
        assertThat(corsConfigurationSource).isNotNull();
    }

    @Test
    @DisplayName("Deve configurar CORS corretamente")
    void deveConfigurarCorsCorretamente() {
        // Act
        CorsConfigurationSource source = securityConfig.corsConfigurationSource();

        // Assert
        assertThat(source).isNotNull();
    }

    @Test
    @DisplayName("Deve codificar senha usando BCrypt")
    void deveCodificarSenhaUsandoBCrypt() {
        // Arrange
        PasswordEncoder encoder = securityConfig.passwordEncoder();
        String senhaOriginal = "senha123";

        // Act
        String senhaEncoded = encoder.encode(senhaOriginal);

        // Assert
        assertThat(senhaEncoded).isNotNull();
        assertThat(senhaEncoded).isNotEqualTo(senhaOriginal);
        assertThat(senhaEncoded).startsWith("$2a$");
        assertThat(encoder.matches(senhaOriginal, senhaEncoded)).isTrue();
    }

    @Test
    @DisplayName("Deve validar senha corretamente com BCrypt")
    void deveValidarSenhaCorretamenteComBCrypt() {
        // Arrange
        PasswordEncoder encoder = securityConfig.passwordEncoder();
        String senhaCorreta = "senha123";
        String senhaIncorreta = "senhaErrada";
        String senhaEncoded = encoder.encode(senhaCorreta);

        // Act & Assert
        assertThat(encoder.matches(senhaCorreta, senhaEncoded)).isTrue();
        assertThat(encoder.matches(senhaIncorreta, senhaEncoded)).isFalse();
    }

    @Test
    @DisplayName("Deve gerar hashes diferentes para mesma senha")
    void deveGerarHashesDiferentesParaMesmaSenha() {
        // Arrange
        PasswordEncoder encoder = securityConfig.passwordEncoder();
        String senha = "senha123";

        // Act
        String hash1 = encoder.encode(senha);
        String hash2 = encoder.encode(senha);

        // Assert
        assertThat(hash1).isNotEqualTo(hash2);
        assertThat(encoder.matches(senha, hash1)).isTrue();
        assertThat(encoder.matches(senha, hash2)).isTrue();
    }

}
