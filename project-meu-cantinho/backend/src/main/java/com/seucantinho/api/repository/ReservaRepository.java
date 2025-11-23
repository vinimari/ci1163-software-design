package com.seucantinho.api.repository;

import com.seucantinho.api.domain.entity.Reserva;
import com.seucantinho.api.domain.enums.StatusReservaEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Integer> {

    List<Reserva> findByUsuarioId(Integer usuarioId);

    List<Reserva> findByEspacoId(Integer espacoId);

    List<Reserva> findByStatus(StatusReservaEnum status);

    List<Reserva> findByDataEvento(LocalDate dataEvento);

    @Query("SELECT r FROM Reserva r " +
           "LEFT JOIN FETCH r.usuario " +
           "LEFT JOIN FETCH r.espaco " +
           "WHERE r.id = :id")
    Optional<Reserva> findByIdWithDetails(Integer id);

    @Query("SELECT r FROM Reserva r " +
           "LEFT JOIN FETCH r.pagamentos " +
           "WHERE r.id = :id")
    Optional<Reserva> findByIdWithPagamentos(Integer id);

    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END " +
           "FROM Reserva r " +
           "WHERE r.espaco.id = :espacoId " +
           "AND r.dataEvento = :dataEvento " +
           "AND r.status NOT IN ('CANCELADA', 'FINALIZADA') " +
           "AND (:reservaId IS NULL OR r.id <> :reservaId)")
    boolean existsReservaAtivaByEspacoAndData(
        @Param("espacoId") Integer espacoId,
        @Param("dataEvento") LocalDate dataEvento,
        @Param("reservaId") Integer reservaId
    );

    @Query("SELECT r FROM Reserva r " +
           "WHERE r.espaco.filial.id = :filialId " +
           "AND r.dataEvento BETWEEN :dataInicio AND :dataFim")
    List<Reserva> findReservasByFilialAndPeriodo(
        @Param("filialId") Integer filialId,
        @Param("dataInicio") LocalDate dataInicio,
        @Param("dataFim") LocalDate dataFim
    );

    @Query("SELECT r FROM Reserva r WHERE r.espaco.filial.id = :filialId")
    List<Reserva> findByEspacoFilialId(@Param("filialId") Integer filialId);
}
