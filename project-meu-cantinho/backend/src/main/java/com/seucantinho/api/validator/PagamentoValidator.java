package com.seucantinho.api.validator;

import com.seucantinho.api.domain.entity.Reserva;
import com.seucantinho.api.domain.enums.TipoPagamentoEnum;
import com.seucantinho.api.dto.pagamento.PagamentoRequestDTO;
import com.seucantinho.api.exception.BusinessException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Validador responsável pelas regras de negócio relacionadas a pagamentos.
 * Aplica o princípio SRP (Single Responsibility Principle).
 */
@Component
public class PagamentoValidator {

    public void validatePagamento(PagamentoRequestDTO requestDTO, Reserva reserva) {
        BigDecimal valorTotal = reserva.getValorTotal();
        BigDecimal metadeValor = valorTotal.divide(new BigDecimal("2"), 2, RoundingMode.HALF_UP);
        boolean possuiPagamentos = !reserva.getPagamentos().isEmpty();

        switch (requestDTO.getTipo()) {
            case TOTAL:
                // TOTAL só pode ser pago na criação da reserva (quando não há pagamentos)
                if (possuiPagamentos) {
                    throw new BusinessException("Pagamento TOTAL só pode ser feito na criação da reserva");
                }
                // TOTAL deve ser 100% do valor
                if (requestDTO.getValor().compareTo(valorTotal) != 0) {
                    throw new BusinessException(
                        String.format("Pagamento TOTAL deve ser o valor completo: R$ %.2f", valorTotal)
                    );
                }
                break;

            case SINAL:
                // SINAL só pode ser pago na criação da reserva (quando não há pagamentos)
                if (possuiPagamentos) {
                    throw new BusinessException("Pagamento SINAL só pode ser feito na criação da reserva");
                }
                // SINAL deve ser 50% do valor
                if (requestDTO.getValor().compareTo(metadeValor) != 0) {
                    throw new BusinessException(
                        String.format("Pagamento SINAL deve ser 50%% do valor total: R$ %.2f", metadeValor)
                    );
                }
                break;

            case QUITACAO:
                // QUITACAO só pode ser feito se já existe um pagamento de SINAL
                if (!possuiPagamentos) {
                    throw new BusinessException("Pagamento QUITACAO só pode ser feito após o pagamento do SINAL");
                }
                // Verificar se o primeiro pagamento foi SINAL
                if (reserva.getPagamentos().get(0).getTipo() != TipoPagamentoEnum.SINAL) {
                    throw new BusinessException("Pagamento QUITACAO só é permitido para reservas com pagamento inicial do tipo SINAL");
                }
                // Verificar se já foi quitado
                if (reserva.getPagamentos().size() > 1) {
                    throw new BusinessException("Esta reserva já foi quitada");
                }
                // QUITACAO deve ser os 50% restantes
                BigDecimal saldoRestante = valorTotal.subtract(reserva.calcularTotalPago());
                if (requestDTO.getValor().compareTo(saldoRestante) != 0) {
                    throw new BusinessException(
                        String.format("Pagamento QUITACAO deve ser o saldo restante: R$ %.2f", saldoRestante)
                    );
                }
                break;

            default:
                throw new BusinessException("Tipo de pagamento inválido");
        }
    }
}
