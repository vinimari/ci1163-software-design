package com.seucantinho.api.dto.reserva;

import com.seucantinho.api.domain.enums.StatusReservaEnum;
import com.seucantinho.api.dto.espaco.EspacoResponseDTO;
import com.seucantinho.api.dto.usuario.ClienteResponseDTO;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ReservaResponseDTOTest {

    @Test
    void shouldCreateReservaResponseDTOWithBuilder() {
        // Given
        ClienteResponseDTO clienteDTO = ClienteResponseDTO.builder()
                .id(1)
                .nome("João Silva")
                .build();

        EspacoResponseDTO espacoDTO = EspacoResponseDTO.builder()
                .id(1)
                .nome("Salão Principal")
                .build();

        LocalDateTime now = LocalDateTime.now();
        LocalDate eventDate = LocalDate.now().plusDays(30);

        // When
        ReservaResponseDTO dto = ReservaResponseDTO.builder()
                .id(1)
                .dataCriacao(now)
                .dataEvento(eventDate)
                .valorTotal(new BigDecimal("500.00"))
                .observacoes("Observação teste")
                .status(StatusReservaEnum.AGUARDANDO_SINAL)
                .usuario(clienteDTO)
                .espaco(espacoDTO)
                .totalPago(new BigDecimal("200.00"))
                .saldo(new BigDecimal("300.00"))
                .build();

        // Then
        assertNotNull(dto);
        assertEquals(1, dto.getId());
        assertEquals(now, dto.getDataCriacao());
        assertEquals(eventDate, dto.getDataEvento());
        assertEquals(new BigDecimal("500.00"), dto.getValorTotal());
        assertEquals("Observação teste", dto.getObservacoes());
        assertEquals(StatusReservaEnum.AGUARDANDO_SINAL, dto.getStatus());
        assertNotNull(dto.getUsuario());
        assertEquals(1, dto.getUsuario().getId());
        assertNotNull(dto.getEspaco());
        assertEquals(1, dto.getEspaco().getId());
        assertEquals(new BigDecimal("200.00"), dto.getTotalPago());
        assertEquals(new BigDecimal("300.00"), dto.getSaldo());
    }

    @Test
    void shouldUseGettersAndSetters() {
        // Given
        ReservaResponseDTO dto = new ReservaResponseDTO();
        ClienteResponseDTO clienteDTO = new ClienteResponseDTO();
        EspacoResponseDTO espacoDTO = new EspacoResponseDTO();
        LocalDateTime now = LocalDateTime.now();
        LocalDate eventDate = LocalDate.now().plusDays(30);

        // When
        dto.setId(1);
        dto.setDataCriacao(now);
        dto.setDataEvento(eventDate);
        dto.setValorTotal(new BigDecimal("500.00"));
        dto.setObservacoes("Observação teste");
        dto.setStatus(StatusReservaEnum.CONFIRMADA);
        dto.setUsuario(clienteDTO);
        dto.setEspaco(espacoDTO);
        dto.setTotalPago(new BigDecimal("500.00"));
        dto.setSaldo(BigDecimal.ZERO);

        // Then
        assertEquals(1, dto.getId());
        assertEquals(now, dto.getDataCriacao());
        assertEquals(eventDate, dto.getDataEvento());
        assertEquals(new BigDecimal("500.00"), dto.getValorTotal());
        assertEquals("Observação teste", dto.getObservacoes());
        assertEquals(StatusReservaEnum.CONFIRMADA, dto.getStatus());
        assertNotNull(dto.getUsuario());
        assertNotNull(dto.getEspaco());
        assertEquals(new BigDecimal("500.00"), dto.getTotalPago());
        assertEquals(BigDecimal.ZERO, dto.getSaldo());
    }

    @Test
    void shouldUseAllArgsConstructor() {
        // Given
        ClienteResponseDTO clienteDTO = new ClienteResponseDTO();
        EspacoResponseDTO espacoDTO = new EspacoResponseDTO();
        LocalDateTime now = LocalDateTime.now();
        LocalDate eventDate = LocalDate.now().plusDays(30);

        // When
        ReservaResponseDTO dto = new ReservaResponseDTO(
                1,
                now,
                eventDate,
                new BigDecimal("500.00"),
                "Observação",
                StatusReservaEnum.QUITADA,
                clienteDTO,
                espacoDTO,
                new BigDecimal("500.00"),
                BigDecimal.ZERO
        );

        // Then
        assertNotNull(dto);
        assertEquals(1, dto.getId());
        assertEquals(StatusReservaEnum.QUITADA, dto.getStatus());
        assertEquals(BigDecimal.ZERO, dto.getSaldo());
    }
}
