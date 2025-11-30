package com.seucantinho.api.feature.usuario.domain;

import com.seucantinho.api.feature.usuario.domain.enums.PerfilUsuarioEnum;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_usuario")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "perfil", discriminatorType = DiscriminatorType.STRING)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 150)
    private String nome;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(name = "senha_hash", nullable = false, length = 255)
    private String senhaHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "perfil_usuario_enum", insertable = false, updatable = false)
    private PerfilUsuarioEnum perfil;

    @Column(length = 14)
    private String cpf;

    @Column(length = 20)
    private String telefone;

    @Column(nullable = false)
    @Builder.Default
    private Boolean ativo = true;

    @Column(name = "data_cadastro", updatable = false)
    private LocalDateTime dataCadastro;

    @PrePersist
    protected void onCreate() {
        dataCadastro = LocalDateTime.now();
        if (ativo == null) {
            ativo = true;
        }
    }

    public abstract PerfilUsuarioEnum getPerfil();

    public void validarEmail() {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email é obrigatório");
        }
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Email inválido");
        }
    }

    public void validarCpf() {
        if (cpf != null && !cpf.matches("\\d{11}")) {
            throw new IllegalArgumentException("CPF deve conter 11 dígitos");
        }
    }

    public void validar() {
        validarEmail();
        validarCpf();
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome é obrigatório");
        }
    }
}
