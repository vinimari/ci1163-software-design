package com.seucantinho.api.feature.funcionario.domain.service;

import com.seucantinho.api.feature.funcionario.domain.port.out.FuncionarioRepositoryPort;
import com.seucantinho.api.feature.usuario.domain.port.out.UsuarioRepositoryPort;
import com.seucantinho.api.shared.domain.exception.DuplicateResourceException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FuncionarioUniquenessService {

    private final FuncionarioRepositoryPort funcionarioRepositoryPort;
    private final UsuarioRepositoryPort usuarioRepositoryPort;

    public void validarEmailUnico(String email) {
        // Verifica em toda a tabela de usuários (Cliente, Funcionário, Administrador)
        if (usuarioRepositoryPort.findByEmail(email).isPresent()) {
            throw new DuplicateResourceException("Email já cadastrado no sistema");
        }
    }

    public void validarEmailUnicoParaAtualizacao(String email, Integer funcionarioId) {
        // Verifica em toda a tabela de usuários
        usuarioRepositoryPort.findByEmail(email).ifPresent(existingUsuario -> {
            if (!existingUsuario.getId().equals(funcionarioId)) {
                throw new DuplicateResourceException("Email já cadastrado no sistema");
            }
        });
    }

    public void validarMatriculaUnica(String matricula) {
        if (funcionarioRepositoryPort.findByMatricula(matricula).isPresent()) {
            throw new DuplicateResourceException("Matrícula já cadastrada");
        }
    }

    public void validarMatriculaUnicaParaAtualizacao(String matricula, Integer funcionarioId) {
        funcionarioRepositoryPort.findByMatricula(matricula).ifPresent(existingFuncionario -> {
            if (!existingFuncionario.getId().equals(funcionarioId)) {
                throw new DuplicateResourceException("Matrícula já cadastrada");
            }
        });
    }
}
