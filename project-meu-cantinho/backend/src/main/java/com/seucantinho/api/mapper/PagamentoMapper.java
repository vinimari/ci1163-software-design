package com.seucantinho.api.mapper;

import com.seucantinho.api.domain.entity.Pagamento;
import com.seucantinho.api.domain.entity.Reserva;
import com.seucantinho.api.dto.pagamento.PagamentoRequestDTO;
import com.seucantinho.api.dto.pagamento.PagamentoResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class PagamentoMapper {

    public Pagamento toEntity(PagamentoRequestDTO dto, Reserva reserva) {
        return Pagamento.builder()
                .valor(dto.getValor())
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
                .valor(pagamento.getValor())
                .tipo(pagamento.getTipo())
                .formaPagamento(pagamento.getFormaPagamento())
                .codigoTransacaoGateway(pagamento.getCodigoTransacaoGateway())
                .reservaId(pagamento.getReserva().getId())
                .build();
    }
}
