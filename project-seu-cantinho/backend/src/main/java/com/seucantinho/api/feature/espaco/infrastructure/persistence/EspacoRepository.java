package com.seucantinho.api.feature.espaco.infrastructure.persistence;

import com.seucantinho.api.feature.espaco.domain.Espaco;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface EspacoRepository extends JpaRepository<Espaco, Integer> {

    List<Espaco> findByFilialId(Integer filialId);

    List<Espaco> findByAtivoTrue();

    List<Espaco> findByFilialIdAndAtivoTrue(Integer filialId);

    @Query("SELECT e FROM Espaco e LEFT JOIN FETCH e.filial WHERE e.id = :id")
    Optional<Espaco> findByIdWithFilial(Integer id);

    @Query("SELECT e FROM Espaco e WHERE e.ativo = true " +
           "AND e.capacidade >= :capacidadeMinima " +
           "AND e.id NOT IN (" +
           "  SELECT r.espaco.id FROM Reserva r " +
           "  WHERE r.dataEvento = :data " +
           "  AND r.status NOT IN ('CANCELADA')" +
           ")")
    List<Espaco> findEspacosDisponiveisPorData(
        @Param("data") LocalDate data,
        @Param("capacidadeMinima") Integer capacidadeMinima
    );
}
