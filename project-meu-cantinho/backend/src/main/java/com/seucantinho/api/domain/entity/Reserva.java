package com.seucantinho.api.domain.entity;

import com.seucantinho.api.domain.enums.StatusReservaEnum;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tb_reserva")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "data_criacao", updatable = false)
    private LocalDateTime dataCriacao;

    @Column(name = "data_evento", nullable = false)
    private LocalDate dataEvento;

    @Column(name = "valor_total", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorTotal;

    @Column(columnDefinition = "TEXT")
    private String observacoes;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(nullable = false, columnDefinition = "status_reserva_enum")
    @Builder.Default
    private StatusReservaEnum status = StatusReservaEnum.AGUARDANDO_SINAL;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "espaco_id", nullable = false)
    private Espaco espaco;

    @OneToMany(mappedBy = "reserva", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Pagamento> pagamentos = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        dataCriacao = LocalDateTime.now();
        if (status == null) {
            status = StatusReservaEnum.AGUARDANDO_SINAL;
        }
    }

    /**
     * Calcula o total pago até o momento somando todos os pagamentos.
     * Método de cálculo simples mantido na entidade (Tell, Don't Ask).
     *
     * @return O valor total já pago
     */
    public BigDecimal calcularTotalPago() {
        return pagamentos.stream()
            .map(Pagamento::getValor)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Calcula o saldo restante a ser pago (valor total - total já pago).
     * Método de cálculo simples mantido na entidade (Tell, Don't Ask).
     *
     * @return O saldo restante
     */
    public BigDecimal calcularSaldo() {
        return valorTotal.subtract(calcularTotalPago());
    }

    /**
     * Verifica se a reserva está completamente quitada.
     *
     * @return true se o saldo é zero e o status é QUITADA
     */
    public boolean isQuitada() {
        return calcularSaldo().compareTo(BigDecimal.ZERO) == 0
            && status == StatusReservaEnum.QUITADA;
    }

    /**
     * Verifica se a reserva está ativa (não cancelada ou finalizada).
     *
     * @return true se a reserva está ativa
     */
    public boolean isAtiva() {
        return status != StatusReservaEnum.CANCELADA
            && status != StatusReservaEnum.FINALIZADA;
    }
}
