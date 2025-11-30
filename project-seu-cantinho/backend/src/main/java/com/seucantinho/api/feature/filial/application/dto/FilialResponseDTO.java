package com.seucantinho.api.feature.filial.application.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FilialResponseDTO {

    private Integer id;
    private String nome;
    private String cidade;
    private String estado;
    private String endereco;
    private String telefone;
    private LocalDateTime dataCadastro;
    private Integer quantidadeEspacos;
}
