package com.seucantinho.api.feature.espaco.domain.valueobject;

import com.seucantinho.api.shared.domain.exception.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Testes da classe Capacidade")
class CapacidadeTest {

    @Test
    @DisplayName("Deve criar capacidade válida")
    void deveCriarCapacidadeValida() {
        // Arrange & Act
        Capacidade capacidade = Capacidade.of(50);

        // Assert
        assertThat(capacidade).isNotNull();
        assertThat(capacidade.getQuantidade()).isEqualTo(50);
    }

    @Test
    @DisplayName("Deve criar capacidade com valor mínimo permitido")
    void deveCriarCapacidadeComValorMinimo() {
        // Arrange & Act
        Capacidade capacidade = Capacidade.of(1);

        // Assert
        assertThat(capacidade).isNotNull();
        assertThat(capacidade.getQuantidade()).isEqualTo(1);
    }

    @Test
    @DisplayName("Deve criar capacidade com valor máximo permitido")
    void deveCriarCapacidadeComValorMaximo() {
        // Arrange & Act
        Capacidade capacidade = Capacidade.of(1000);

        // Assert
        assertThat(capacidade).isNotNull();
        assertThat(capacidade.getQuantidade()).isEqualTo(1000);
    }

    @Test
    @DisplayName("Deve lançar exceção quando capacidade for nula")
    void deveLancarExcecaoQuandoCapacidadeForNula() {
        // Arrange, Act & Assert
        assertThatThrownBy(() -> Capacidade.of(null))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Capacidade não pode ser nula");
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, -10, -100})
    @DisplayName("Deve lançar exceção quando capacidade for menor que o mínimo")
    void deveLancarExcecaoQuandoCapacidadeForMenorQueMinimo(int quantidade) {
        // Arrange, Act & Assert
        assertThatThrownBy(() -> Capacidade.of(quantidade))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Capacidade deve ser no mínimo 1 pessoa(s)");
    }

    @ParameterizedTest
    @ValueSource(ints = {1001, 1500, 2000, 10000})
    @DisplayName("Deve lançar exceção quando capacidade for maior que o máximo")
    void deveLancarExcecaoQuandoCapacidadeForMaiorQueMaximo(int quantidade) {
        // Arrange, Act & Assert
        assertThatThrownBy(() -> Capacidade.of(quantidade))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Capacidade não pode exceder 1000 pessoas por questões de segurança");
    }

    @Test
    @DisplayName("Deve manter igualdade entre capacidades com mesmo valor")
    void deveManterIgualdadeEntreCapacidades() {
        // Arrange
        Capacidade capacidade1 = Capacidade.of(100);
        Capacidade capacidade2 = Capacidade.of(100);

        // Act & Assert
        assertThat(capacidade1).isEqualTo(capacidade2);
        assertThat(capacidade1.hashCode()).isEqualTo(capacidade2.hashCode());
    }

    @Test
    @DisplayName("Deve diferenciar capacidades com valores diferentes")
    void deveDiferenciarCapacidadesComValoresDiferentes() {
        // Arrange
        Capacidade capacidade1 = Capacidade.of(50);
        Capacidade capacidade2 = Capacidade.of(100);

        // Act & Assert
        assertThat(capacidade1).isNotEqualTo(capacidade2);
    }

    @Test
    @DisplayName("Deve retornar toString formatado corretamente")
    void deveRetornarToStringFormatadoCorretamente() {
        // Arrange
        Capacidade capacidade = Capacidade.of(50);

        // Act
        String resultado = capacidade.toString();

        // Assert
        assertThat(resultado).isEqualTo("50 pessoa(s)");
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 10, 50, 100, 500, 999, 1000})
    @DisplayName("Deve aceitar todos os valores válidos dentro do intervalo permitido")
    void deveAceitarValoresValidosDentroDoIntervalo(int quantidade) {
        // Arrange & Act
        Capacidade capacidade = Capacidade.of(quantidade);

        // Assert
        assertThat(capacidade).isNotNull();
        assertThat(capacidade.getQuantidade()).isEqualTo(quantidade);
    }
}
