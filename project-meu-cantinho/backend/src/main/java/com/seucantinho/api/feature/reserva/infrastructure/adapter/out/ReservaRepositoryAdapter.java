package com.seucantinho.api.feature.reserva.infrastructure.adapter.out;

import com.seucantinho.api.feature.reserva.domain.Reserva;
import com.seucantinho.api.feature.reserva.domain.enums.StatusReservaEnum;
import com.seucantinho.api.feature.reserva.domain.port.out.ReservaRepositoryPort;
import com.seucantinho.api.feature.reserva.infrastructure.persistence.ReservaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ReservaRepositoryAdapter implements ReservaRepositoryPort {

    private final ReservaRepository reservaRepository;

    @Override
    public List<Reserva> findAll() {
        return reservaRepository.findAll();
    }

    @Override
    public Optional<Reserva> findById(Integer id) {
        return reservaRepository.findById(id);
    }

    @Override
    public List<Reserva> findByUsuarioId(Integer usuarioId) {
        return reservaRepository.findByUsuarioId(usuarioId);
    }

    @Override
    public List<Reserva> findByEspacoId(Integer espacoId) {
        return reservaRepository.findByEspacoId(espacoId);
    }

    @Override
    public List<Reserva> findByStatus(StatusReservaEnum status) {
        return reservaRepository.findByStatus(status);
    }

    @Override
    public Optional<Reserva> findByIdWithPagamentos(Integer id) {
        return reservaRepository.findByIdWithPagamentos(id);
    }

    @Override
    public boolean existsActiveReservationByEspacoAndData(Integer espacoId, LocalDate dataEvento, Integer excludeReservaId) {
        return reservaRepository.existsReservaAtivaByEspacoAndData(espacoId, dataEvento, excludeReservaId);
    }

    @Override
    public Reserva save(Reserva reserva) {
        return reservaRepository.save(reserva);
    }

    @Override
    public void deleteById(Integer id) {
        reservaRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Integer id) {
        return reservaRepository.existsById(id);
    }
}