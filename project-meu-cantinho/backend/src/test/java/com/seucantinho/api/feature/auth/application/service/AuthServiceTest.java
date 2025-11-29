package com.seucantinho.api.feature.auth.application.service;

import com.seucantinho.api.feature.auth.application.dto.LoginRequest;
import com.seucantinho.api.feature.auth.application.dto.LoginResponse;
import com.seucantinho.api.feature.auth.domain.port.out.PasswordEncoderPort;
import com.seucantinho.api.feature.cliente.domain.Cliente;
import com.seucantinho.api.feature.usuario.domain.enums.PerfilUsuarioEnum;
import com.seucantinho.api.feature.usuario.domain.port.out.UsuarioRepositoryPort;
import com.seucantinho.api.shared.domain.exception.BusinessException;
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
@DisplayName("Testes do AuthService")
class AuthServiceTest {

    @Mock
    private UsuarioRepositoryPort usuarioRepositoryPort;

    @Mock
    private PasswordEncoderPort passwordEncoderPort;

    @InjectMocks
    private AuthService authService;

    private Cliente cliente;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        cliente = Cliente.builder()
                .id(1)
                .nome("João Silva")
                .email("joao@email.com")
                .senhaHash("$2a$10$hashedPassword")
                .cpf("12345678901")
                .perfil(PerfilUsuarioEnum.CLIENTE)
                .build();

        loginRequest = new LoginRequest("joao@email.com", "senha123");
    }

    @Test
    @DisplayName("Deve fazer login com sucesso")
    void deveFazerLoginComSucesso() {
        // Arrange
        when(usuarioRepositoryPort.findByEmail(loginRequest.email())).thenReturn(Optional.of(cliente));
        when(passwordEncoderPort.matches(loginRequest.senha(), cliente.getSenhaHash())).thenReturn(true);

        // Act
        LoginResponse response = authService.login(loginRequest);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(cliente.getId());
        assertThat(response.nome()).isEqualTo(cliente.getNome());
        assertThat(response.email()).isEqualTo(cliente.getEmail());
        assertThat(response.perfil()).isEqualTo(cliente.getPerfil());
        assertThat(response.token()).startsWith("Bearer ");
        assertThat(response.token()).contains(cliente.getEmail());
        
        verify(usuarioRepositoryPort).findByEmail(loginRequest.email());
        verify(passwordEncoderPort).matches(loginRequest.senha(), cliente.getSenhaHash());
    }

    @Test
    @DisplayName("Deve lançar exceção quando usuário não existe")
    void deveLancarExcecaoQuandoUsuarioNaoExiste() {
        // Arrange
        when(usuarioRepositoryPort.findByEmail(loginRequest.email())).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Email ou senha inválidos");
        
        verify(usuarioRepositoryPort).findByEmail(loginRequest.email());
        verify(passwordEncoderPort, never()).matches(anyString(), anyString());
    }

    @Test
    @DisplayName("Deve lançar exceção quando senha está incorreta")
    void deveLancarExcecaoQuandoSenhaEstaIncorreta() {
        // Arrange
        when(usuarioRepositoryPort.findByEmail(loginRequest.email())).thenReturn(Optional.of(cliente));
        when(passwordEncoderPort.matches(loginRequest.senha(), cliente.getSenhaHash())).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Email ou senha inválidos");
        
        verify(usuarioRepositoryPort).findByEmail(loginRequest.email());
        verify(passwordEncoderPort).matches(loginRequest.senha(), cliente.getSenhaHash());
    }

    @Test
    @DisplayName("Deve gerar token com formato correto")
    void deveGerarTokenComFormatoCorreto() {
        // Arrange
        when(usuarioRepositoryPort.findByEmail(loginRequest.email())).thenReturn(Optional.of(cliente));
        when(passwordEncoderPort.matches(loginRequest.senha(), cliente.getSenhaHash())).thenReturn(true);

        // Act
        LoginResponse response = authService.login(loginRequest);

        // Assert
        assertThat(response.token()).matches("^Bearer .+$");
    }

    @Test
    @DisplayName("Deve retornar perfil correto do usuário")
    void deveRetornarPerfilCorretoDoUsuario() {
        // Arrange
        when(usuarioRepositoryPort.findByEmail(loginRequest.email())).thenReturn(Optional.of(cliente));
        when(passwordEncoderPort.matches(loginRequest.senha(), cliente.getSenhaHash())).thenReturn(true);

        // Act
        LoginResponse response = authService.login(loginRequest);

        // Assert
        assertThat(response.perfil()).isEqualTo(PerfilUsuarioEnum.CLIENTE);
    }

    @Test
    @DisplayName("Deve fazer login com email em caixa diferente")
    void deveFazerLoginComEmailEmCaixaDiferente() {
        // Arrange
        LoginRequest requestUpperCase = new LoginRequest("JOAO@EMAIL.COM", "senha123");
        when(usuarioRepositoryPort.findByEmail(requestUpperCase.email())).thenReturn(Optional.of(cliente));
        when(passwordEncoderPort.matches(requestUpperCase.senha(), cliente.getSenhaHash())).thenReturn(true);

        // Act
        LoginResponse response = authService.login(requestUpperCase);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.email()).isEqualTo(cliente.getEmail());
    }

    @Test
    @DisplayName("Deve validar senha corretamente usando encoder")
    void deveValidarSenhaCorretamenteUsandoEncoder() {
        // Arrange
        String senhaCorreta = "senha123";
        
        when(usuarioRepositoryPort.findByEmail(loginRequest.email())).thenReturn(Optional.of(cliente));
        when(passwordEncoderPort.matches(senhaCorreta, cliente.getSenhaHash())).thenReturn(true);

        // Act
        LoginResponse response = authService.login(new LoginRequest(loginRequest.email(), senhaCorreta));

        // Assert
        assertThat(response).isNotNull();
        verify(passwordEncoderPort).matches(senhaCorreta, cliente.getSenhaHash());
    }
}
