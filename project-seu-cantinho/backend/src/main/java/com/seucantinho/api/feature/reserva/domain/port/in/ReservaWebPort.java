package com.seucantinho.api.feature.reserva.domain.port.in;

import com.seucantinho.api.feature.reserva.application.dto.ReservaRequestDTO;
import com.seucantinho.api.feature.reserva.application.dto.ReservaResponseDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ReservaWebPort {

    ResponseEntity<List<ReservaResponseDTO>> findAll();

    ResponseEntity<ReservaResponseDTO> findById(Integer id);

    ResponseEntity<List<ReservaResponseDTO>> findByUsuarioId(Integer usuarioId);

    ResponseEntity<List<ReservaResponseDTO>> findByEspacoId(Integer espacoId);

    ResponseEntity<ReservaResponseDTO> create(ReservaRequestDTO requestDTO);

    ResponseEntity<ReservaResponseDTO> update(Integer id, ReservaRequestDTO requestDTO);

    ResponseEntity<Void> delete(Integer id);

    ResponseEntity<List<ReservaResponseDTO>> findByAcessoPorEmail(String email);
}