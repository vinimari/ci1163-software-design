package com.seucantinho.api.feature.cliente.domain.port.in;

import com.seucantinho.api.feature.cliente.application.dto.ClienteRequestDTO;
import com.seucantinho.api.feature.cliente.application.dto.ClienteResponseDTO;

import java.util.List;

public interface IClienteService {

    List<ClienteResponseDTO> findAll();

    ClienteResponseDTO findById(Integer id);

    ClienteResponseDTO create(ClienteRequestDTO requestDTO);

    ClienteResponseDTO update(Integer id, ClienteRequestDTO requestDTO);

    void delete(Integer id);

    ClienteResponseDTO toggleAtivo(Integer id, Boolean ativo);
}
