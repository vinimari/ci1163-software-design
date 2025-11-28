package com.seucantinho.api.feature.pagamento.application.validator;

import com.seucantinho.api.feature.reserva.domain.Reserva;
import com.seucantinho.api.feature.pagamento.domain.enums.TipoPagamentoEnum;
import com.seucantinho.api.shared.domain.valueobject.ValorMonetario;
import com.seucantinho.api.feature.pagamento.application.dto.PagamentoRequestDTO;
import com.seucantinho.api.shared.domain.exception.BusinessException;
import org.springframework.stereotype.Component;

@Component
public class PagamentoValidator {

    public void validatePagamento(PagamentoRequestDTO requestDTO, Reserva reserva) {
        ValorMonetario valorTotal = reserva.getValorTotal();
        ValorMonetario metadeValor = valorTotal.calcularMetade();
        ValorMonetario valorPagamento = ValorMonetario.of(requestDTO.getValor());
        boolean possuiPagamentos = !reserva.getPagamentos().isEmpty();

        switch (requestDTO.getTipo()) {
            case TOTAL:
                if (possuiPagamentos) {
                    throw new BusinessException("Pagamento TOTAL só pode ser feito na criação da reserva");
                }
                if (!valorPagamento.isIgualA(valorTotal)) {
                    throw new BusinessException(
                        String.format("Pagamento TOTAL deve ser o valor completo: %s", valorTotal.getValorFormatado())
                    );
                }
                break;

            case SINAL:
                if (possuiPagamentos) {
                    throw new BusinessException("Pagamento SINAL só pode ser feito na criação da reserva");
                }
                if (!valorPagamento.isIgualA(metadeValor)) {
                    throw new BusinessException(
                        String.format("Pagamento SINAL deve ser 50%% do valor total: %s", metadeValor.getValorFormatado())
                    );
                }
                break;

            case QUITACAO:
                if (!possuiPagamentos) {
                    throw new BusinessException("Pagamento QUITACAO só pode ser feito após o pagamento do SINAL");
                }
                if (reserva.getPagamentos().get(0).getTipo() != TipoPagamentoEnum.SINAL) {
                    throw new BusinessException("Pagamento QUITACAO só é permitido para reservas com pagamento inicial do tipo SINAL");
                }
                if (reserva.getPagamentos().size() > 1) {
                    throw new BusinessException("Esta reserva já foi quitada");
                }
                ValorMonetario saldoRestante = reserva.calcularSaldo();
                if (!valorPagamento.isIgualA(saldoRestante)) {
                    throw new BusinessException(
                        String.format("Pagamento QUITACAO deve ser o saldo restante: %s", saldoRestante.getValorFormatado())
                    );
                }
                break;

            default:
                throw new BusinessException("Tipo de pagamento inválido");
        }
    }
}
