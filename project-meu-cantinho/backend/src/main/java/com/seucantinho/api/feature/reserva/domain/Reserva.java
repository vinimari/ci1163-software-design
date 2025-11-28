package com.seucantinho.api.feature.reserva.domain;

import com.seucantinho.api.feature.reserva.domain.enums.StatusReservaEnum;
import com.seucantinho.api.feature.usuario.domain.Usuario;
import com.seucantinho.api.feature.reserva.domain.valueobject.DataEvento;
import com.seucantinho.api.shared.domain.valueobject.ValorMonetario;
import com.seucantinho.api.feature.espaco.domain.Espaco;
import com.seucantinho.api.feature.pagamento.domain.Pagamento;
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

    @Embedded
    @AttributeOverride(name = "data", column = @Column(name = "data_evento", nullable = false))
    private DataEvento dataEvento;

    @Embedded
    @AttributeOverride(name = "valor", column = @Column(name = "valor_total", nullable = false, precision = 10, scale = 2))
    private ValorMonetario valorTotal;

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

    public ValorMonetario calcularTotalPago() {
        return pagamentos.stream()
            .map(Pagamento::getValor)
            .reduce(ValorMonetario.zero(), ValorMonetario::somar);
    }

    public ValorMonetario calcularSaldo() {
        return valorTotal.subtrair(calcularTotalPago());
    }

    public boolean isQuitada() {
        return calcularSaldo().isZero()
            && status == StatusReservaEnum.QUITADA;
    }

    public boolean isAtiva() {
        return status != StatusReservaEnum.CANCELADA
            && status != StatusReservaEnum.FINALIZADA;
    }
}
