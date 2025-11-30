package com.seucantinho.api.feature.reserva.domain.port.out;

import com.seucantinho.api.feature.reserva.domain.Reserva;
import com.seucantinho.api.feature.reserva.domain.enums.StatusReservaEnum;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ReservaRepositoryPort {

    List<Reserva> findAll();

    Optional<Reserva> findById(Integer id);

    List<Reserva> findByUsuarioId(Integer usuarioId);

    List<Reserva> findByEspacoId(Integer espacoId);

    List<Reserva> findByStatus(StatusReservaEnum status);

    Optional<Reserva> findByIdWithPagamentos(Integer id);

    boolean existsActiveReservationByEspacoAndData(Integer espacoId, LocalDate dataEvento, Integer excludeReservaId);

    Reserva save(Reserva reserva);

    void deleteById(Integer id);

    boolean existsById(Integer id);
}