package com.seucantinho.api.feature.filial.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Testes da classe Filial")
class FilialTest {

    @Test
    @DisplayName("Deve criar filial válida com todos os atributos obrigatórios")
    void deveCriarFilialValidaComAtributosObrigatorios() {
        // Arrange & Act
        Filial filial = Filial.builder()
                .nome("Filial Centro")
                .cidade("Curitiba")
                .estado("PR")
                .build();

        // Assert
        assertThat(filial).isNotNull();
        assertThat(filial.getNome()).isEqualTo("Filial Centro");
        assertThat(filial.getCidade()).isEqualTo("Curitiba");
        assertThat(filial.getEstado()).isEqualTo("PR");
    }

    @Test
    @DisplayName("Deve criar filial com todos os atributos incluindo opcionais")
    void deveCriarFilialComTodosOsAtributos() {
        // Arrange & Act
        Filial filial = Filial.builder()
                .id(1)
                .nome("Filial Batel")
                .cidade("Curitiba")
                .estado("PR")
                .endereco("Rua XV de Novembro, 1000")
                .telefone("(41) 3333-4444")
                .build();

        // Assert
        assertThat(filial.getId()).isEqualTo(1);
        assertThat(filial.getNome()).isEqualTo("Filial Batel");
        assertThat(filial.getCidade()).isEqualTo("Curitiba");
        assertThat(filial.getEstado()).isEqualTo("PR");
        assertThat(filial.getEndereco()).isEqualTo("Rua XV de Novembro, 1000");
        assertThat(filial.getTelefone()).isEqualTo("(41) 3333-4444");
    }

    @Test
    @DisplayName("Deve inicializar data de cadastro no @PrePersist")
    void deveInicializarDataDeCadastroNoPrePersist() {
        // Arrange
        Filial filial = Filial.builder()
                .nome("Filial Teste")
                .cidade("São Paulo")
                .estado("SP")
                .build();
        LocalDateTime antes = LocalDateTime.now();

        // Act
        filial.onCreate();
        LocalDateTime depois = LocalDateTime.now();

        // Assert
        assertThat(filial.getDataCadastro()).isNotNull();
        assertThat(filial.getDataCadastro()).isBetween(antes, depois);
    }

    @Test
    @DisplayName("Deve inicializar lista de espaços vazia")
    void deveInicializarListaDeEspacosVazia() {
        // Arrange & Act
        Filial filial = Filial.builder()
                .nome("Filial Teste")
                .cidade("Curitiba")
                .estado("PR")
                .build();

        // Assert
        assertThat(filial.getEspacos()).isNotNull();
        assertThat(filial.getEspacos()).isEmpty();
    }

    @Test
    @DisplayName("Deve inicializar lista de funcionários vazia")
    void deveInicializarListaDeFuncionariosVazia() {
        // Arrange & Act
        Filial filial = Filial.builder()
                .nome("Filial Teste")
                .cidade("Curitiba")
                .estado("PR")
                .build();

        // Assert
        assertThat(filial.getFuncionarios()).isNotNull();
        assertThat(filial.getFuncionarios()).isEmpty();
    }

    @Test
    @DisplayName("Deve permitir criar filial sem endereço")
    void devePermitirCriarFilialSemEndereco() {
        // Arrange & Act
        Filial filial = Filial.builder()
                .nome("Filial Teste")
                .cidade("Curitiba")
                .estado("PR")
                .build();

        // Assert
        assertThat(filial.getEndereco()).isNull();
    }

    @Test
    @DisplayName("Deve permitir criar filial sem telefone")
    void devePermitirCriarFilialSemTelefone() {
        // Arrange & Act
        Filial filial = Filial.builder()
                .nome("Filial Teste")
                .cidade("Curitiba")
                .estado("PR")
                .build();

        // Assert
        assertThat(filial.getTelefone()).isNull();
    }

    @Test
    @DisplayName("Deve aceitar estado com 2 caracteres")
    void deveAceitarEstadoCom2Caracteres() {
        // Arrange & Act
        Filial filial = Filial.builder()
                .nome("Filial Teste")
                .cidade("São Paulo")
                .estado("SP")
                .build();

        // Assert
        assertThat(filial.getEstado()).isEqualTo("SP");
        assertThat(filial.getEstado()).hasSize(2);
    }

    @Test
    @DisplayName("Deve aceitar diferentes UFs brasileiras")
    void deveAceitarDiferentesUFsBrasileiras() {
        // Arrange & Act
        Filial filialPR = criarFilialComEstado("PR");
        Filial filialSP = criarFilialComEstado("SP");
        Filial filialRJ = criarFilialComEstado("RJ");
        Filial filialMG = criarFilialComEstado("MG");

        // Assert
        assertThat(filialPR.getEstado()).isEqualTo("PR");
        assertThat(filialSP.getEstado()).isEqualTo("SP");
        assertThat(filialRJ.getEstado()).isEqualTo("RJ");
        assertThat(filialMG.getEstado()).isEqualTo("MG");
    }

    @Test
    @DisplayName("Deve permitir atualizar nome da filial")
    void devePermitirAtualizarNomeDaFilial() {
        // Arrange
        Filial filial = Filial.builder()
                .nome("Filial Antiga")
                .cidade("Curitiba")
                .estado("PR")
                .build();

        // Act
        filial.setNome("Filial Nova");

        // Assert
        assertThat(filial.getNome()).isEqualTo("Filial Nova");
    }

    @Test
    @DisplayName("Deve permitir atualizar endereço da filial")
    void devePermitirAtualizarEnderecoDaFilial() {
        // Arrange
        Filial filial = Filial.builder()
                .nome("Filial Teste")
                .cidade("Curitiba")
                .estado("PR")
                .endereco("Endereço Antigo")
                .build();

        // Act
        filial.setEndereco("Novo Endereço Completo");

        // Assert
        assertThat(filial.getEndereco()).isEqualTo("Novo Endereço Completo");
    }

    @Test
    @DisplayName("Deve permitir atualizar telefone da filial")
    void devePermitirAtualizarTelefoneDaFilial() {
        // Arrange
        Filial filial = Filial.builder()
                .nome("Filial Teste")
                .cidade("Curitiba")
                .estado("PR")
                .telefone("(41) 1111-1111")
                .build();

        // Act
        filial.setTelefone("(41) 9999-9999");

        // Assert
        assertThat(filial.getTelefone()).isEqualTo("(41) 9999-9999");
    }

    @Test
    @DisplayName("Deve aceitar nome de filial com até 100 caracteres")
    void deveAceitarNomeDeFilialAte100Caracteres() {
        // Arrange
        String nomeLongo = "Filial " + "A".repeat(93); // Total: 100 caracteres

        // Act
        Filial filial = Filial.builder()
                .nome(nomeLongo)
                .cidade("Curitiba")
                .estado("PR")
                .build();

        // Assert
        assertThat(filial.getNome()).hasSize(100);
    }

    @Test
    @DisplayName("Deve aceitar cidade com até 100 caracteres")
    void deveAceitarCidadeAte100Caracteres() {
        // Arrange
        String cidadeLonga = "São " + "A".repeat(96); // Total: 100 caracteres

        // Act
        Filial filial = Filial.builder()
                .nome("Filial Teste")
                .cidade(cidadeLonga)
                .estado("PR")
                .build();

        // Assert
        assertThat(filial.getCidade()).hasSize(100);
    }

    @Test
    @DisplayName("Deve aceitar telefone com até 20 caracteres")
    void deveAceitarTelefoneAte20Caracteres() {
        // Arrange
        String telefone = "(41) 99999-9999"; // 16 caracteres

        // Act
        Filial filial = Filial.builder()
                .nome("Filial Teste")
                .cidade("Curitiba")
                .estado("PR")
                .telefone(telefone)
                .build();

        // Assert
        assertThat(filial.getTelefone()).isEqualTo(telefone);
        assertThat(filial.getTelefone().length()).isLessThanOrEqualTo(20);
    }

    // Método auxiliar
    private Filial criarFilialComEstado(String estado) {
        return Filial.builder()
                .nome("Filial " + estado)
                .cidade("Cidade Teste")
                .estado(estado)
                .build();
    }
}
