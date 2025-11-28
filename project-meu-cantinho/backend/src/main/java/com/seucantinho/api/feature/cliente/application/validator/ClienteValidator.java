package com.seucantinho.api.feature.cliente.application.validator;

import com.seucantinho.api.shared.domain.exception.DuplicateResourceException;
import com.seucantinho.api.feature.cliente.domain.port.out.ClienteRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ClienteValidator {

    private final ClienteRepositoryPort clienteRepositoryPort;

    public void validateEmailUnique(String email) {
        if (clienteRepositoryPort.findByEmail(email).isPresent()) {
            throw new DuplicateResourceException("Email já cadastrado: " + email);
        }
    }

    public void validateEmailUniqueForUpdate(String email, Integer clienteId) {
        clienteRepositoryPort.findByEmail(email).ifPresent(existingCliente -> {
            if (!existingCliente.getId().equals(clienteId)) {
                throw new DuplicateResourceException("Email já cadastrado: " + email);
            }
        });
    }

    public void validateCpfUnique(String cpf) {
        if (cpf != null && clienteRepositoryPort.findByCpf(cpf).isPresent()) {
            throw new DuplicateResourceException("CPF já cadastrado: " + cpf);
        }
    }
}
