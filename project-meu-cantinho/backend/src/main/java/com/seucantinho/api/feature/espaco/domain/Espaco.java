package com.seucantinho.api.feature.espaco.domain;

import com.seucantinho.api.feature.reserva.domain.Reserva;
import com.seucantinho.api.feature.filial.domain.Filial;

import com.seucantinho.api.feature.espaco.domain.valueobject.Capacidade;
import com.seucantinho.api.shared.domain.valueobject.ValorMonetario;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tb_espaco")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Espaco {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 150)
    private String nome;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Embedded
    @AttributeOverride(name = "quantidade", column = @Column(name = "capacidade", nullable = false))
    private Capacidade capacidade;

    @Embedded
    @AttributeOverride(name = "valor", column = @Column(name = "preco_diaria", nullable = false, precision = 10, scale = 2))
    private ValorMonetario precoDiaria;

    @Column(nullable = false)
    @Builder.Default
    private Boolean ativo = true;

    @Column(name = "url_foto_principal", length = 255)
    private String urlFotoPrincipal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "filial_id", nullable = false)
    private Filial filial;

    @OneToMany(mappedBy = "espaco", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Reserva> reservas = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        if (ativo == null) {
            ativo = true;
        }
    }
}
