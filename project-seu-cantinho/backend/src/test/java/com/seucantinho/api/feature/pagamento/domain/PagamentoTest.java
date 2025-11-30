package com.seucantinho.api.feature.pagamento.domain;

import com.seucantinho.api.feature.espaco.domain.Espaco;
import com.seucantinho.api.feature.espaco.domain.valueobject.Capacidade;
import com.seucantinho.api.feature.filial.domain.Filial;
import com.seucantinho.api.feature.pagamento.domain.enums.TipoPagamentoEnum;
import com.seucantinho.api.feature.reserva.domain.Reserva;
import com.seucantinho.api.feature.reserva.domain.enums.StatusReservaEnum;
import com.seucantinho.api.feature.reserva.domain.valueobject.DataEvento;
import com.seucantinho.api.feature.cliente.domain.Cliente;
import com.seucantinho.api.shared.domain.valueobject.ValorMonetario;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Testes da classe Pagamento")
class PagamentoTest {

    @Test
    @DisplayName("Deve criar pagamento válido com todos os atributos obrigatórios")
    void deveCriarPagamentoValidoComAtributosObrigatorios() {
        // Arrange
        Reserva reserva = criarReserva();
        ValorMonetario valor = ValorMonetario.of("150.00");

        // Act
        Pagamento pagamento = Pagamento.builder()
                .reserva(reserva)
                .valor(valor)
                .tipo(TipoPagamentoEnum.SINAL)
                .formaPagamento("PIX")
                .build();

        // Assert
        assertThat(pagamento).isNotNull();
        assertThat(pagamento.getReserva()).isEqualTo(reserva);
        assertThat(pagamento.getValor()).isEqualTo(valor);
        assertThat(pagamento.getTipo()).isEqualTo(TipoPagamentoEnum.SINAL);
        assertThat(pagamento.getFormaPagamento()).isEqualTo("PIX");
    }

    @Test
    @DisplayName("Deve validar pagamento SINAL com valor correto (50% do total)")
    void deveValidarPagamentoSinalComValorCorreto() {
        // Arrange
        Reserva reserva = criarReserva();
        ValorMonetario valorSinal = ValorMonetario.of("150.00"); // 50% de 300

        Pagamento pagamento = Pagamento.builder()
                .reserva(reserva)
                .valor(valorSinal)
                .tipo(TipoPagamentoEnum.SINAL)
                .formaPagamento("PIX")
                .build();

        // Act & Assert
        assertThatCode(() -> pagamento.validar()).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Deve lançar exceção ao validar pagamento SINAL com valor incorreto")
    void deveLancarExcecaoAoValidarPagamentoSinalComValorIncorreto() {
        // Arrange
        Reserva reserva = criarReserva();
        ValorMonetario valorErrado = ValorMonetario.of("100.00");

        Pagamento pagamento = Pagamento.builder()
                .reserva(reserva)
                .valor(valorErrado)
                .tipo(TipoPagamentoEnum.SINAL)
                .formaPagamento("PIX")
                .build();

        // Act & Assert
        assertThatThrownBy(() -> pagamento.validar())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Pagamento SINAL deve ser 50% do valor total");
    }

    @Test
    @DisplayName("Deve validar pagamento TOTAL com valor correto (100% do total)")
    void deveValidarPagamentoTotalComValorCorreto() {
        // Arrange
        Reserva reserva = criarReserva();
        ValorMonetario valorTotal = ValorMonetario.of("300.00");

        Pagamento pagamento = Pagamento.builder()
                .reserva(reserva)
                .valor(valorTotal)
                .tipo(TipoPagamentoEnum.TOTAL)
                .formaPagamento("Cartão de Crédito")
                .build();

        // Act & Assert
        assertThatCode(() -> pagamento.validar()).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Deve lançar exceção ao validar pagamento TOTAL com valor incorreto")
    void deveLancarExcecaoAoValidarPagamentoTotalComValorIncorreto() {
        // Arrange
        Reserva reserva = criarReserva();
        ValorMonetario valorErrado = ValorMonetario.of("150.00");

        Pagamento pagamento = Pagamento.builder()
                .reserva(reserva)
                .valor(valorErrado)
                .tipo(TipoPagamentoEnum.TOTAL)
                .formaPagamento("PIX")
                .build();

        // Act & Assert
        assertThatThrownBy(() -> pagamento.validar())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Pagamento TOTAL deve ser o valor completo");
    }

    @Test
    @DisplayName("Deve validar pagamento QUITACAO após pagamento SINAL")
    void deveValidarPagamentoQuitacaoAposPagamentoSinal() {
        // Arrange
        Reserva reserva = criarReserva();

        Pagamento pagamentoSinal = Pagamento.builder()
                .reserva(reserva)
                .valor(ValorMonetario.of("150.00"))
                .tipo(TipoPagamentoEnum.SINAL)
                .formaPagamento("PIX")
                .build();
        reserva.getPagamentos().add(pagamentoSinal);

        Pagamento pagamentoQuitacao = Pagamento.builder()
                .reserva(reserva)
                .valor(ValorMonetario.of("150.00"))
                .tipo(TipoPagamentoEnum.QUITACAO)
                .formaPagamento("Cartão")
                .build();

        // Act & Assert
        assertThatCode(() -> pagamentoQuitacao.validar()).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar QUITACAO sem pagamento SINAL anterior")
    void deveLancarExcecaoAoTentarQuitacaoSemPagamentoSinalAnterior() {
        // Arrange
        Reserva reserva = criarReserva();

        Pagamento pagamento = Pagamento.builder()
                .reserva(reserva)
                .valor(ValorMonetario.of("150.00"))
                .tipo(TipoPagamentoEnum.QUITACAO)
                .formaPagamento("PIX")
                .build();

        // Act & Assert
        assertThatThrownBy(() -> pagamento.validar())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Pagamento QUITACAO só pode ser feito após o pagamento do SINAL");
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar segundo pagamento SINAL")
    void deveLancarExcecaoAoTentarSegundoPagamentoSinal() {
        // Arrange
        Reserva reserva = criarReserva();

        Pagamento pagamentoSinal1 = Pagamento.builder()
                .reserva(reserva)
                .valor(ValorMonetario.of("150.00"))
                .tipo(TipoPagamentoEnum.SINAL)
                .formaPagamento("PIX")
                .build();
        reserva.getPagamentos().add(pagamentoSinal1);

        Pagamento pagamentoSinal2 = Pagamento.builder()
                .reserva(reserva)
                .valor(ValorMonetario.of("150.00"))
                .tipo(TipoPagamentoEnum.SINAL)
                .formaPagamento("PIX")
                .build();

        // Act & Assert
        assertThatThrownBy(() -> pagamentoSinal2.validar())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Pagamento SINAL só pode ser feito na criação da reserva");
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar pagamento TOTAL quando já existem pagamentos")
    void deveLancarExcecaoAoTentarPagamentoTotalQuandoJaExistemPagamentos() {
        // Arrange
        Reserva reserva = criarReserva();

        Pagamento pagamentoSinal = Pagamento.builder()
                .reserva(reserva)
                .valor(ValorMonetario.of("150.00"))
                .tipo(TipoPagamentoEnum.SINAL)
                .formaPagamento("PIX")
                .build();
        reserva.getPagamentos().add(pagamentoSinal);

        Pagamento pagamentoTotal = Pagamento.builder()
                .reserva(reserva)
                .valor(ValorMonetario.of("300.00"))
                .tipo(TipoPagamentoEnum.TOTAL)
                .formaPagamento("Cartão")
                .build();

        // Act & Assert
        assertThatThrownBy(() -> pagamentoTotal.validar())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Pagamento TOTAL só pode ser feito na criação da reserva");
    }

    @Test
    @DisplayName("Deve lançar exceção quando reserva for nula")
    void deveLancarExcecaoQuandoReservaForNula() {
        // Arrange
        Pagamento pagamento = Pagamento.builder()
                .reserva(null)
                .valor(ValorMonetario.of("150.00"))
                .tipo(TipoPagamentoEnum.SINAL)
                .formaPagamento("PIX")
                .build();

        // Act & Assert
        assertThatThrownBy(() -> pagamento.validar())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Reserva é obrigatória");
    }

    @Test
    @DisplayName("Deve lançar exceção quando valor for nulo")
    void deveLancarExcecaoQuandoValorForNulo() {
        // Arrange
        Reserva reserva = criarReserva();

        Pagamento pagamento = Pagamento.builder()
                .reserva(reserva)
                .valor(null)
                .tipo(TipoPagamentoEnum.SINAL)
                .formaPagamento("PIX")
                .build();

        // Act & Assert
        assertThatThrownBy(() -> pagamento.validar())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Valor é obrigatório");
    }

    @Test
    @DisplayName("Deve lançar exceção quando tipo for nulo")
    void deveLancarExcecaoQuandoTipoForNulo() {
        // Arrange
        Reserva reserva = criarReserva();

        Pagamento pagamento = Pagamento.builder()
                .reserva(reserva)
                .valor(ValorMonetario.of("150.00"))
                .tipo(null)
                .formaPagamento("PIX")
                .build();

        // Act & Assert
        assertThatThrownBy(() -> pagamento.validar())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Tipo de pagamento é obrigatório");
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar QUITACAO com valor incorreto")
    void deveLancarExcecaoAoTentarQuitacaoComValorIncorreto() {
        // Arrange
        Reserva reserva = criarReserva();

        Pagamento pagamentoSinal = Pagamento.builder()
                .reserva(reserva)
                .valor(ValorMonetario.of("150.00"))
                .tipo(TipoPagamentoEnum.SINAL)
                .formaPagamento("PIX")
                .build();
        reserva.getPagamentos().add(pagamentoSinal);

        Pagamento pagamentoQuitacao = Pagamento.builder()
                .reserva(reserva)
                .valor(ValorMonetario.of("100.00")) // Valor errado
                .tipo(TipoPagamentoEnum.QUITACAO)
                .formaPagamento("Cartão")
                .build();

        // Act & Assert
        assertThatThrownBy(() -> pagamentoQuitacao.validar())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Pagamento QUITACAO deve ser o saldo restante");
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar segunda QUITACAO")
    void deveLancarExcecaoAoTentarSegundaQuitacao() {
        // Arrange
        Reserva reserva = criarReserva();

        Pagamento pagamentoSinal = Pagamento.builder()
                .reserva(reserva)
                .valor(ValorMonetario.of("150.00"))
                .tipo(TipoPagamentoEnum.SINAL)
                .formaPagamento("PIX")
                .build();

        Pagamento pagamentoQuitacao1 = Pagamento.builder()
                .reserva(reserva)
                .valor(ValorMonetario.of("150.00"))
                .tipo(TipoPagamentoEnum.QUITACAO)
                .formaPagamento("Cartão")
                .build();

        reserva.getPagamentos().add(pagamentoSinal);
        reserva.getPagamentos().add(pagamentoQuitacao1);

        Pagamento pagamentoQuitacao2 = Pagamento.builder()
                .reserva(reserva)
                .valor(ValorMonetario.of("150.00"))
                .tipo(TipoPagamentoEnum.QUITACAO)
                .formaPagamento("Cartão")
                .build();

        // Act & Assert
        assertThatThrownBy(() -> pagamentoQuitacao2.validar())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Esta reserva já foi quitada");
    }

    @Test
    @DisplayName("Deve aceitar diferentes formas de pagamento")
    void deveAceitarDiferentesFormasDePagamento() {
        // Arrange & Act
        Reserva reserva = criarReserva();

        Pagamento pagamentoPix = criarPagamentoValido(reserva, "PIX");
        Pagamento pagamentoCartao = criarPagamentoValido(criarReserva(), "Cartão de Crédito");
        Pagamento pagamentoDinheiro = criarPagamentoValido(criarReserva(), "Dinheiro");
        Pagamento pagamentoBoleto = criarPagamentoValido(criarReserva(), "Boleto");

        // Assert
        assertThat(pagamentoPix.getFormaPagamento()).isEqualTo("PIX");
        assertThat(pagamentoCartao.getFormaPagamento()).isEqualTo("Cartão de Crédito");
        assertThat(pagamentoDinheiro.getFormaPagamento()).isEqualTo("Dinheiro");
        assertThat(pagamentoBoleto.getFormaPagamento()).isEqualTo("Boleto");
    }

    @Test
    @DisplayName("Deve permitir código de transação do gateway")
    void devePermitirCodigoDeTransacaoDoGateway() {
        // Arrange
        Reserva reserva = criarReserva();
        String codigoTransacao = "TXN_12345678";

        // Act
        Pagamento pagamento = Pagamento.builder()
                .reserva(reserva)
                .valor(ValorMonetario.of("150.00"))
                .tipo(TipoPagamentoEnum.SINAL)
                .formaPagamento("PIX")
                .codigoTransacaoGateway(codigoTransacao)
                .build();

        // Assert
        assertThat(pagamento.getCodigoTransacaoGateway()).isEqualTo(codigoTransacao);
    }

    // Métodos auxiliares
    private Reserva criarReserva() {
        Cliente cliente = Cliente.builder()
                .id(1)
                .nome("João Silva")
                .email("joao@email.com")
                .senhaHash("hash123")
                .ativo(true)
                .build();

        Filial filial = Filial.builder()
                .id(1)
                .nome("Filial Centro")
                .cidade("Curitiba")
                .estado("PR")
                .build();

        Espaco espaco = Espaco.builder()
                .id(1)
                .nome("Salão de Eventos")
                .capacidade(Capacidade.of(50))
                .precoDiaria(ValorMonetario.of("300.00"))
                .filial(filial)
                .ativo(true)
                .build();

        return Reserva.builder()
                .id(1)
                .usuario(cliente)
                .espaco(espaco)
                .dataEvento(DataEvento.of(LocalDate.now().plusDays(10)))
                .valorTotal(ValorMonetario.of("300.00"))
                .status(StatusReservaEnum.AGUARDANDO_SINAL)
                .pagamentos(new ArrayList<>())
                .build();
    }

    private Pagamento criarPagamentoValido(Reserva reserva, String formaPagamento) {
        return Pagamento.builder()
                .reserva(reserva)
                .valor(ValorMonetario.of("300.00"))
                .tipo(TipoPagamentoEnum.TOTAL)
                .formaPagamento(formaPagamento)
                .build();
    }
}
