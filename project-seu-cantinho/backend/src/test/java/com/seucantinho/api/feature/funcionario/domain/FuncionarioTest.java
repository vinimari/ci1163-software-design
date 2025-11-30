package com.seucantinho.api.feature.funcionario.domain;

import com.seucantinho.api.feature.filial.domain.Filial;
import com.seucantinho.api.feature.usuario.domain.enums.PerfilUsuarioEnum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Testes da classe Funcionario")
class FuncionarioTest {

    @Test
    @DisplayName("Deve criar funcionário com atributos básicos")
    void deveCriarFuncionarioComAtributosBasicos() {
        // Arrange
        Filial filial = criarFilial();

        // Act
        Funcionario funcionario = new Funcionario();
        funcionario.setNome("Carlos Santos");
        funcionario.setEmail("carlos@empresa.com");
        funcionario.setSenhaHash("hash123");
        funcionario.setMatricula("FUN001");
        funcionario.setFilial(filial);

        // Assert
        assertThat(funcionario).isNotNull();
        assertThat(funcionario.getNome()).isEqualTo("Carlos Santos");
        assertThat(funcionario.getEmail()).isEqualTo("carlos@empresa.com");
        assertThat(funcionario.getSenhaHash()).isEqualTo("hash123");
        assertThat(funcionario.getMatricula()).isEqualTo("FUN001");
        assertThat(funcionario.getFilial()).isEqualTo(filial);
    }

    @Test
    @DisplayName("Deve retornar perfil FUNCIONARIO")
    void deveRetornarPerfilFuncionario() {
        // Arrange
        Funcionario funcionario = new Funcionario();
        funcionario.setNome("Carlos Santos");
        funcionario.setEmail("carlos@empresa.com");
        funcionario.setSenhaHash("hash123");

        // Act
        PerfilUsuarioEnum perfil = funcionario.getPerfil();

        // Assert
        assertThat(perfil).isEqualTo(PerfilUsuarioEnum.FUNCIONARIO);
    }

    @Test
    @DisplayName("Deve permitir criar funcionário sem matrícula")
    void devePermitirCriarFuncionarioSemMatricula() {
        // Arrange & Act
        Funcionario funcionario = new Funcionario();
        funcionario.setNome("Carlos Santos");
        funcionario.setEmail("carlos@empresa.com");
        funcionario.setSenhaHash("hash123");

        // Assert
        assertThat(funcionario.getMatricula()).isNull();
    }

    @Test
    @DisplayName("Deve permitir criar funcionário sem filial")
    void devePermitirCriarFuncionarioSemFilial() {
        // Arrange & Act
        Funcionario funcionario = new Funcionario();
        funcionario.setNome("Carlos Santos");
        funcionario.setEmail("carlos@empresa.com");
        funcionario.setSenhaHash("hash123");

        // Assert
        assertThat(funcionario.getFilial()).isNull();
    }

    @Test
    @DisplayName("Deve permitir associar funcionário a uma filial")
    void devePermitirAssociarFuncionarioAFilial() {
        // Arrange
        Filial filial = criarFilial();
        Funcionario funcionario = new Funcionario();
        funcionario.setNome("Carlos Santos");
        funcionario.setEmail("carlos@empresa.com");
        funcionario.setSenhaHash("hash123");

        // Act
        funcionario.setFilial(filial);

        // Assert
        assertThat(funcionario.getFilial()).isEqualTo(filial);
        assertThat(funcionario.getFilial().getNome()).isEqualTo("Filial Centro");
    }

    @Test
    @DisplayName("Deve aceitar matrícula com até 50 caracteres")
    void deveAceitarMatriculaAte50Caracteres() {
        // Arrange
        String matriculaLonga = "MAT" + "0".repeat(47); // Total: 50 caracteres
        Funcionario funcionario = new Funcionario();
        funcionario.setNome("Carlos Santos");
        funcionario.setEmail("carlos@empresa.com");
        funcionario.setSenhaHash("hash123");

        // Act
        funcionario.setMatricula(matriculaLonga);

        // Assert
        assertThat(funcionario.getMatricula()).hasSize(50);
    }

    @Test
    @DisplayName("Deve herdar validações da classe Usuario")
    void deveHerdarValidacoesDaClasseUsuario() {
        // Arrange
        Funcionario funcionario = new Funcionario();
        funcionario.setNome("Carlos Santos");
        funcionario.setEmail("carlos@empresa.com");
        funcionario.setSenhaHash("hash123");
        funcionario.setCpf("12345678901");

        // Act & Assert
        assertThatCode(() -> funcionario.validar()).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Deve ser instância de Usuario")
    void deveSerInstanciaDeUsuario() {
        // Arrange & Act
        Funcionario funcionario = new Funcionario();
        funcionario.setNome("Carlos Santos");
        funcionario.setEmail("carlos@empresa.com");
        funcionario.setSenhaHash("hash123");

        // Assert
        assertThat(funcionario).isInstanceOf(com.seucantinho.api.feature.usuario.domain.Usuario.class);
    }

    @Test
    @DisplayName("Deve permitir alterar filial do funcionário")
    void devePermitirAlterarFilialDoFuncionario() {
        // Arrange
        Filial filialOriginal = criarFilial();
        Filial novaFilial = Filial.builder()
                .id(2)
                .nome("Filial Batel")
                .cidade("Curitiba")
                .estado("PR")
                .build();

        Funcionario funcionario = new Funcionario();
        funcionario.setNome("Carlos Santos");
        funcionario.setEmail("carlos@empresa.com");
        funcionario.setSenhaHash("hash123");
        funcionario.setFilial(filialOriginal);

        // Act
        funcionario.setFilial(novaFilial);

        // Assert
        assertThat(funcionario.getFilial()).isEqualTo(novaFilial);
        assertThat(funcionario.getFilial().getNome()).isEqualTo("Filial Batel");
    }

    @Test
    @DisplayName("Deve permitir definir funcionário como ativo")
    void devePermitirDefinirFuncionarioComoAtivo() {
        // Arrange & Act
        Funcionario funcionario = new Funcionario();
        funcionario.setNome("Carlos Santos");
        funcionario.setEmail("carlos@empresa.com");
        funcionario.setSenhaHash("hash123");
        funcionario.setAtivo(true);

        // Assert
        assertThat(funcionario.getAtivo()).isTrue();
    }

    @Test
    @DisplayName("Deve permitir diferentes formatos de matrícula")
    void devePermitirDiferentesFormatosDeMatricula() {
        // Arrange & Act
        Funcionario func1 = criarFuncionarioComMatricula("FUN001");
        Funcionario func2 = criarFuncionarioComMatricula("2024-001");
        Funcionario func3 = criarFuncionarioComMatricula("ABC123XYZ");

        // Assert
        assertThat(func1.getMatricula()).isEqualTo("FUN001");
        assertThat(func2.getMatricula()).isEqualTo("2024-001");
        assertThat(func3.getMatricula()).isEqualTo("ABC123XYZ");
    }

    // Métodos auxiliares
    private Filial criarFilial() {
        return Filial.builder()
                .id(1)
                .nome("Filial Centro")
                .cidade("Curitiba")
                .estado("PR")
                .build();
    }

    private Funcionario criarFuncionarioComMatricula(String matricula) {
        Funcionario funcionario = new Funcionario();
        funcionario.setNome("Carlos Santos");
        funcionario.setEmail("carlos@empresa.com");
        funcionario.setSenhaHash("hash123");
        funcionario.setMatricula(matricula);
        return funcionario;
    }
}
