package com.seucantinho.api.feature.cliente.application.service;

import com.seucantinho.api.feature.cliente.domain.Cliente;
import com.seucantinho.api.feature.cliente.application.dto.ClienteRequestDTO;
import com.seucantinho.api.feature.cliente.application.dto.ClienteResponseDTO;
import com.seucantinho.api.shared.domain.exception.ResourceNotFoundException;
import com.seucantinho.api.feature.cliente.infrastructure.mapper.ClienteMapper;
import com.seucantinho.api.feature.cliente.domain.port.out.ClienteRepositoryPort;
import com.seucantinho.api.feature.cliente.domain.port.in.ClienteServicePort;
import com.seucantinho.api.feature.cliente.domain.service.ClienteUniquenessService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClienteService implements ClienteServicePort {

    private final ClienteRepositoryPort clienteRepositoryPort;
    private final ClienteMapper clienteMapper;
    private final ClienteUniquenessService clienteUniquenessService;

    @Override
    @Transactional(readOnly = true)
    public List<ClienteResponseDTO> findAll() {
        return clienteRepositoryPort.findAll().stream()
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
        clienteUniquenessService.validarEmailUnico(requestDTO.getEmail());
        clienteUniquenessService.validarCpfUnico(requestDTO.getCpf());

        Cliente cliente = clienteMapper.toEntity(requestDTO);
        cliente.validar();
        Cliente savedCliente = clienteRepositoryPort.save(cliente);
        return clienteMapper.toResponseDTO(savedCliente);
    }

    @Override
    @Transactional
    public ClienteResponseDTO update(Integer id, ClienteRequestDTO requestDTO) {
        Cliente cliente = findClienteById(id);
        clienteUniquenessService.validarEmailUnicoParaAtualizacao(requestDTO.getEmail(), id);

        clienteMapper.updateEntityFromDTO(cliente, requestDTO);
        cliente.validar();

        clienteMapper.updateEntityFromDTO(cliente, requestDTO);
        Cliente updatedCliente = clienteRepositoryPort.save(cliente);
        return clienteMapper.toResponseDTO(updatedCliente);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        if (clienteRepositoryPort.findById(id).isEmpty()) {
            throw new ResourceNotFoundException("Cliente não encontrado com ID: " + id);
        }
        clienteRepositoryPort.deleteById(id);
    }

    @Override
    @Transactional
    public ClienteResponseDTO toggleAtivo(Integer id, Boolean ativo) {
        Cliente cliente = findClienteById(id);
        cliente.setAtivo(ativo);
        Cliente updatedCliente = clienteRepositoryPort.save(cliente);
        return clienteMapper.toResponseDTO(updatedCliente);
    }

    private Cliente findClienteById(Integer id) {
        return clienteRepositoryPort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado com ID: " + id));
    }
}
