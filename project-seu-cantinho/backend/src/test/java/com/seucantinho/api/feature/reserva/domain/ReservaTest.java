package com.seucantinho.api.feature.reserva.domain;

import com.seucantinho.api.feature.espaco.domain.Espaco;
import com.seucantinho.api.feature.espaco.domain.valueobject.Capacidade;
import com.seucantinho.api.feature.filial.domain.Filial;
import com.seucantinho.api.feature.pagamento.domain.Pagamento;
import com.seucantinho.api.feature.pagamento.domain.enums.TipoPagamentoEnum;
import com.seucantinho.api.feature.reserva.domain.enums.StatusReservaEnum;
import com.seucantinho.api.feature.reserva.domain.valueobject.DataEvento;
import com.seucantinho.api.feature.usuario.domain.Usuario;
import com.seucantinho.api.feature.cliente.domain.Cliente;
import com.seucantinho.api.shared.domain.exception.BusinessException;
import com.seucantinho.api.shared.domain.valueobject.ValorMonetario;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Testes da classe Reserva")
class ReservaTest {

    @Test
    @DisplayName("Deve criar reserva válida com todos os atributos obrigatórios")
    void deveCriarReservaValidaComAtributosObrigatorios() {
        // Arrange
        Usuario usuario = criarCliente();
        Espaco espaco = criarEspaco();
        DataEvento dataEvento = DataEvento.of(LocalDate.now().plusDays(10));
        ValorMonetario valorTotal = ValorMonetario.of("300.00");

        // Act
        Reserva reserva = Reserva.builder()
                .usuario(usuario)
                .espaco(espaco)
                .dataEvento(dataEvento)
                .valorTotal(valorTotal)
                .build();

        // Assert
        assertThat(reserva).isNotNull();
        assertThat(reserva.getUsuario()).isEqualTo(usuario);
        assertThat(reserva.getEspaco()).isEqualTo(espaco);
        assertThat(reserva.getDataEvento()).isEqualTo(dataEvento);
        assertThat(reserva.getValorTotal()).isEqualTo(valorTotal);
    }

    @Test
    @DisplayName("Deve inicializar status como AGUARDANDO_SINAL por padrão")
    void deveInicializarStatusComoAguardandoSinalPorPadrao() {
        // Arrange
        Reserva reserva = Reserva.builder()
                .usuario(criarCliente())
                .espaco(criarEspaco())
                .dataEvento(DataEvento.of(LocalDate.now().plusDays(10)))
                .valorTotal(ValorMonetario.of("300.00"))
                .build();

        // Act
        reserva.onCreate();

        // Assert
        assertThat(reserva.getStatus()).isEqualTo(StatusReservaEnum.AGUARDANDO_SINAL);
    }

    @Test
    @DisplayName("Deve inicializar lista de pagamentos vazia")
    void deveInicializarListaDePagamentosVazia() {
        // Arrange & Act
        Reserva reserva = Reserva.builder()
                .usuario(criarCliente())
                .espaco(criarEspaco())
                .dataEvento(DataEvento.of(LocalDate.now().plusDays(10)))
                .valorTotal(ValorMonetario.of("300.00"))
                .build();

        // Assert
        assertThat(reserva.getPagamentos()).isNotNull();
        assertThat(reserva.getPagamentos()).isEmpty();
    }

    @Test
    @DisplayName("Deve validar reserva com dados corretos sem lançar exceção")
    void deveValidarReservaComDadosCorretosSemLancarExcecao() {
        // Arrange
        Reserva reserva = criarReservaValida();

        // Act & Assert
        assertThatCode(() -> reserva.validar()).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Deve lançar exceção ao validar reserva sem espaço")
    void deveLancarExcecaoAoValidarReservaSemEspaco() {
        // Arrange
        Reserva reserva = Reserva.builder()
                .usuario(criarCliente())
                .espaco(null)
                .dataEvento(DataEvento.of(LocalDate.now().plusDays(10)))
                .valorTotal(ValorMonetario.of("300.00"))
                .build();

        // Act & Assert
        assertThatThrownBy(() -> reserva.validar())
                .isInstanceOf(BusinessException.class)
                .hasMessage("Espaço não pode ser nulo");
    }

    @Test
    @DisplayName("Deve lançar exceção ao validar reserva com espaço inativo")
    void deveLancarExcecaoAoValidarReservaComEspacoInativo() {
        // Arrange
        Espaco espaco = criarEspaco();
        espaco.setAtivo(false);

        Reserva reserva = Reserva.builder()
                .usuario(criarCliente())
                .espaco(espaco)
                .dataEvento(DataEvento.of(LocalDate.now().plusDays(10)))
                .valorTotal(ValorMonetario.of("300.00"))
                .build();

        // Act & Assert
        assertThatThrownBy(() -> reserva.validar())
                .isInstanceOf(BusinessException.class)
                .hasMessage("Não é possível reservar um espaço inativo");
    }

    @Test
    @DisplayName("Deve lançar exceção ao validar reserva com valor total incorreto")
    void deveLancarExcecaoAoValidarReservaComValorTotalIncorreto() {
        // Arrange
        Espaco espaco = criarEspaco();
        Reserva reserva = Reserva.builder()
                .usuario(criarCliente())
                .espaco(espaco)
                .dataEvento(DataEvento.of(LocalDate.now().plusDays(10)))
                .valorTotal(ValorMonetario.of("999.99")) // Valor diferente do preço do espaço
                .build();

        // Act & Assert
        assertThatThrownBy(() -> reserva.validar())
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Valor total incorreto");
    }

    @Test
    @DisplayName("Deve calcular total pago como zero quando não há pagamentos")
    void deveCalcularTotalPagoComoZeroQuandoNaoHaPagamentos() {
        // Arrange
        Reserva reserva = criarReservaValida();

        // Act
        ValorMonetario totalPago = reserva.calcularTotalPago();

        // Assert
        assertThat(totalPago.isZero()).isTrue();
    }

    @Test
    @DisplayName("Deve calcular total pago somando todos os pagamentos")
    void deveCalcularTotalPagoSomandoTodosPagamentos() {
        // Arrange
        Reserva reserva = criarReservaValida();
        Pagamento pagamento1 = criarPagamento(reserva, ValorMonetario.of("150.00"));
        Pagamento pagamento2 = criarPagamento(reserva, ValorMonetario.of("150.00"));
        reserva.getPagamentos().add(pagamento1);
        reserva.getPagamentos().add(pagamento2);

        // Act
        ValorMonetario totalPago = reserva.calcularTotalPago();

        // Assert
        assertThat(totalPago.getValor()).isEqualByComparingTo("300.00");
    }

    @Test
    @DisplayName("Deve calcular saldo restante corretamente")
    void deveCalcularSaldoRestanteCorretamente() {
        // Arrange
        Reserva reserva = criarReservaValida();
        Pagamento pagamento = criarPagamento(reserva, ValorMonetario.of("150.00"));
        reserva.getPagamentos().add(pagamento);

        // Act
        ValorMonetario saldo = reserva.calcularSaldo();

        // Assert
        assertThat(saldo.getValor()).isEqualByComparingTo("150.00");
    }

    @Test
    @DisplayName("Deve calcular saldo como zero quando reserva está totalmente paga")
    void deveCalcularSaldoComoZeroQuandoReservaTotalmentePaga() {
        // Arrange
        Reserva reserva = criarReservaValida();
        Pagamento pagamento = criarPagamento(reserva, ValorMonetario.of("300.00"));
        reserva.getPagamentos().add(pagamento);

        // Act
        ValorMonetario saldo = reserva.calcularSaldo();

        // Assert
        assertThat(saldo.isZero()).isTrue();
    }

    @Test
    @DisplayName("Deve identificar reserva como quitada quando saldo é zero e status é QUITADA")
    void deveIdentificarReservaComoQuitadaQuandoSaldoZeroEStatusQuitada() {
        // Arrange
        Reserva reserva = criarReservaValida();
        reserva.setStatus(StatusReservaEnum.QUITADA);
        Pagamento pagamento = criarPagamento(reserva, ValorMonetario.of("300.00"));
        reserva.getPagamentos().add(pagamento);

        // Act
        boolean isQuitada = reserva.isQuitada();

        // Assert
        assertThat(isQuitada).isTrue();
    }

    @Test
    @DisplayName("Deve identificar reserva como não quitada quando saldo não é zero")
    void deveIdentificarReservaComoNaoQuitadaQuandoSaldoNaoZero() {
        // Arrange
        Reserva reserva = criarReservaValida();
        reserva.setStatus(StatusReservaEnum.QUITADA);
        Pagamento pagamento = criarPagamento(reserva, ValorMonetario.of("150.00"));
        reserva.getPagamentos().add(pagamento);

        // Act
        boolean isQuitada = reserva.isQuitada();

        // Assert
        assertThat(isQuitada).isFalse();
    }

    @Test
    @DisplayName("Deve identificar reserva como ativa quando status não é CANCELADA ou FINALIZADA")
    void deveIdentificarReservaComoAtivaQuandoStatusAtivo() {
        // Arrange
        Reserva reserva1 = criarReservaValida();
        reserva1.setStatus(StatusReservaEnum.AGUARDANDO_SINAL);

        Reserva reserva2 = criarReservaValida();
        reserva2.setStatus(StatusReservaEnum.CONFIRMADA);

        Reserva reserva3 = criarReservaValida();
        reserva3.setStatus(StatusReservaEnum.QUITADA);

        // Act & Assert
        assertThat(reserva1.isAtiva()).isTrue();
        assertThat(reserva2.isAtiva()).isTrue();
        assertThat(reserva3.isAtiva()).isTrue();
    }

    @Test
    @DisplayName("Deve identificar reserva como inativa quando status é CANCELADA")
    void deveIdentificarReservaComoInativaQuandoStatusCancelada() {
        // Arrange
        Reserva reserva = criarReservaValida();
        reserva.setStatus(StatusReservaEnum.CANCELADA);

        // Act
        boolean isAtiva = reserva.isAtiva();

        // Assert
        assertThat(isAtiva).isFalse();
    }

    @Test
    @DisplayName("Deve identificar reserva como inativa quando status é FINALIZADA")
    void deveIdentificarReservaComoInativaQuandoStatusFinalizada() {
        // Arrange
        Reserva reserva = criarReservaValida();
        reserva.setStatus(StatusReservaEnum.FINALIZADA);

        // Act
        boolean isAtiva = reserva.isAtiva();

        // Assert
        assertThat(isAtiva).isFalse();
    }

    @Test
    @DisplayName("Deve validar disponibilidade com sucesso quando espaço está disponível")
    void deveValidarDisponibilidadeComSucessoQuandoEspacoDisponivel() {
        // Arrange
        Reserva reserva = criarReservaValida();

        // Act & Assert
        assertThatCode(() -> reserva.validarDisponibilidade(true)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Deve lançar exceção ao validar disponibilidade quando espaço não está disponível")
    void deveLancarExcecaoAoValidarDisponibilidadeQuandoEspacoNaoDisponivel() {
        // Arrange
        Reserva reserva = criarReservaValida();

        // Act & Assert
        assertThatThrownBy(() -> reserva.validarDisponibilidade(false))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Espaço já possui reserva ativa para esta data");
    }

    @Test
    @DisplayName("Deve permitir adicionar observações à reserva")
    void devePermitirAdicionarObservacoesAReserva() {
        // Arrange
        Reserva reserva = criarReservaValida();
        String observacao = "Festa de aniversário - decoração azul";

        // Act
        reserva.setObservacoes(observacao);

        // Assert
        assertThat(reserva.getObservacoes()).isEqualTo(observacao);
    }

    @Test
    @DisplayName("Deve aceitar todos os status de reserva válidos")
    void deveAceitarTodosOsStatusDeReservaValidos() {
        // Arrange
        Reserva reserva = criarReservaValida();

        // Act & Assert
        reserva.setStatus(StatusReservaEnum.AGUARDANDO_SINAL);
        assertThat(reserva.getStatus()).isEqualTo(StatusReservaEnum.AGUARDANDO_SINAL);

        reserva.setStatus(StatusReservaEnum.CONFIRMADA);
        assertThat(reserva.getStatus()).isEqualTo(StatusReservaEnum.CONFIRMADA);

        reserva.setStatus(StatusReservaEnum.QUITADA);
        assertThat(reserva.getStatus()).isEqualTo(StatusReservaEnum.QUITADA);

        reserva.setStatus(StatusReservaEnum.CANCELADA);
        assertThat(reserva.getStatus()).isEqualTo(StatusReservaEnum.CANCELADA);

        reserva.setStatus(StatusReservaEnum.FINALIZADA);
        assertThat(reserva.getStatus()).isEqualTo(StatusReservaEnum.FINALIZADA);
    }

    // Métodos auxiliares
    private Reserva criarReservaValida() {
        return Reserva.builder()
                .usuario(criarCliente())
                .espaco(criarEspaco())
                .dataEvento(DataEvento.of(LocalDate.now().plusDays(10)))
                .valorTotal(ValorMonetario.of("300.00"))
                .status(StatusReservaEnum.AGUARDANDO_SINAL)
                .pagamentos(new ArrayList<>())
                .build();
    }

    private Cliente criarCliente() {
        return Cliente.builder()
                .id(1)
                .nome("João Silva")
                .email("joao@email.com")
                .senhaHash("hash123")
                .cpf("12345678901")
                .telefone("(41) 99999-9999")
                .ativo(true)
                .build();
    }

    private Espaco criarEspaco() {
        Filial filial = Filial.builder()
                .id(1)
                .nome("Filial Centro")
                .cidade("Curitiba")
                .estado("PR")
                .build();

        return Espaco.builder()
                .id(1)
                .nome("Salão de Eventos")
                .capacidade(Capacidade.of(50))
                .precoDiaria(ValorMonetario.of("300.00"))
                .filial(filial)
                .ativo(true)
                .build();
    }

    private Pagamento criarPagamento(Reserva reserva, ValorMonetario valor) {
        return Pagamento.builder()
                .reserva(reserva)
                .valor(valor)
                .tipo(TipoPagamentoEnum.SINAL)
                .formaPagamento("PIX")
                .build();
    }
}
