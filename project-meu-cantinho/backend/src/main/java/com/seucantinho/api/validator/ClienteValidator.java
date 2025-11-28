package com.seucantinho.api.validator;

import com.seucantinho.api.exception.DuplicateResourceException;
import com.seucantinho.api.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ClienteValidator {

    private final ClienteRepository clienteRepository;

    public void validateEmailUnique(String email) {
        if (clienteRepository.findByEmail(email).isPresent()) {
            throw new DuplicateResourceException("Email já cadastrado: " + email);
        }
    }

    public void validateEmailUniqueForUpdate(String email, Integer clienteId) {
        clienteRepository.findByEmail(email).ifPresent(existingCliente -> {
            if (!existingCliente.getId().equals(clienteId)) {
                throw new DuplicateResourceException("Email já cadastrado: " + email);
            }
        });
    }

    public void validateCpfUnique(String cpf) {
        if (cpf != null && clienteRepository.findByCpf(cpf).isPresent()) {
            throw new DuplicateResourceException("CPF já cadastrado: " + cpf);
        }
    }
}
