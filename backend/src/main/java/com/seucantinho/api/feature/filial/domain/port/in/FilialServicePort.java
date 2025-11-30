package com.seucantinho.api.feature.filial.domain.port.in;

import com.seucantinho.api.feature.filial.application.dto.FilialRequestDTO;
import com.seucantinho.api.feature.filial.application.dto.FilialResponseDTO;

import java.util.List;

public interface FilialServicePort {

    List<FilialResponseDTO> findAll();

    FilialResponseDTO findById(Integer id);

    FilialResponseDTO create(FilialRequestDTO requestDTO);

    FilialResponseDTO update(Integer id, FilialRequestDTO requestDTO);

    void delete(Integer id);
}
