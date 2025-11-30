package com.seucantinho.api.feature.funcionario.infrastructure.adapter.out;

import com.seucantinho.api.feature.funcionario.domain.Funcionario;
import com.seucantinho.api.feature.funcionario.domain.port.out.FuncionarioRepositoryPort;
import com.seucantinho.api.feature.funcionario.infrastructure.persistence.FuncionarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class FuncionarioRepositoryAdapter implements FuncionarioRepositoryPort {

    private final FuncionarioRepository funcionarioRepository;

    @Override
    public List<Funcionario> findAll() {
        return funcionarioRepository.findAll();
    }

    @Override
    public List<Funcionario> findByFilialId(Integer filialId) {
        return funcionarioRepository.findByFilialId(filialId);
    }

    @Override
    public Optional<Funcionario> findById(Integer id) {
        return funcionarioRepository.findByIdWithFilial(id);
    }

    @Override
    public Optional<Funcionario> findByEmail(String email) {
        return funcionarioRepository.findByEmail(email);
    }

    @Override
    public Optional<Funcionario> findByMatricula(String matricula) {
        return funcionarioRepository.findByMatricula(matricula);
    }

    @Override
    public Funcionario save(Funcionario funcionario) {
        return funcionarioRepository.save(funcionario);
    }

    @Override
    public void deleteById(Integer id) {
        funcionarioRepository.deleteById(id);
    }

    @Override
    public boolean existsByEmail(String email) {
        return funcionarioRepository.findByEmail(email).isPresent();
    }

    @Override
    public boolean existsByMatricula(String matricula) {
        return funcionarioRepository.existsByMatricula(matricula);
    }
}
