package com.seucantinho.api.service;

import com.seucantinho.api.domain.entity.Cliente;
import com.seucantinho.api.dto.usuario.ClienteRequestDTO;
import com.seucantinho.api.dto.usuario.ClienteResponseDTO;
import com.seucantinho.api.exception.ResourceNotFoundException;
import com.seucantinho.api.mapper.ClienteMapper;
import com.seucantinho.api.repository.ClienteRepository;
import com.seucantinho.api.service.interfaces.IClienteService;
import com.seucantinho.api.validator.ClienteValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClienteService implements IClienteService {

    private final ClienteRepository clienteRepository;
    private final ClienteMapper clienteMapper;
    private final ClienteValidator clienteValidator;

    @Override
    @Transactional(readOnly = true)
    public List<ClienteResponseDTO> findAll() {
        return clienteRepository.findAll().stream()
                .map(clienteMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ClienteResponseDTO findById(Integer id) {
        Cliente cliente = findClienteById(id);
        return clienteMapper.toResponseDTO(cliente);
    }

    @Override
    @Transactional
    public ClienteResponseDTO create(ClienteRequestDTO requestDTO) {
        clienteValidator.validateEmailUnique(requestDTO.getEmail());
        clienteValidator.validateCpfUnique(requestDTO.getCpf());

        Cliente cliente = clienteMapper.toEntity(requestDTO);
        Cliente savedCliente = clienteRepository.save(cliente);
        return clienteMapper.toResponseDTO(savedCliente);
    }

    @Override
    @Transactional
    public ClienteResponseDTO update(Integer id, ClienteRequestDTO requestDTO) {
        Cliente cliente = findClienteById(id);
        clienteValidator.validateEmailUniqueForUpdate(requestDTO.getEmail(), id);

        clienteMapper.updateEntityFromDTO(cliente, requestDTO);
        Cliente updatedCliente = clienteRepository.save(cliente);
        return clienteMapper.toResponseDTO(updatedCliente);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        if (!clienteRepository.existsById(id)) {
            throw new ResourceNotFoundException("Cliente não encontrado com ID: " + id);
        }
        clienteRepository.deleteById(id);
    }

    private Cliente findClienteById(Integer id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado com ID: " + id));
    }
}
