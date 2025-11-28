package com.seucantinho.api.feature.cliente.infrastructure.adapter.out;

import com.seucantinho.api.feature.cliente.domain.Cliente;
import com.seucantinho.api.feature.cliente.domain.port.out.ClienteRepositoryPort;
import com.seucantinho.api.feature.cliente.infrastructure.persistence.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ClienteRepositoryAdapter implements ClienteRepositoryPort {

    private final ClienteRepository clienteRepository;

    @Override
    public List<Cliente> findAll() {
        return clienteRepository.findAll();
    }

    @Override
    public Optional<Cliente> findById(Integer id) {
        return clienteRepository.findById(id);
    }

    @Override
    public Optional<Cliente> findByEmail(String email) {
        return clienteRepository.findByEmail(email);
    }

    @Override
    public Optional<Cliente> findByCpf(String cpf) {
        return clienteRepository.findByCpf(cpf);
    }

    @Override
    public boolean existsByEmail(String email) {
        return clienteRepository.findByEmail(email).isPresent();
    }

    @Override
    public boolean existsByCpf(String cpf) {
        return clienteRepository.findByCpf(cpf).isPresent();
    }

    @Override
    public Cliente save(Cliente cliente) {
        return clienteRepository.save(cliente);
    }

    @Override
    public void deleteById(Integer id) {
        clienteRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Integer id) {
        return clienteRepository.existsById(id);
    }
}