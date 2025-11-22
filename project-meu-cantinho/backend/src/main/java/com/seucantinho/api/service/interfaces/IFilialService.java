package com.seucantinho.api.service.interfaces;

import com.seucantinho.api.dto.filial.FilialRequestDTO;
import com.seucantinho.api.dto.filial.FilialResponseDTO;

import java.util.List;

public interface IFilialService {

    List<FilialResponseDTO> findAll();

    FilialResponseDTO findById(Integer id);

    FilialResponseDTO create(FilialRequestDTO requestDTO);

    FilialResponseDTO update(Integer id, FilialRequestDTO requestDTO);

    void delete(Integer id);
}
