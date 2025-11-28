package com.seucantinho.api.feature.cliente.domain.port.out;

import com.seucantinho.api.feature.cliente.domain.Cliente;

import java.util.List;
import java.util.Optional;

public interface ClienteRepositoryPort {

    List<Cliente> findAll();

    Optional<Cliente> findById(Integer id);

    Optional<Cliente> findByEmail(String email);

    Optional<Cliente> findByCpf(String cpf);

    boolean existsByEmail(String email);

    boolean existsByCpf(String cpf);

    Cliente save(Cliente cliente);

    void deleteById(Integer id);

    boolean existsById(Integer id);
}