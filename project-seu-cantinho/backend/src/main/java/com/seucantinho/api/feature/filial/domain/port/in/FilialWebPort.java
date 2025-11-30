package com.seucantinho.api.feature.filial.domain.port.in;

import com.seucantinho.api.feature.filial.application.dto.FilialRequestDTO;
import com.seucantinho.api.feature.filial.application.dto.FilialResponseDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface FilialWebPort {

    ResponseEntity<List<FilialResponseDTO>> findAll();

    ResponseEntity<FilialResponseDTO> findById(Integer id);

    ResponseEntity<FilialResponseDTO> create(FilialRequestDTO requestDTO);

    ResponseEntity<FilialResponseDTO> update(Integer id, FilialRequestDTO requestDTO);

    ResponseEntity<Void> delete(Integer id);
}