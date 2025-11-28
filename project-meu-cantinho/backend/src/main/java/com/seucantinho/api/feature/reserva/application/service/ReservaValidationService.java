package com.seucantinho.api.feature.reserva.application.service;

import com.seucantinho.api.feature.reserva.domain.Reserva;
import com.seucantinho.api.feature.reserva.infrastructure.persistence.ReservaRepository;
import com.seucantinho.api.feature.espaco.domain.Espaco;
import com.seucantinho.api.feature.reserva.domain.valueobject.DataEvento;
import com.seucantinho.api.shared.domain.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class ReservaValidationService {

    private final ReservaRepository reservaRepository;

    public void validateEspacoAtivo(Espaco espaco) {
        if (!espaco.getAtivo()) {
            throw new BusinessException("Espaço não está ativo para reservas");
        }
    }

    public void validateDisponibilidade(Integer espacoId, LocalDate dataEvento, Integer reservaId) {
        DataEvento.of(dataEvento);
        if (reservaRepository.existsReservaAtivaByEspacoAndData(espacoId, dataEvento, reservaId)) {
            throw new BusinessException("Espaço já possui reserva ativa para esta data");
        }
    }

    public void validateValorTotal(Reserva reserva) {
        if (reserva.getEspaco() == null) {
            throw new BusinessException("Espaço não pode ser nulo");
        }

        if (!reserva.getValorTotal().isIgualA(reserva.getEspaco().getPrecoDiaria())) {
            throw new BusinessException(
                String.format("Valor total incorreto. Esperado: %s, Recebido: %s",
                    reserva.getEspaco().getPrecoDiaria().getValorFormatado(),
                    reserva.getValorTotal().getValorFormatado())
            );
        }
    }
}

