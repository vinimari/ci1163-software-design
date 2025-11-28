package com.seucantinho.api.feature.filial.domain;

import com.seucantinho.api.feature.espaco.domain.Espaco;
import com.seucantinho.api.feature.funcionario.domain.Funcionario;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tb_filial")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Filial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false, length = 100)
    private String cidade;

    @Column(nullable = false, length = 2)
    private String estado;

    @Column(length = 255)
    private String endereco;

    @Column(length = 20)
    private String telefone;

    @Column(name = "data_cadastro", updatable = false)
    private LocalDateTime dataCadastro;

    @OneToMany(mappedBy = "filial", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Espaco> espacos = new ArrayList<>();

    @OneToMany(mappedBy = "filial")
    @Builder.Default
    private List<Funcionario> funcionarios = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        dataCadastro = LocalDateTime.now();
    }
}
