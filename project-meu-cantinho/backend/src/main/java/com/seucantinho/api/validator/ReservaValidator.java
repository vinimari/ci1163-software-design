package com.seucantinho.api.validator;

import com.seucantinho.api.domain.entity.Espaco;
import com.seucantinho.api.domain.entity.Reserva;
import com.seucantinho.api.exception.BusinessException;
import com.seucantinho.api.repository.ReservaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
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
        if (reservaRepository.existsReservaAtivaByEspacoAndData(espacoId, dataEvento, reservaId)) {
            throw new BusinessException("Espaço já possui reserva ativa para esta data");
        }
    }

    public void validateValorTotal(Reserva reserva) {
        if (reserva.getEspaco() == null) {
            throw new BusinessException("Espaço não pode ser nulo");
        }

        BigDecimal valorEsperado = reserva.getEspaco().getPrecoDiaria();
        if (reserva.getValorTotal().compareTo(valorEsperado) != 0) {
            throw new BusinessException(
                String.format("Valor total incorreto. Esperado: R$ %s, Recebido: R$ %s",
                    valorEsperado, reserva.getValorTotal())
            );
        }
    }
}
