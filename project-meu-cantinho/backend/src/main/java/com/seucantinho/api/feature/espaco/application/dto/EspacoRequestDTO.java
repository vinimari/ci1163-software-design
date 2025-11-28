package com.seucantinho.api.feature.espaco.application.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EspacoRequestDTO {

    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 150, message = "Nome deve ter no máximo 150 caracteres")
    private String nome;

    private String descricao;

    @NotNull(message = "Capacidade é obrigatória")
    @Min(value = 1, message = "Capacidade deve ser no mínimo 1")
    private Integer capacidade;

    @NotNull(message = "Preço da diária é obrigatório")
    @DecimalMin(value = "0.0", inclusive = true, message = "Preço deve ser maior ou igual a zero")
    private BigDecimal precoDiaria;

    private Boolean ativo;

    @Size(max = 255, message = "URL da foto deve ter no máximo 255 caracteres")
    private String urlFotoPrincipal;

    @NotNull(message = "ID da filial é obrigatório")
    private Integer filialId;
}
