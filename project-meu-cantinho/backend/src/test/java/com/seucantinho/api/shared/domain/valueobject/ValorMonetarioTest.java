package com.seucantinho.api.shared.domain.valueobject;

import com.seucantinho.api.shared.domain.exception.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Testes da classe ValorMonetario")
class ValorMonetarioTest {

    @Test
    @DisplayName("Deve criar valor monetário válido a partir de BigDecimal")
    void deveCriarValorMonetarioValidoComBigDecimal() {
        // Arrange & Act
        ValorMonetario valor = ValorMonetario.of(new BigDecimal("100.50"));

        // Assert
        assertThat(valor).isNotNull();
        assertThat(valor.getValor()).isEqualByComparingTo(new BigDecimal("100.50"));
    }

    @Test
    @DisplayName("Deve criar valor monetário válido a partir de String")
    void deveCriarValorMonetarioValidoComString() {
        // Arrange & Act
        ValorMonetario valor = ValorMonetario.of("250.75");

        // Assert
        assertThat(valor).isNotNull();
        assertThat(valor.getValor()).isEqualByComparingTo(new BigDecimal("250.75"));
    }

    @Test
    @DisplayName("Deve criar valor monetário válido a partir de double")
    void deveCriarValorMonetarioValidoComDouble() {
        // Arrange & Act
        ValorMonetario valor = ValorMonetario.of(99.99);

        // Assert
        assertThat(valor).isNotNull();
        assertThat(valor.getValor()).isEqualByComparingTo(new BigDecimal("99.99"));
    }

    @Test
    @DisplayName("Deve criar valor monetário zero")
    void deveCriarValorMonetarioZero() {
        // Arrange & Act
        ValorMonetario valor = ValorMonetario.zero();

        // Assert
        assertThat(valor).isNotNull();
        assertThat(valor.getValor()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(valor.isZero()).isTrue();
    }

    @Test
    @DisplayName("Deve arredondar valores para duas casas decimais")
    void deveArredondarValoresParaDuasCasasDecimais() {
        // Arrange & Act
        ValorMonetario valor = ValorMonetario.of(new BigDecimal("100.999"));

        // Assert
        assertThat(valor.getValor()).isEqualByComparingTo(new BigDecimal("101.00"));
    }

    @Test
    @DisplayName("Deve lançar exceção quando valor for nulo")
    void deveLancarExcecaoQuandoValorForNulo() {
        // Arrange, Act & Assert
        assertThatThrownBy(() -> ValorMonetario.of((BigDecimal) null))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Valor monetário não pode ser nulo");
    }

    @Test
    @DisplayName("Deve lançar exceção quando valor for negativo")
    void deveLancarExcecaoQuandoValorForNegativo() {
        // Arrange, Act & Assert
        assertThatThrownBy(() -> ValorMonetario.of(new BigDecimal("-10.00")))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Valor monetário não pode ser negativo");
    }

    @Test
    @DisplayName("Deve lançar exceção quando string for inválida")
    void deveLancarExcecaoQuandoStringForInvalida() {
        // Arrange, Act & Assert
        assertThatThrownBy(() -> ValorMonetario.of("abc"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Valor monetário inválido");
    }

    @Test
    @DisplayName("Deve somar dois valores monetários")
    void deveSomarDoisValoresMonetarios() {
        // Arrange
        ValorMonetario valor1 = ValorMonetario.of("100.00");
        ValorMonetario valor2 = ValorMonetario.of("50.50");

        // Act
        ValorMonetario resultado = valor1.somar(valor2);

        // Assert
        assertThat(resultado.getValor()).isEqualByComparingTo(new BigDecimal("150.50"));
    }

    @Test
    @DisplayName("Deve somar com valor nulo sem alterar o original")
    void deveSomarComValorNuloSemAlterar() {
        // Arrange
        ValorMonetario valor = ValorMonetario.of("100.00");

        // Act
        ValorMonetario resultado = valor.somar(null);

        // Assert
        assertThat(resultado).isEqualTo(valor);
    }

    @Test
    @DisplayName("Deve subtrair dois valores monetários")
    void deveSubtrairDoisValoresMonetarios() {
        // Arrange
        ValorMonetario valor1 = ValorMonetario.of("150.00");
        ValorMonetario valor2 = ValorMonetario.of("50.50");

        // Act
        ValorMonetario resultado = valor1.subtrair(valor2);

        // Assert
        assertThat(resultado.getValor()).isEqualByComparingTo(new BigDecimal("99.50"));
    }

    @Test
    @DisplayName("Deve subtrair com valor nulo sem alterar o original")
    void deveSubtrairComValorNuloSemAlterar() {
        // Arrange
        ValorMonetario valor = ValorMonetario.of("100.00");

        // Act
        ValorMonetario resultado = valor.subtrair(null);

        // Assert
        assertThat(resultado).isEqualTo(valor);
    }

    @Test
    @DisplayName("Deve lançar exceção ao subtrair quando resultado for negativo")
    void deveLancarExcecaoAoSubtrairQuandoResultadoForNegativo() {
        // Arrange
        ValorMonetario valor1 = ValorMonetario.of("50.00");
        ValorMonetario valor2 = ValorMonetario.of("100.00");

        // Act & Assert
        assertThatThrownBy(() -> valor1.subtrair(valor2))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Resultado da subtração não pode ser negativo");
    }

    @Test
    @DisplayName("Deve calcular metade do valor corretamente")
    void deveCalcularMetadeDoValorCorretamente() {
        // Arrange
        ValorMonetario valor = ValorMonetario.of("100.00");

        // Act
        ValorMonetario metade = valor.calcularMetade();

        // Assert
        assertThat(metade.getValor()).isEqualByComparingTo(new BigDecimal("50.00"));
    }

    @Test
    @DisplayName("Deve calcular metade de valor ímpar com arredondamento")
    void deveCalcularMetadeDeValorImparComArredondamento() {
        // Arrange
        ValorMonetario valor = ValorMonetario.of("100.01");

        // Act
        ValorMonetario metade = valor.calcularMetade();

        // Assert
        assertThat(metade.getValor()).isEqualByComparingTo(new BigDecimal("50.01"));
    }

    @ParameterizedTest
    @CsvSource({
            "100.00, 50.00, true",
            "100.00, 100.00, false",
            "100.00, 150.00, false"
    })
    @DisplayName("Deve comparar se valor é maior que outro")
    void deveCompararSeValorEMaiorQueOutro(String valor1Str, String valor2Str, boolean esperado) {
        // Arrange
        ValorMonetario valor1 = ValorMonetario.of(valor1Str);
        ValorMonetario valor2 = ValorMonetario.of(valor2Str);

        // Act
        boolean resultado = valor1.isMaiorQue(valor2);

        // Assert
        assertThat(resultado).isEqualTo(esperado);
    }

    @ParameterizedTest
    @CsvSource({
            "50.00, 100.00, true",
            "100.00, 100.00, false",
            "150.00, 100.00, false"
    })
    @DisplayName("Deve comparar se valor é menor que outro")
    void deveCompararSeValorEMenorQueOutro(String valor1Str, String valor2Str, boolean esperado) {
        // Arrange
        ValorMonetario valor1 = ValorMonetario.of(valor1Str);
        ValorMonetario valor2 = ValorMonetario.of(valor2Str);

        // Act
        boolean resultado = valor1.isMenorQue(valor2);

        // Assert
        assertThat(resultado).isEqualTo(esperado);
    }

    @ParameterizedTest
    @CsvSource({
            "100.00, 100.00, true",
            "100.00, 50.00, false",
            "100.00, 150.00, false"
    })
    @DisplayName("Deve comparar se valor é igual a outro")
    void deveCompararSeValorEIgualAOutro(String valor1Str, String valor2Str, boolean esperado) {
        // Arrange
        ValorMonetario valor1 = ValorMonetario.of(valor1Str);
        ValorMonetario valor2 = ValorMonetario.of(valor2Str);

        // Act
        boolean resultado = valor1.isIgualA(valor2);

        // Assert
        assertThat(resultado).isEqualTo(esperado);
    }

    @ParameterizedTest
    @CsvSource({
            "100.00, 100.00, true",
            "150.00, 100.00, true",
            "50.00, 100.00, false"
    })
    @DisplayName("Deve comparar se valor é maior ou igual a outro")
    void deveCompararSeValorEMaiorOuIgualAOutro(String valor1Str, String valor2Str, boolean esperado) {
        // Arrange
        ValorMonetario valor1 = ValorMonetario.of(valor1Str);
        ValorMonetario valor2 = ValorMonetario.of(valor2Str);

        // Act
        boolean resultado = valor1.isMaiorOuIgualA(valor2);

        // Assert
        assertThat(resultado).isEqualTo(esperado);
    }

    @Test
    @DisplayName("Deve identificar valor positivo corretamente")
    void deveIdentificarValorPositivoCorretamente() {
        // Arrange
        ValorMonetario valor = ValorMonetario.of("100.00");

        // Act & Assert
        assertThat(valor.isPositivo()).isTrue();
    }

    @Test
    @DisplayName("Deve identificar valor zero não é positivo")
    void deveIdentificarValorZeroNaoEPositivo() {
        // Arrange
        ValorMonetario valor = ValorMonetario.zero();

        // Act & Assert
        assertThat(valor.isPositivo()).isFalse();
    }

    @Test
    @DisplayName("Deve formatar valor corretamente")
    void deveFormatarValorCorretamente() {
        // Arrange
        ValorMonetario valor = ValorMonetario.of("1250.50");

        // Act
        String formatado = valor.getValorFormatado();

        // Assert
        assertThat(formatado).isEqualTo("R$ 1250.50");
    }

    @Test
    @DisplayName("Deve retornar toString formatado")
    void deveRetornarToStringFormatado() {
        // Arrange
        ValorMonetario valor = ValorMonetario.of("999.99");

        // Act
        String resultado = valor.toString();

        // Assert
        assertThat(resultado).isEqualTo("R$ 999.99");
    }

    @Test
    @DisplayName("Deve manter igualdade entre valores monetários com mesmo valor")
    void deveManterIgualdadeEntreValoresMonetarios() {
        // Arrange
        ValorMonetario valor1 = ValorMonetario.of("100.00");
        ValorMonetario valor2 = ValorMonetario.of("100.00");

        // Act & Assert
        assertThat(valor1).isEqualTo(valor2);
        assertThat(valor1.hashCode()).isEqualTo(valor2.hashCode());
    }

    @Test
    @DisplayName("Deve diferenciar valores monetários com valores diferentes")
    void deveDiferenciarValoresMonetariosComValoresDiferentes() {
        // Arrange
        ValorMonetario valor1 = ValorMonetario.of("100.00");
        ValorMonetario valor2 = ValorMonetario.of("200.00");

        // Act & Assert
        assertThat(valor1).isNotEqualTo(valor2);
    }
}
