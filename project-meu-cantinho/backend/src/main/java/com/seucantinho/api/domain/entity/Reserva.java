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

    public BigDecimal calcularTotalPago() {
        return pagamentos.stream()
            .map(Pagamento::getValor)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal calcularSaldo() {
        return valorTotal.subtract(calcularTotalPago());
    }

    public void validarValorTotal() {
        if (espaco == null) {
            throw new IllegalArgumentException("Espaço não pode ser nulo");
        }

        BigDecimal valorEsperado = espaco.getPrecoDiaria();
        if (valorTotal.compareTo(valorEsperado) != 0) {
            throw new IllegalArgumentException(
                "Valor total incorreto. Esperado: R$ " + valorEsperado +
                ", Recebido: R$ " + valorTotal
            );
        }
    }
}
