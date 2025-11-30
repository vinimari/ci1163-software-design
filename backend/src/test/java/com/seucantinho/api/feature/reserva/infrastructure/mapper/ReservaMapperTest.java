package com.seucantinho.api.feature.reserva.infrastructure.mapper;

import com.seucantinho.api.feature.cliente.domain.Cliente;
import com.seucantinho.api.feature.espaco.domain.Espaco;
import com.seucantinho.api.feature.espaco.domain.valueobject.Capacidade;
import com.seucantinho.api.feature.espaco.infrastructure.mapper.EspacoMapper;
import com.seucantinho.api.feature.filial.domain.Filial;
import com.seucantinho.api.feature.reserva.application.dto.ReservaRequestDTO;
import com.seucantinho.api.feature.reserva.application.dto.ReservaResponseDTO;
import com.seucantinho.api.feature.reserva.domain.Reserva;
import com.seucantinho.api.feature.reserva.domain.enums.StatusReservaEnum;
import com.seucantinho.api.feature.reserva.domain.valueobject.DataEvento;
import com.seucantinho.api.feature.usuario.infrastructure.mapper.UsuarioMapper;
import com.seucantinho.api.shared.domain.valueobject.ValorMonetario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do ReservaMapper")
class ReservaMapperTest {

    @Mock
    private EspacoMapper espacoMapper;

    @Mock
    private UsuarioMapper usuarioMapper;

    private ReservaMapper mapper;
    private Cliente cliente;
    private Espaco espaco;
    private Filial filial;

    @BeforeEach
    void setUp() {
        mapper = new ReservaMapper(espacoMapper, usuarioMapper);

        filial = Filial.builder()
                .id(1)
                .nome("Filial Centro")
                .cidade("São Paulo")
                .estado("SP")
                .build();

        espaco = Espaco.builder()
                .id(1)
                .nome("Sala de Reunião")
                .descricao("Sala moderna")
                .capacidade(Capacidade.of(10))
                .precoDiaria(ValorMonetario.of(new BigDecimal("150.00")))
                .ativo(true)
                .filial(filial)
                .build();

        cliente = Cliente.builder()
                .id(1)
                .nome("João Silva")
                .email("joao@email.com")
                .cpf("12345678900")
                .telefone("11999999999")
                .ativo(true)
                .build();
    }

    @Test
    @DisplayName("Deve converter ReservaRequestDTO para Reserva")
    void deveConverterRequestDTOParaEntity() {
        ReservaRequestDTO dto = ReservaRequestDTO.builder()
                .dataEvento(LocalDate.of(2025, 12, 25))
                .valorTotal(new BigDecimal("500.00"))
                .observacoes("Evento corporativo")
                .status(StatusReservaEnum.CONFIRMADA)
                .usuarioId(1)
                .espacoId(1)
                .build();

        Reserva reserva = mapper.toEntity(dto, cliente, espaco);

        assertNotNull(reserva);
        assertEquals(dto.getDataEvento(), reserva.getDataEvento().getData());
        assertEquals(dto.getValorTotal(), reserva.getValorTotal().getValor());
        assertEquals(dto.getObservacoes(), reserva.getObservacoes());
        assertEquals(dto.getStatus(), reserva.getStatus());
        assertEquals(cliente, reserva.getUsuario());
        assertEquals(espaco, reserva.getEspaco());
    }

    @Test
    @DisplayName("Deve usar status AGUARDANDO_SINAL como padrão quando não especificado")
    void deveUsarStatusPadraoQuandoNaoEspecificado() {
        ReservaRequestDTO dto = ReservaRequestDTO.builder()
                .dataEvento(LocalDate.of(2025, 12, 25))
                .valorTotal(new BigDecimal("500.00"))
                .observacoes("Evento corporativo")
                .status(null)
                .usuarioId(1)
                .espacoId(1)
                .build();

        Reserva reserva = mapper.toEntity(dto, cliente, espaco);

        assertEquals(StatusReservaEnum.AGUARDANDO_SINAL, reserva.getStatus());
    }

    @Test
    @DisplayName("Deve converter Reserva para ReservaResponseDTO")
    void deveConverterEntityParaResponseDTO() {
        Reserva reserva = Reserva.builder()
                .id(1)
                .dataEvento(DataEvento.of(LocalDate.of(2025, 12, 25)))
                .valorTotal(ValorMonetario.of(new BigDecimal("500.00")))
                .observacoes("Evento corporativo")
                .status(StatusReservaEnum.CONFIRMADA)
                .usuario(cliente)
                .espaco(espaco)
                .build();

        when(usuarioMapper.toResponseDTO(any())).thenReturn(null);
        when(espacoMapper.toResponseDTO(any())).thenReturn(null);

        ReservaResponseDTO dto = mapper.toResponseDTO(reserva);

        assertNotNull(dto);
        assertEquals(reserva.getId(), dto.getId());
        assertEquals(reserva.getDataEvento().getData(), dto.getDataEvento());
        assertEquals(reserva.getValorTotal().getValor(), dto.getValorTotal());
        assertEquals(reserva.getObservacoes(), dto.getObservacoes());
        assertEquals(reserva.getStatus(), dto.getStatus());
        assertEquals(0, dto.getTotalPago().compareTo(BigDecimal.ZERO));
        assertEquals(reserva.getValorTotal().getValor(), dto.getSaldo());
    }
}

