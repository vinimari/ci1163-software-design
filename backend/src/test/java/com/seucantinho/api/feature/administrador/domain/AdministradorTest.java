package com.seucantinho.api.feature.administrador.domain;

import com.seucantinho.api.feature.usuario.domain.enums.PerfilUsuarioEnum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Testes da classe Administrador")
class AdministradorTest {

    @Test
    @DisplayName("Deve criar administrador com atributos básicos")
    void deveCriarAdministradorComAtributosBasicos() {
        // Arrange & Act
        Administrador admin = new Administrador();
        admin.setNome("Admin Master");
        admin.setEmail("admin@empresa.com");
        admin.setSenhaHash("hash123");

        // Assert
        assertThat(admin).isNotNull();
        assertThat(admin.getNome()).isEqualTo("Admin Master");
        assertThat(admin.getEmail()).isEqualTo("admin@empresa.com");
        assertThat(admin.getSenhaHash()).isEqualTo("hash123");
    }

    @Test
    @DisplayName("Deve retornar perfil ADMIN")
    void deveRetornarPerfilAdmin() {
        // Arrange
        Administrador admin = new Administrador();
        admin.setNome("Admin Master");
        admin.setEmail("admin@empresa.com");
        admin.setSenhaHash("hash123");

        // Act
        PerfilUsuarioEnum perfil = admin.getPerfil();

        // Assert
        assertThat(perfil).isEqualTo(PerfilUsuarioEnum.ADMIN);
    }

    @Test
    @DisplayName("Deve herdar validações da classe Usuario")
    void deveHerdarValidacoesDaClasseUsuario() {
        // Arrange
        Administrador admin = new Administrador();
        admin.setNome("Admin Master");
        admin.setEmail("admin@empresa.com");
        admin.setSenhaHash("hash123");
        admin.setCpf("12345678901");

        // Act & Assert
        assertThatCode(() -> admin.validar()).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Deve herdar validação de email da classe Usuario")
    void deveHerdarValidacaoDeEmailDaClasseUsuario() {
        // Arrange
        Administrador admin = new Administrador();
        admin.setNome("Admin Master");
        admin.setEmail("email_invalido");
        admin.setSenhaHash("hash123");

        // Act & Assert
        assertThatThrownBy(() -> admin.validarEmail())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Email inválido");
    }

    @Test
    @DisplayName("Deve ser instância de Usuario")
    void deveSerInstanciaDeUsuario() {
        // Arrange & Act
        Administrador admin = new Administrador();
        admin.setNome("Admin Master");
        admin.setEmail("admin@empresa.com");
        admin.setSenhaHash("hash123");

        // Assert
        assertThat(admin).isInstanceOf(com.seucantinho.api.feature.usuario.domain.Usuario.class);
    }

    @Test
    @DisplayName("Deve permitir definir ID do administrador")
    void devePermitirDefinirIdDoAdministrador() {
        // Arrange & Act
        Administrador admin = new Administrador();
        admin.setId(1);
        admin.setNome("Admin Master");
        admin.setEmail("admin@empresa.com");
        admin.setSenhaHash("hash123");

        // Assert
        assertThat(admin.getId()).isEqualTo(1);
    }

    @Test
    @DisplayName("Deve permitir definir administrador como ativo")
    void devePermitirDefinirAdministradorComoAtivo() {
        // Arrange & Act
        Administrador admin = new Administrador();
        admin.setNome("Admin Master");
        admin.setEmail("admin@empresa.com");
        admin.setSenhaHash("hash123");
        admin.setAtivo(true);

        // Assert
        assertThat(admin.getAtivo()).isTrue();
    }

    @Test
    @DisplayName("Deve permitir definir administrador como inativo")
    void devePermitirDefinirAdministradorComoInativo() {
        // Arrange & Act
        Administrador admin = new Administrador();
        admin.setNome("Admin Master");
        admin.setEmail("admin@empresa.com");
        admin.setSenhaHash("hash123");
        admin.setAtivo(false);

        // Assert
        assertThat(admin.getAtivo()).isFalse();
    }

    @Test
    @DisplayName("Deve permitir criar administrador com CPF")
    void devePermitirCriarAdministradorComCpf() {
        // Arrange & Act
        Administrador admin = new Administrador();
        admin.setNome("Admin Master");
        admin.setEmail("admin@empresa.com");
        admin.setSenhaHash("hash123");
        admin.setCpf("12345678901");

        // Assert
        assertThat(admin.getCpf()).isEqualTo("12345678901");
    }

    @Test
    @DisplayName("Deve permitir criar administrador com telefone")
    void devePermitirCriarAdministradorComTelefone() {
        // Arrange & Act
        Administrador admin = new Administrador();
        admin.setNome("Admin Master");
        admin.setEmail("admin@empresa.com");
        admin.setSenhaHash("hash123");
        admin.setTelefone("(41) 99999-9999");

        // Assert
        assertThat(admin.getTelefone()).isEqualTo("(41) 99999-9999");
    }

    @Test
    @DisplayName("Deve lançar exceção ao validar administrador sem nome")
    void deveLancarExcecaoAoValidarAdministradorSemNome() {
        // Arrange
        Administrador admin = new Administrador();
        admin.setNome(null);
        admin.setEmail("admin@empresa.com");
        admin.setSenhaHash("hash123");

        // Act & Assert
        assertThatThrownBy(() -> admin.validar())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Nome é obrigatório");
    }
}
