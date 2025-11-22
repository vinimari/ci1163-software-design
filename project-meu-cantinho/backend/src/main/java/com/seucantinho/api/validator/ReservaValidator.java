package com.seucantinho.api.validator;

import com.seucantinho.api.domain.entity.Espaco;
import com.seucantinho.api.exception.BusinessException;
import com.seucantinho.api.repository.ReservaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * Validador responsável pelas regras de negócio relacionadas a reservas.
 * Aplica o princípio SRP (Single Responsibility Principle).
 */
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
}
