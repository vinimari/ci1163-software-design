package com.seucantinho.api.feature.cliente.domain.service;

import com.seucantinho.api.feature.cliente.domain.port.out.ClienteRepositoryPort;
import com.seucantinho.api.feature.usuario.domain.port.out.UsuarioRepositoryPort;
import com.seucantinho.api.shared.domain.exception.DuplicateResourceException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ClienteUniquenessService {

    private final ClienteRepositoryPort clienteRepositoryPort;
    private final UsuarioRepositoryPort usuarioRepositoryPort;

    public void validarEmailUnico(String email) {
        // Verifica em toda a tabela de usuários (Cliente, Funcionário, Administrador)
        if (usuarioRepositoryPort.findByEmail(email).isPresent()) {
            throw new DuplicateResourceException("Email já cadastrado no sistema");
        }
    }

    public void validarEmailUnicoParaAtualizacao(String email, Integer clienteId) {
        // Verifica em toda a tabela de usuários
        usuarioRepositoryPort.findByEmail(email).ifPresent(existingUsuario -> {
            if (!existingUsuario.getId().equals(clienteId)) {
                throw new DuplicateResourceException("Email já cadastrado no sistema");
            }
        });
    }

    public void validarCpfUnico(String cpf) {
        if (cpf != null && clienteRepositoryPort.findByCpf(cpf).isPresent()) {
            throw new DuplicateResourceException("CPF já cadastrado");
        }
    }
}