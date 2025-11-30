package com.seucantinho.api.feature.usuario.domain;

import com.seucantinho.api.feature.usuario.domain.enums.PerfilUsuarioEnum;
import com.seucantinho.api.feature.cliente.domain.Cliente;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Testes da classe Usuario")
class UsuarioTest {

    @Test
    @DisplayName("Deve criar usuário com atributos básicos obrigatórios")
    void deveCriarUsuarioComAtributosBasicosObrigatorios() {
        // Arrange & Act
        Cliente usuario = Cliente.builder()
                .nome("João Silva")
                .email("joao@email.com")
                .senhaHash("hash123")
                .build();

        // Assert
        assertThat(usuario).isNotNull();
        assertThat(usuario.getNome()).isEqualTo("João Silva");
        assertThat(usuario.getEmail()).isEqualTo("joao@email.com");
        assertThat(usuario.getSenhaHash()).isEqualTo("hash123");
    }

    @Test
    @DisplayName("Deve inicializar usuário como ativo por padrão")
    void deveInicializarUsuarioComoAtivoPorPadrao() {
        // Arrange
        Cliente usuario = Cliente.builder()
                .nome("João Silva")
                .email("joao@email.com")
                .senhaHash("hash123")
                .build();

        // Act
        usuario.onCreate();

        // Assert
        assertThat(usuario.getAtivo()).isTrue();
    }

    @Test
    @DisplayName("Deve inicializar data de cadastro no @PrePersist")
    void deveInicializarDataDeCadastroNoPrePersist() {
        // Arrange
        Cliente usuario = Cliente.builder()
                .nome("João Silva")
                .email("joao@email.com")
                .senhaHash("hash123")
                .build();
        LocalDateTime antes = LocalDateTime.now();

        // Act
        usuario.onCreate();
        LocalDateTime depois = LocalDateTime.now();

        // Assert
        assertThat(usuario.getDataCadastro()).isNotNull();
        assertThat(usuario.getDataCadastro()).isBetween(antes, depois);
    }

    @Test
    @DisplayName("Deve validar usuário com dados corretos sem lançar exceção")
    void deveValidarUsuarioComDadosCorretosSemLancarExcecao() {
        // Arrange
        Cliente usuario = Cliente.builder()
                .nome("João Silva")
                .email("joao@email.com")
                .senhaHash("hash123")
                .cpf("12345678901")
                .build();

        // Act & Assert
        assertThatCode(() -> usuario.validar()).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Deve lançar exceção ao validar usuário sem nome")
    void deveLancarExcecaoAoValidarUsuarioSemNome() {
        // Arrange
        Cliente usuario = Cliente.builder()
                .nome(null)
                .email("joao@email.com")
                .senhaHash("hash123")
                .build();

        // Act & Assert
        assertThatThrownBy(() -> usuario.validar())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Nome é obrigatório");
    }

    @Test
    @DisplayName("Deve lançar exceção ao validar usuário com nome vazio")
    void deveLancarExcecaoAoValidarUsuarioComNomeVazio() {
        // Arrange
        Cliente usuario = Cliente.builder()
                .nome("   ")
                .email("joao@email.com")
                .senhaHash("hash123")
                .build();

        // Act & Assert
        assertThatThrownBy(() -> usuario.validar())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Nome é obrigatório");
    }

    @Test
    @DisplayName("Deve lançar exceção ao validar email nulo")
    void deveLancarExcecaoAoValidarEmailNulo() {
        // Arrange
        Cliente usuario = Cliente.builder()
                .nome("João Silva")
                .email(null)
                .senhaHash("hash123")
                .build();

        // Act & Assert
        assertThatThrownBy(() -> usuario.validarEmail())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Email é obrigatório");
    }

    @Test
    @DisplayName("Deve lançar exceção ao validar email vazio")
    void deveLancarExcecaoAoValidarEmailVazio() {
        // Arrange
        Cliente usuario = Cliente.builder()
                .nome("João Silva")
                .email("   ")
                .senhaHash("hash123")
                .build();

        // Act & Assert
        assertThatThrownBy(() -> usuario.validarEmail())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Email é obrigatório");
    }

    @Test
    @DisplayName("Deve lançar exceção ao validar email inválido")
    void deveLancarExcecaoAoValidarEmailInvalido() {
        // Arrange
        Cliente usuario = Cliente.builder()
                .nome("João Silva")
                .email("email_invalido")
                .senhaHash("hash123")
                .build();

        // Act & Assert
        assertThatThrownBy(() -> usuario.validarEmail())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Email inválido");
    }

    @Test
    @DisplayName("Deve validar emails válidos sem lançar exceção")
    void deveValidarEmailsValidosSemLancarExcecao() {
        // Arrange
        Cliente usuario1 = criarUsuarioComEmail("joao@email.com");
        Cliente usuario2 = criarUsuarioComEmail("maria.silva@empresa.com.br");
        Cliente usuario3 = criarUsuarioComEmail("usuario+tag@dominio.co");

        // Act & Assert
        assertThatCode(() -> usuario1.validarEmail()).doesNotThrowAnyException();
        assertThatCode(() -> usuario2.validarEmail()).doesNotThrowAnyException();
        assertThatCode(() -> usuario3.validarEmail()).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Deve validar CPF com 11 dígitos")
    void deveValidarCpfCom11Digitos() {
        // Arrange
        Cliente usuario = Cliente.builder()
                .nome("João Silva")
                .email("joao@email.com")
                .senhaHash("hash123")
                .cpf("12345678901")
                .build();

        // Act & Assert
        assertThatCode(() -> usuario.validarCpf()).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Deve lançar exceção ao validar CPF com formato inválido")
    void deveLancarExcecaoAoValidarCpfComFormatoInvalido() {
        // Arrange
        Cliente usuario = Cliente.builder()
                .nome("João Silva")
                .email("joao@email.com")
                .senhaHash("hash123")
                .cpf("123.456.789-01")
                .build();

        // Act & Assert
        assertThatThrownBy(() -> usuario.validarCpf())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("CPF deve conter 11 dígitos");
    }

    @Test
    @DisplayName("Deve lançar exceção ao validar CPF com menos de 11 dígitos")
    void deveLancarExcecaoAoValidarCpfComMenosDe11Digitos() {
        // Arrange
        Cliente usuario = Cliente.builder()
                .nome("João Silva")
                .email("joao@email.com")
                .senhaHash("hash123")
                .cpf("123456789")
                .build();

        // Act & Assert
        assertThatThrownBy(() -> usuario.validarCpf())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("CPF deve conter 11 dígitos");
    }

    @Test
    @DisplayName("Deve permitir CPF nulo")
    void devePermitirCpfNulo() {
        // Arrange
        Cliente usuario = Cliente.builder()
                .nome("João Silva")
                .email("joao@email.com")
                .senhaHash("hash123")
                .cpf(null)
                .build();

        // Act & Assert
        assertThatCode(() -> usuario.validarCpf()).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Deve permitir criar usuário com telefone")
    void devePermitirCriarUsuarioComTelefone() {
        // Arrange & Act
        Cliente usuario = Cliente.builder()
                .nome("João Silva")
                .email("joao@email.com")
                .senhaHash("hash123")
                .telefone("(41) 99999-9999")
                .build();

        // Assert
        assertThat(usuario.getTelefone()).isEqualTo("(41) 99999-9999");
    }

    @Test
    @DisplayName("Deve permitir criar usuário sem telefone")
    void devePermitirCriarUsuarioSemTelefone() {
        // Arrange & Act
        Cliente usuario = Cliente.builder()
                .nome("João Silva")
                .email("joao@email.com")
                .senhaHash("hash123")
                .build();

        // Assert
        assertThat(usuario.getTelefone()).isNull();
    }

    @Test
    @DisplayName("Deve retornar perfil CLIENTE para Cliente")
    void deveRetornarPerfilClienteParaCliente() {
        // Arrange
        Cliente cliente = Cliente.builder()
                .nome("João Silva")
                .email("joao@email.com")
                .senhaHash("hash123")
                .build();

        // Act
        PerfilUsuarioEnum perfil = cliente.getPerfil();

        // Assert
        assertThat(perfil).isEqualTo(PerfilUsuarioEnum.CLIENTE);
    }

    @Test
    @DisplayName("Deve permitir definir usuário como inativo")
    void devePermitirDefinirUsuarioComoInativo() {
        // Arrange
        Cliente usuario = Cliente.builder()
                .nome("João Silva")
                .email("joao@email.com")
                .senhaHash("hash123")
                .ativo(false)
                .build();

        // Act & Assert
        assertThat(usuario.getAtivo()).isFalse();
    }

    @Test
    @DisplayName("Deve aceitar nome com até 150 caracteres")
    void deveAceitarNomeAte150Caracteres() {
        // Arrange
        String nomeLongo = "A".repeat(150);

        // Act
        Cliente usuario = Cliente.builder()
                .nome(nomeLongo)
                .email("joao@email.com")
                .senhaHash("hash123")
                .build();

        // Assert
        assertThat(usuario.getNome()).hasSize(150);
    }

    @Test
    @DisplayName("Deve aceitar email com até 150 caracteres")
    void deveAceitarEmailAte150Caracteres() {
        // Arrange
        String emailLongo = "a".repeat(130) + "@dominio.com"; // 142 caracteres

        // Act
        Cliente usuario = Cliente.builder()
                .nome("João Silva")
                .email(emailLongo)
                .senhaHash("hash123")
                .build();

        // Assert
        assertThat(usuario.getEmail()).hasSize(142);
    }

    @Test
    @DisplayName("Deve aceitar telefone com até 20 caracteres")
    void deveAceitarTelefoneAte20Caracteres() {
        // Arrange
        String telefone = "+55 (41) 99999-9999"; // 20 caracteres

        // Act
        Cliente usuario = Cliente.builder()
                .nome("João Silva")
                .email("joao@email.com")
                .senhaHash("hash123")
                .telefone(telefone)
                .build();

        // Assert
        assertThat(usuario.getTelefone()).isEqualTo(telefone);
        assertThat(usuario.getTelefone().length()).isLessThanOrEqualTo(20);
    }

    // Método auxiliar
    private Cliente criarUsuarioComEmail(String email) {
        return Cliente.builder()
                .nome("João Silva")
                .email(email)
                .senhaHash("hash123")
                .build();
    }
}
