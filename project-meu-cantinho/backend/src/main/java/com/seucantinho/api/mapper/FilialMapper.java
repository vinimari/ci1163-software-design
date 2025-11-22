package com.seucantinho.api.mapper;

import com.seucantinho.api.domain.entity.Filial;
import com.seucantinho.api.dto.filial.FilialRequestDTO;
import com.seucantinho.api.dto.filial.FilialResponseDTO;
import org.springframework.stereotype.Component;

/**
 * Mapper responsável pela conversão entre Filial (Entity) e seus DTOs.
 * Aplica o princípio SRP (Single Responsibility Principle).
 */
@Component
public class FilialMapper {

    public Filial toEntity(FilialRequestDTO dto) {
        return Filial.builder()
                .nome(dto.getNome())
                .cidade(dto.getCidade())
                .estado(dto.getEstado())
                .endereco(dto.getEndereco())
                .telefone(dto.getTelefone())
                .build();
    }

    public FilialResponseDTO toResponseDTO(Filial filial) {
        return FilialResponseDTO.builder()
                .id(filial.getId())
                .nome(filial.getNome())
                .cidade(filial.getCidade())
                .estado(filial.getEstado())
                .endereco(filial.getEndereco())
                .telefone(filial.getTelefone())
                .dataCadastro(filial.getDataCadastro())
                .quantidadeEspacos(filial.getEspacos() != null ? filial.getEspacos().size() : 0)
                .build();
    }

    public void updateEntityFromDTO(Filial filial, FilialRequestDTO dto) {
        filial.setNome(dto.getNome());
        filial.setCidade(dto.getCidade());
        filial.setEstado(dto.getEstado());
        filial.setEndereco(dto.getEndereco());
        filial.setTelefone(dto.getTelefone());
    }
}
