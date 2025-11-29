package com.seucantinho.api.feature.funcionario.domain.port.out;

import com.seucantinho.api.feature.funcionario.domain.Funcionario;

import java.util.List;
import java.util.Optional;

public interface FuncionarioRepositoryPort {

    List<Funcionario> findAll();

    List<Funcionario> findByFilialId(Integer filialId);

    Optional<Funcionario> findById(Integer id);

    Optional<Funcionario> findByEmail(String email);

    Optional<Funcionario> findByMatricula(String matricula);

    Funcionario save(Funcionario funcionario);

    void deleteById(Integer id);

    boolean existsByEmail(String email);

    boolean existsByMatricula(String matricula);
}
