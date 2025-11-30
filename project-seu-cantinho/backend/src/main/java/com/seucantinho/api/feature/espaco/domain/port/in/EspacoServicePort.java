package com.seucantinho.api.feature.espaco.domain.port.in;

import com.seucantinho.api.feature.espaco.application.dto.EspacoRequestDTO;
import com.seucantinho.api.feature.espaco.application.dto.EspacoResponseDTO;

import java.time.LocalDate;
import java.util.List;

public interface EspacoServicePort {

    List<EspacoResponseDTO> findAll();

    List<EspacoResponseDTO> findByFilialId(Integer filialId);

    List<EspacoResponseDTO> findAtivos();

    List<EspacoResponseDTO> findDisponiveisPorData(LocalDate data, Integer capacidadeMinima);

    EspacoResponseDTO findById(Integer id);

    EspacoResponseDTO create(EspacoRequestDTO requestDTO);

    EspacoResponseDTO update(Integer id, EspacoRequestDTO requestDTO);

    void delete(Integer id);
}
