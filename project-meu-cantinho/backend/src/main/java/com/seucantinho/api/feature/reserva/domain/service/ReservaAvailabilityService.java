package com.seucantinho.api.feature.reserva.domain.service;

import com.seucantinho.api.feature.reserva.domain.port.out.ReservaRepositoryPort;
import com.seucantinho.api.shared.domain.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class ReservaAvailabilityService {

    private final ReservaRepositoryPort reservaRepositoryPort;

    public void validarDisponibilidade(Integer espacoId, LocalDate dataEvento, Integer reservaId) {
        if (reservaRepositoryPort.existsActiveReservationByEspacoAndData(espacoId, dataEvento, reservaId)) {
            throw new BusinessException("Espaço já possui reserva ativa para esta data");
        }
    }
}