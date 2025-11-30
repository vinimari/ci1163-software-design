package com.seucantinho.api.feature.pagamento.domain.port.in;

import com.seucantinho.api.feature.pagamento.application.dto.PagamentoRequestDTO;
import com.seucantinho.api.feature.pagamento.application.dto.PagamentoResponseDTO;

import java.util.List;

public interface PagamentoServicePort {

    List<PagamentoResponseDTO> findAll();

    PagamentoResponseDTO findById(Integer id);

    List<PagamentoResponseDTO> findByReservaId(Integer reservaId);

    PagamentoResponseDTO create(PagamentoRequestDTO requestDTO);
}
