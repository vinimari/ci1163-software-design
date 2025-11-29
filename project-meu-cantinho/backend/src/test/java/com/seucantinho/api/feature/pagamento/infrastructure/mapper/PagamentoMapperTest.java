package com.seucantinho.api.feature.pagamento.infrastructure.mapper;

import com.seucantinho.api.feature.pagamento.application.dto.PagamentoRequestDTO;
import com.seucantinho.api.feature.pagamento.application.dto.PagamentoResponseDTO;
import com.seucantinho.api.feature.pagamento.domain.Pagamento;
import com.seucantinho.api.feature.pagamento.domain.enums.TipoPagamentoEnum;
import com.seucantinho.api.feature.reserva.domain.Reserva;
import com.seucantinho.api.shared.domain.valueobject.ValorMonetario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes do PagamentoMapper")
class PagamentoMapperTest {

    private PagamentoMapper mapper;
    private Reserva reserva;

    @BeforeEach
    void setUp() {
        mapper = new PagamentoMapper();
        reserva = Reserva.builder()
                .id(1)
                .build();
    }

    @Test
    @DisplayName("Deve converter PagamentoRequestDTO para Pagamento")
    void deveConverterRequestDTOParaEntity() {
        PagamentoRequestDTO dto = PagamentoRequestDTO.builder()
                .valor(new BigDecimal("500.00"))
                .tipo(TipoPagamentoEnum.SINAL)
                .formaPagamento("PIX")
                .codigoTransacaoGateway("TXN123456")
                .reservaId(1)
                .build();

        Pagamento pagamento = mapper.toEntity(dto, reserva);

        assertNotNull(pagamento);
        assertEquals(dto.getValor(), pagamento.getValor().getValor());
        assertEquals(dto.getTipo(), pagamento.getTipo());
        assertEquals(dto.getFormaPagamento(), pagamento.getFormaPagamento());
        assertEquals(dto.getCodigoTransacaoGateway(), pagamento.getCodigoTransacaoGateway());
        assertEquals(reserva, pagamento.getReserva());
    }

    @Test
    @DisplayName("Deve converter Pagamento para PagamentoResponseDTO")
    void deveConverterEntityParaResponseDTO() {
        Pagamento pagamento = Pagamento.builder()
                .id(1)
                .dataPagamento(LocalDateTime.now())
                .valor(ValorMonetario.of(new BigDecimal("500.00")))
                .tipo(TipoPagamentoEnum.SINAL)
                .formaPagamento("PIX")
                .codigoTransacaoGateway("TXN123456")
                .reserva(reserva)
                .build();

        PagamentoResponseDTO dto = mapper.toResponseDTO(pagamento);

        assertNotNull(dto);
        assertEquals(pagamento.getId(), dto.getId());
        assertEquals(pagamento.getDataPagamento(), dto.getDataPagamento());
        assertEquals(pagamento.getValor().getValor(), dto.getValor());
        assertEquals(pagamento.getTipo(), dto.getTipo());
        assertEquals(pagamento.getFormaPagamento(), dto.getFormaPagamento());
        assertEquals(pagamento.getCodigoTransacaoGateway(), dto.getCodigoTransacaoGateway());
        assertEquals(reserva.getId(), dto.getReservaId());
    }

    @Test
    @DisplayName("Deve converter pagamento com diferentes formas de pagamento")
    void deveConverterComDiferentesFormasDePagamento() {
        String[] formas = {"PIX", "CARTAO_CREDITO", "CARTAO_DEBITO", "DINHEIRO"};

        for (String forma : formas) {
            PagamentoRequestDTO dto = PagamentoRequestDTO.builder()
                    .valor(new BigDecimal("300.00"))
                    .tipo(TipoPagamentoEnum.SINAL)
                    .formaPagamento(forma)
                    .codigoTransacaoGateway("TXN" + forma)
                    .reservaId(1)
                    .build();

            Pagamento pagamento = mapper.toEntity(dto, reserva);

            assertNotNull(pagamento);
            assertEquals(forma, pagamento.getFormaPagamento());
        }
    }

    @Test
    @DisplayName("Deve converter pagamento com diferentes tipos")
    void deveConverterComDiferentesTiposDePagamento() {
        TipoPagamentoEnum[] tipos = TipoPagamentoEnum.values();

        for (TipoPagamentoEnum tipo : tipos) {
            PagamentoRequestDTO dto = PagamentoRequestDTO.builder()
                    .valor(new BigDecimal("300.00"))
                    .tipo(tipo)
                    .formaPagamento("PIX")
                    .codigoTransacaoGateway("TXN" + tipo.name())
                    .reservaId(1)
                    .build();

            Pagamento pagamento = mapper.toEntity(dto, reserva);

            assertNotNull(pagamento);
            assertEquals(tipo, pagamento.getTipo());
        }
    }
}

