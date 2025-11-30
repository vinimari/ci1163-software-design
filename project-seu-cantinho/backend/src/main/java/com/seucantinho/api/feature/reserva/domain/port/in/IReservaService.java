package com.seucantinho.api.feature.reserva.domain.port.in;

import com.seucantinho.api.feature.reserva.domain.enums.StatusReservaEnum;
import com.seucantinho.api.feature.reserva.application.dto.ReservaRequestDTO;
import com.seucantinho.api.feature.reserva.application.dto.ReservaResponseDTO;

import java.util.List;

public interface IReservaService {

    List<ReservaResponseDTO> findAll();

    ReservaResponseDTO findById(Integer id);

    List<ReservaResponseDTO> findByUsuarioId(Integer usuarioId);

    List<ReservaResponseDTO> findByEspacoId(Integer espacoId);

    ReservaResponseDTO create(ReservaRequestDTO requestDTO);

    ReservaResponseDTO update(Integer id, ReservaRequestDTO requestDTO);

    ReservaResponseDTO updateStatus(Integer id, StatusReservaEnum novoStatus);

    void delete(Integer id);

    List<ReservaResponseDTO> findByAcessoPorEmail(String email);
}
