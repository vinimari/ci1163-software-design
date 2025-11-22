package com.seucantinho.api.service.interfaces;

import com.seucantinho.api.dto.usuario.ClienteRequestDTO;
import com.seucantinho.api.dto.usuario.ClienteResponseDTO;

import java.util.List;

public interface IClienteService {

    List<ClienteResponseDTO> findAll();

    ClienteResponseDTO findById(Integer id);

    ClienteResponseDTO create(ClienteRequestDTO requestDTO);

    ClienteResponseDTO update(Integer id, ClienteRequestDTO requestDTO);

    void delete(Integer id);
}
