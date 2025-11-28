package com.seucantinho.api.feature.filial.application.port.in;

import com.seucantinho.api.feature.filial.application.dto.FilialRequestDTO;
import com.seucantinho.api.feature.filial.application.dto.FilialResponseDTO;

import java.util.List;

public interface IFilialService {

    List<FilialResponseDTO> findAll();

    FilialResponseDTO findById(Integer id);

    FilialResponseDTO create(FilialRequestDTO requestDTO);

    FilialResponseDTO update(Integer id, FilialRequestDTO requestDTO);

    void delete(Integer id);
}
