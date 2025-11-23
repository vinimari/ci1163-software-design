package com.seucantinho.api.service.interfaces;

import com.seucantinho.api.dto.pagamento.PagamentoRequestDTO;
import com.seucantinho.api.dto.pagamento.PagamentoResponseDTO;

import java.util.List;

public interface IPagamentoService {

    List<PagamentoResponseDTO> findAll();

    PagamentoResponseDTO findById(Integer id);

    List<PagamentoResponseDTO> findByReservaId(Integer reservaId);

    PagamentoResponseDTO create(PagamentoRequestDTO requestDTO);
}
