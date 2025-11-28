package com.seucantinho.api.validator;

import com.seucantinho.api.domain.entity.Espaco;
import com.seucantinho.api.domain.entity.Reserva;
import com.seucantinho.api.domain.valueobject.DataEvento;
import com.seucantinho.api.exception.BusinessException;
import com.seucantinho.api.repository.ReservaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class ReservaValidator {

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
