package com.seucantinho.api.mapper;

import com.seucantinho.api.domain.entity.Espaco;
import com.seucantinho.api.domain.entity.Filial;
import com.seucantinho.api.domain.valueobject.Capacidade;
import com.seucantinho.api.domain.valueobject.ValorMonetario;
import com.seucantinho.api.dto.espaco.EspacoRequestDTO;
import com.seucantinho.api.dto.espaco.EspacoResponseDTO;
import com.seucantinho.api.dto.filial.FilialResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class EspacoMapper {

    public Espaco toEntity(EspacoRequestDTO dto, Filial filial) {
        return Espaco.builder()
                .nome(dto.getNome())
                .descricao(dto.getDescricao())
                .capacidade(Capacidade.of(dto.getCapacidade()))
                .precoDiaria(ValorMonetario.of(dto.getPrecoDiaria()))
                .ativo(dto.getAtivo() != null ? dto.getAtivo() : true)
                .urlFotoPrincipal(dto.getUrlFotoPrincipal())
                .filial(filial)
                .build();
    }

    public EspacoResponseDTO toResponseDTO(Espaco espaco) {
        FilialResponseDTO filialDTO = FilialResponseDTO.builder()
                .id(espaco.getFilial().getId())
                .nome(espaco.getFilial().getNome())
                .cidade(espaco.getFilial().getCidade())
                .estado(espaco.getFilial().getEstado())
                .build();

        return EspacoResponseDTO.builder()
                .id(espaco.getId())
                .nome(espaco.getNome())
                .descricao(espaco.getDescricao())
                .capacidade(espaco.getCapacidade().getQuantidade())
                .precoDiaria(espaco.getPrecoDiaria().getValor())
                .ativo(espaco.getAtivo())
                .urlFotoPrincipal(espaco.getUrlFotoPrincipal())
                .filial(filialDTO)
                .build();
    }

    public void updateEntityFromDTO(Espaco espaco, EspacoRequestDTO dto, Filial filial) {
        espaco.setNome(dto.getNome());
        espaco.setDescricao(dto.getDescricao());
        espaco.setCapacidade(Capacidade.of(dto.getCapacidade()));
        espaco.setPrecoDiaria(ValorMonetario.of(dto.getPrecoDiaria()));
        espaco.setAtivo(dto.getAtivo() != null ? dto.getAtivo() : espaco.getAtivo());
        espaco.setUrlFotoPrincipal(dto.getUrlFotoPrincipal());
        espaco.setFilial(filial);
    }
}
