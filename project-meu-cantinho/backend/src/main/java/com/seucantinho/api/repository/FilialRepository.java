package com.seucantinho.api.repository;

import com.seucantinho.api.domain.entity.Filial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FilialRepository extends JpaRepository<Filial, Integer> {

    Optional<Filial> findByNome(String nome);

    List<Filial> findByCidade(String cidade);

    List<Filial> findByEstado(String estado);

    @Query("SELECT f FROM Filial f LEFT JOIN FETCH f.espacos WHERE f.id = :id")
    Optional<Filial> findByIdWithEspacos(Integer id);
}
