package com.seucantinho.api.feature.cliente.domain.service;

import com.seucantinho.api.feature.cliente.domain.port.out.ClienteRepositoryPort;
import com.seucantinho.api.shared.domain.exception.DuplicateResourceException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ClienteUniquenessService {

    private final ClienteRepositoryPort clienteRepositoryPort;

    public void validarEmailUnico(String email) {
        if (clienteRepositoryPort.findByEmail(email).isPresent()) {
            throw new DuplicateResourceException("Email já cadastrado: " + email);
        }
    }

    public void validarEmailUnicoParaAtualizacao(String email, Integer clienteId) {
        clienteRepositoryPort.findByEmail(email).ifPresent(existingCliente -> {
            if (!existingCliente.getId().equals(clienteId)) {
                throw new DuplicateResourceException("Email já cadastrado: " + email);
            }
        });
    }

    public void validarCpfUnico(String cpf) {
        if (cpf != null && clienteRepositoryPort.findByCpf(cpf).isPresent()) {
            throw new DuplicateResourceException("CPF já cadastrado: " + cpf);
        }
    }
}