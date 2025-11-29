package com.seucantinho.api.feature.reserva.domain.valueobject;

import com.seucantinho.api.shared.domain.exception.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Testes da classe DataEvento")
class DataEventoTest {

    @Test
    @DisplayName("Deve criar data evento válida no futuro")
    void deveCriarDataEventoValidaNoFuturo() {
        // Arrange
        LocalDate dataFutura = LocalDate.now().plusDays(10);

        // Act
        DataEvento dataEvento = DataEvento.of(dataFutura);

        // Assert
        assertThat(dataEvento).isNotNull();
        assertThat(dataEvento.getData()).isEqualTo(dataFutura);
    }

    @Test
    @DisplayName("Deve criar data evento com 1 dia de antecedência")
    void deveCriarDataEventoComUmDiaDeAntecedencia() {
        // Arrange
        LocalDate dataFutura = LocalDate.now().plusDays(1);

        // Act
        DataEvento dataEvento = DataEvento.of(dataFutura);

        // Assert
        assertThat(dataEvento).isNotNull();
        assertThat(dataEvento.getData()).isEqualTo(dataFutura);
    }

    @Test
    @DisplayName("Deve criar data evento com 365 dias de antecedência")
    void deveCriarDataEventoComMaximoDeAntecedencia() {
        // Arrange
        LocalDate dataFutura = LocalDate.now().plusDays(365);

        // Act
        DataEvento dataEvento = DataEvento.of(dataFutura);

        // Assert
        assertThat(dataEvento).isNotNull();
        assertThat(dataEvento.getData()).isEqualTo(dataFutura);
    }

    @Test
    @DisplayName("Deve lançar exceção quando data for nula")
    void deveLancarExcecaoQuandoDataForNula() {
        // Arrange, Act & Assert
        assertThatThrownBy(() -> DataEvento.of(null))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Data do evento não pode ser nula");
    }

    @Test
    @DisplayName("Deve lançar exceção quando data for no passado")
    void deveLancarExcecaoQuandoDataForNoPassado() {
        // Arrange
        LocalDate dataPassada = LocalDate.now().minusDays(1);

        // Act & Assert
        assertThatThrownBy(() -> DataEvento.of(dataPassada))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Data do evento não pode ser no passado");
    }

    @Test
    @DisplayName("Deve lançar exceção quando data for hoje (sem antecedência mínima)")
    void deveLancarExcecaoQuandoDataForHoje() {
        // Arrange
        LocalDate hoje = LocalDate.now();

        // Act & Assert
        assertThatThrownBy(() -> DataEvento.of(hoje))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Evento deve ser agendado com pelo menos 1 dia(s) de antecedência");
    }

    @Test
    @DisplayName("Deve lançar exceção quando data exceder máximo de antecedência")
    void deveLancarExcecaoQuandoDataExcederMaximoDeAntecedencia() {
        // Arrange
        LocalDate dataDistante = LocalDate.now().plusDays(366);

        // Act & Assert
        assertThatThrownBy(() -> DataEvento.of(dataDistante))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Evento não pode ser agendado com mais de 365 dias de antecedência");
    }

    @Test
    @DisplayName("Deve formatar data corretamente")
    void deveFormatarDataCorretamente() {
        // Arrange
        LocalDate data = LocalDate.of(2025, 12, 25);
        DataEvento dataEvento = DataEvento.of(data);

        // Act
        String dataFormatada = dataEvento.getDataFormatada();

        // Assert
        assertThat(dataFormatada).isEqualTo("25/12/2025");
    }

    @Test
    @DisplayName("Deve retornar toString formatado")
    void deveRetornarToStringFormatado() {
        // Arrange
        LocalDate data = LocalDate.of(2025, 12, 31);
        DataEvento dataEvento = DataEvento.of(data);

        // Act
        String resultado = dataEvento.toString();

        // Assert
        assertThat(resultado).isEqualTo("31/12/2025");
    }

    @Test
    @DisplayName("Deve manter igualdade entre datas evento com mesma data")
    void deveManterIgualdadeEntreDatasEvento() {
        // Arrange
        LocalDate data = LocalDate.now().plusDays(10);
        DataEvento dataEvento1 = DataEvento.of(data);
        DataEvento dataEvento2 = DataEvento.of(data);

        // Act & Assert
        assertThat(dataEvento1).isEqualTo(dataEvento2);
        assertThat(dataEvento1.hashCode()).isEqualTo(dataEvento2.hashCode());
    }

    @Test
    @DisplayName("Deve diferenciar datas evento com datas diferentes")
    void deveDiferenciarDatasEventoComDatasDiferentes() {
        // Arrange
        LocalDate data1 = LocalDate.now().plusDays(10);
        LocalDate data2 = LocalDate.now().plusDays(20);
        DataEvento dataEvento1 = DataEvento.of(data1);
        DataEvento dataEvento2 = DataEvento.of(data2);

        // Act & Assert
        assertThat(dataEvento1).isNotEqualTo(dataEvento2);
    }

    @Test
    @DisplayName("Deve aceitar datas válidas dentro do intervalo permitido")
    void deveAceitarDatasValidasDentroDoIntervalo() {
        // Arrange & Act
        DataEvento dataEvento30Dias = DataEvento.of(LocalDate.now().plusDays(30));
        DataEvento dataEvento90Dias = DataEvento.of(LocalDate.now().plusDays(90));
        DataEvento dataEvento180Dias = DataEvento.of(LocalDate.now().plusDays(180));
        DataEvento dataEvento364Dias = DataEvento.of(LocalDate.now().plusDays(364));

        // Assert
        assertThat(dataEvento30Dias).isNotNull();
        assertThat(dataEvento90Dias).isNotNull();
        assertThat(dataEvento180Dias).isNotNull();
        assertThat(dataEvento364Dias).isNotNull();
    }

    @Test
    @DisplayName("Deve lançar exceção para data 2 anos no futuro")
    void deveLancarExcecaoParaDataMuitoDistante() {
        // Arrange
        LocalDate dataDistante = LocalDate.now().plusYears(2);

        // Act & Assert
        assertThatThrownBy(() -> DataEvento.of(dataDistante))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Evento não pode ser agendado com mais de 365 dias de antecedência");
    }
}
