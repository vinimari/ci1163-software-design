package com.seucantinho.api.feature.espaco.application.dto;

import com.seucantinho.api.feature.filial.application.dto.FilialResponseDTO;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EspacoResponseDTO {

    private Integer id;
    private String nome;
    private String descricao;
    private Integer capacidade;
    private BigDecimal precoDiaria;
    private Boolean ativo;
    private String urlFotoPrincipal;
    private FilialResponseDTO filial;
}
