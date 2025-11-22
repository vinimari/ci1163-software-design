package com.seucantinho.api.service.interfaces;

import com.seucantinho.api.dto.espaco.EspacoRequestDTO;
import com.seucantinho.api.dto.espaco.EspacoResponseDTO;

import java.time.LocalDate;
import java.util.List;

public interface IEspacoService {

    List<EspacoResponseDTO> findAll();

    List<EspacoResponseDTO> findByFilialId(Integer filialId);

    List<EspacoResponseDTO> findAtivos();

    List<EspacoResponseDTO> findDisponiveisPorData(LocalDate data, Integer capacidadeMinima);

    EspacoResponseDTO findById(Integer id);

    EspacoResponseDTO create(EspacoRequestDTO requestDTO);

    EspacoResponseDTO update(Integer id, EspacoRequestDTO requestDTO);

    void delete(Integer id);
}
