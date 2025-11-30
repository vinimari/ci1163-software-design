package com.seucantinho.api.feature.cliente.domain.port.in;

import com.seucantinho.api.feature.cliente.application.dto.ClienteRequestDTO;
import com.seucantinho.api.feature.cliente.application.dto.ClienteResponseDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ClienteWebPort {

    ResponseEntity<List<ClienteResponseDTO>> findAll();

    ResponseEntity<ClienteResponseDTO> findById(Integer id);

    ResponseEntity<ClienteResponseDTO> create(ClienteRequestDTO requestDTO);

    ResponseEntity<ClienteResponseDTO> update(Integer id, ClienteRequestDTO requestDTO);

    ResponseEntity<Void> delete(Integer id);

    ResponseEntity<ClienteResponseDTO> toggleAtivo(Integer id, Boolean ativo);
}