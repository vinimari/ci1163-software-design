package com.seucantinho.api.feature.pagamento.infrastructure.mapper;

import com.seucantinho.api.feature.pagamento.domain.Pagamento;
import com.seucantinho.api.feature.reserva.domain.Reserva;
import com.seucantinho.api.shared.domain.valueobject.ValorMonetario;
import com.seucantinho.api.feature.pagamento.application.dto.PagamentoRequestDTO;
import com.seucantinho.api.feature.pagamento.application.dto.PagamentoResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class PagamentoMapper {

    public Pagamento toEntity(PagamentoRequestDTO dto, Reserva reserva) {
        return Pagamento.builder()
                .valor(ValorMonetario.of(dto.getValor()))
                .tipo(dto.getTipo())
                .formaPagamento(dto.getFormaPagamento())
                .codigoTransacaoGateway(dto.getCodigoTransacaoGateway())
                .reserva(reserva)
                .build();
    }

    public PagamentoResponseDTO toResponseDTO(Pagamento pagamento) {
        return PagamentoResponseDTO.builder()
                .id(pagamento.getId())
                .dataPagamento(pagamento.getDataPagamento())
                .valor(pagamento.getValor().getValor())
                .tipo(pagamento.getTipo())
                .formaPagamento(pagamento.getFormaPagamento())
                .codigoTransacaoGateway(pagamento.getCodigoTransacaoGateway())
                .reservaId(pagamento.getReserva().getId())
                .build();
    }
}
