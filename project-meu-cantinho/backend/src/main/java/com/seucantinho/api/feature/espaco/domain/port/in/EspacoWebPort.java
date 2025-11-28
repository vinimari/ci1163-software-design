package com.seucantinho.api.feature.espaco.domain.port.in;

import com.seucantinho.api.feature.espaco.application.dto.EspacoRequestDTO;
import com.seucantinho.api.feature.espaco.application.dto.EspacoResponseDTO;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;

public interface EspacoWebPort {

    ResponseEntity<List<EspacoResponseDTO>> findAll();

    ResponseEntity<EspacoResponseDTO> findById(Integer id);

    ResponseEntity<List<EspacoResponseDTO>> findByFilialId(Integer filialId);

    ResponseEntity<List<EspacoResponseDTO>> findAtivos();

    ResponseEntity<List<EspacoResponseDTO>> findDisponiveis(LocalDate data, Integer capacidadeMinima);

    ResponseEntity<EspacoResponseDTO> create(EspacoRequestDTO requestDTO);

    ResponseEntity<EspacoResponseDTO> update(Integer id, EspacoRequestDTO requestDTO);

    ResponseEntity<Void> delete(Integer id);
}