package com.seucantinho.api.feature.cliente.domain;

import com.seucantinho.api.feature.usuario.domain.enums.PerfilUsuarioEnum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Testes da classe Cliente")
class ClienteTest {

    @Test
    @DisplayName("Deve criar cliente com atributos básicos")
    void deveCriarClienteComAtributosBasicos() {
        // Arrange & Act
        Cliente cliente = Cliente.builder()
                .nome("Maria Silva")
                .email("maria@email.com")
                .senhaHash("hash123")
                .cpf("12345678901")
                .telefone("(41) 99999-9999")
                .build();

        // Assert
        assertThat(cliente).isNotNull();
        assertThat(cliente.getNome()).isEqualTo("Maria Silva");
        assertThat(cliente.getEmail()).isEqualTo("maria@email.com");
        assertThat(cliente.getSenhaHash()).isEqualTo("hash123");
        assertThat(cliente.getCpf()).isEqualTo("12345678901");
        assertThat(cliente.getTelefone()).isEqualTo("(41) 99999-9999");
    }

    @Test
    @DisplayName("Deve retornar perfil CLIENTE")
    void deveRetornarPerfilCliente() {
        // Arrange
        Cliente cliente = Cliente.builder()
                .nome("Maria Silva")
                .email("maria@email.com")
                .senhaHash("hash123")
                .build();

        // Act
        PerfilUsuarioEnum perfil = cliente.getPerfil();

        // Assert
        assertThat(perfil).isEqualTo(PerfilUsuarioEnum.CLIENTE);
    }

    @Test
    @DisplayName("Deve inicializar lista de reservas vazia")
    void deveInicializarListaDeReservasVazia() {
        // Arrange & Act
        Cliente cliente = Cliente.builder()
                .nome("Maria Silva")
                .email("maria@email.com")
                .senhaHash("hash123")
                .build();

        // Assert
        assertThat(cliente.getReservas()).isNotNull();
        assertThat(cliente.getReservas()).isEmpty();
    }

    @Test
    @DisplayName("Deve permitir criar cliente sem CPF")
    void devePermitirCriarClienteSemCpf() {
        // Arrange & Act
        Cliente cliente = Cliente.builder()
                .nome("Maria Silva")
                .email("maria@email.com")
                .senhaHash("hash123")
                .build();

        // Assert
        assertThat(cliente.getCpf()).isNull();
    }

    @Test
    @DisplayName("Deve permitir criar cliente sem telefone")
    void devePermitirCriarClienteSemTelefone() {
        // Arrange & Act
        Cliente cliente = Cliente.builder()
                .nome("Maria Silva")
                .email("maria@email.com")
                .senhaHash("hash123")
                .build();

        // Assert
        assertThat(cliente.getTelefone()).isNull();
    }

    @Test
    @DisplayName("Deve herdar validações da classe Usuario")
    void deveHerdarValidacoesDaClasseUsuario() {
        // Arrange
        Cliente cliente = Cliente.builder()
                .nome("Maria Silva")
                .email("maria@email.com")
                .senhaHash("hash123")
                .cpf("12345678901")
                .build();

        // Act & Assert
        assertThatCode(() -> cliente.validar()).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Deve herdar validação de email da classe Usuario")
    void deveHerdarValidacaoDeEmailDaClasseUsuario() {
        // Arrange
        Cliente cliente = Cliente.builder()
                .nome("Maria Silva")
                .email("email_invalido")
                .senhaHash("hash123")
                .build();

        // Act & Assert
        assertThatThrownBy(() -> cliente.validarEmail())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Email inválido");
    }

    @Test
    @DisplayName("Deve ser instância de Usuario")
    void deveSerInstanciaDeUsuario() {
        // Arrange & Act
        Cliente cliente = Cliente.builder()
                .nome("Maria Silva")
                .email("maria@email.com")
                .senhaHash("hash123")
                .build();

        // Assert
        assertThat(cliente).isInstanceOf(com.seucantinho.api.feature.usuario.domain.Usuario.class);
    }

    @Test
    @DisplayName("Deve permitir definir ID do cliente")
    void devePermitirDefinirIdDoCliente() {
        // Arrange & Act
        Cliente cliente = Cliente.builder()
                .id(1)
                .nome("Maria Silva")
                .email("maria@email.com")
                .senhaHash("hash123")
                .build();

        // Assert
        assertThat(cliente.getId()).isEqualTo(1);
    }

    @Test
    @DisplayName("Deve permitir definir cliente como ativo")
    void devePermitirDefinirClienteComoAtivo() {
        // Arrange & Act
        Cliente cliente = Cliente.builder()
                .nome("Maria Silva")
                .email("maria@email.com")
                .senhaHash("hash123")
                .ativo(true)
                .build();

        // Assert
        assertThat(cliente.getAtivo()).isTrue();
    }

    @Test
    @DisplayName("Deve permitir definir cliente como inativo")
    void devePermitirDefinirClienteComoInativo() {
        // Arrange & Act
        Cliente cliente = Cliente.builder()
                .nome("Maria Silva")
                .email("maria@email.com")
                .senhaHash("hash123")
                .ativo(false)
                .build();

        // Assert
        assertThat(cliente.getAtivo()).isFalse();
    }
}
