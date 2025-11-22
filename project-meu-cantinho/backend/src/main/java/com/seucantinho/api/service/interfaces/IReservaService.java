package com.seucantinho.api.service.interfaces;

import com.seucantinho.api.domain.enums.StatusReservaEnum;
import com.seucantinho.api.dto.reserva.ReservaRequestDTO;
import com.seucantinho.api.dto.reserva.ReservaResponseDTO;

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
}
