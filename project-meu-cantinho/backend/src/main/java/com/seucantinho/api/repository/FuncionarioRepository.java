package com.seucantinho.api.repository;

import com.seucantinho.api.domain.entity.Funcionario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FuncionarioRepository extends JpaRepository<Funcionario, Integer> {

    Optional<Funcionario> findByEmail(String email);

    Optional<Funcionario> findByMatricula(String matricula);

    List<Funcionario> findByFilialId(Integer filialId);

    @Query("SELECT f FROM Funcionario f LEFT JOIN FETCH f.filial WHERE f.id = :id")
    Optional<Funcionario> findByIdWithFilial(Integer id);

    boolean existsByMatricula(String matricula);
}
