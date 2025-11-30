package com.seucantinho.api.feature.pagamento.domain.port.in;

import com.seucantinho.api.feature.pagamento.application.dto.PagamentoRequestDTO;
import com.seucantinho.api.feature.pagamento.application.dto.PagamentoResponseDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface PagamentoWebPort {

    ResponseEntity<List<PagamentoResponseDTO>> findAll();

    ResponseEntity<PagamentoResponseDTO> findById(Integer id);

    ResponseEntity<List<PagamentoResponseDTO>> findByReservaId(Integer reservaId);

    ResponseEntity<PagamentoResponseDTO> create(PagamentoRequestDTO requestDTO);
}