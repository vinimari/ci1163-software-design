package com.seucantinho.api.feature.espaco.infrastructure.mapper;

import com.seucantinho.api.feature.espaco.application.dto.EspacoRequestDTO;
import com.seucantinho.api.feature.espaco.application.dto.EspacoResponseDTO;
import com.seucantinho.api.feature.espaco.domain.Espaco;
import com.seucantinho.api.feature.espaco.domain.valueobject.Capacidade;
import com.seucantinho.api.feature.filial.domain.Filial;
import com.seucantinho.api.shared.domain.valueobject.ValorMonetario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes do EspacoMapper")
class EspacoMapperTest {

    private EspacoMapper mapper;
    private Filial filial;

    @BeforeEach
    void setUp() {
        mapper = new EspacoMapper();
        filial = Filial.builder()
                .id(1)
                .nome("Filial Centro")
                .cidade("São Paulo")
                .estado("SP")
                .build();
    }

    @Test
    @DisplayName("Deve converter EspacoRequestDTO para Espaco")
    void deveConverterRequestDTOParaEntity() {
        EspacoRequestDTO dto = EspacoRequestDTO.builder()
                .nome("Sala de Reunião")
                .descricao("Sala moderna com projetor")
                .capacidade(10)
                .precoDiaria(new BigDecimal("150.00"))
                .ativo(true)
                .urlFotoPrincipal("http://foto.com/sala.jpg")
                .filialId(1)
                .build();

        Espaco espaco = mapper.toEntity(dto, filial);

        assertNotNull(espaco);
        assertEquals(dto.getNome(), espaco.getNome());
        assertEquals(dto.getDescricao(), espaco.getDescricao());
        assertEquals(dto.getCapacidade(), espaco.getCapacidade().getQuantidade());
        assertEquals(dto.getPrecoDiaria(), espaco.getPrecoDiaria().getValor());
        assertEquals(dto.getAtivo(), espaco.getAtivo());
        assertEquals(dto.getUrlFotoPrincipal(), espaco.getUrlFotoPrincipal());
        assertEquals(filial, espaco.getFilial());
    }

    @Test
    @DisplayName("Deve converter EspacoRequestDTO sem ativo definido usando valor padrão true")
    void deveConverterRequestDTOSemAtivoUsandoPadrao() {
        EspacoRequestDTO dto = EspacoRequestDTO.builder()
                .nome("Sala de Reunião")
                .descricao("Sala moderna")
                .capacidade(10)
                .precoDiaria(new BigDecimal("150.00"))
                .ativo(null)
                .filialId(1)
                .build();

        Espaco espaco = mapper.toEntity(dto, filial);

        assertTrue(espaco.getAtivo());
    }

    @Test
    @DisplayName("Deve converter Espaco para EspacoResponseDTO")
    void deveConverterEntityParaResponseDTO() {
        Espaco espaco = Espaco.builder()
                .id(1)
                .nome("Sala de Reunião")
                .descricao("Sala moderna com projetor")
                .capacidade(Capacidade.of(10))
                .precoDiaria(ValorMonetario.of(new BigDecimal("150.00")))
                .ativo(true)
                .urlFotoPrincipal("http://foto.com/sala.jpg")
                .filial(filial)
                .build();

        EspacoResponseDTO dto = mapper.toResponseDTO(espaco);

        assertNotNull(dto);
        assertEquals(espaco.getId(), dto.getId());
        assertEquals(espaco.getNome(), dto.getNome());
        assertEquals(espaco.getDescricao(), dto.getDescricao());
        assertEquals(espaco.getCapacidade().getQuantidade(), dto.getCapacidade());
        assertEquals(espaco.getPrecoDiaria().getValor(), dto.getPrecoDiaria());
        assertEquals(espaco.getAtivo(), dto.getAtivo());
        assertEquals(espaco.getUrlFotoPrincipal(), dto.getUrlFotoPrincipal());
        assertNotNull(dto.getFilial());
        assertEquals(filial.getId(), dto.getFilial().getId());
        assertEquals(filial.getNome(), dto.getFilial().getNome());
        assertEquals(filial.getCidade(), dto.getFilial().getCidade());
        assertEquals(filial.getEstado(), dto.getFilial().getEstado());
    }

    @Test
    @DisplayName("Deve atualizar entidade existente a partir do DTO")
    void deveAtualizarEntityAPartirDoDTO() {
        Espaco espaco = Espaco.builder()
                .id(1)
                .nome("Nome Antigo")
                .descricao("Descrição Antiga")
                .capacidade(Capacidade.of(5))
                .precoDiaria(ValorMonetario.of(new BigDecimal("100.00")))
                .ativo(true)
                .urlFotoPrincipal("http://foto.com/antiga.jpg")
                .filial(filial)
                .build();

        EspacoRequestDTO dto = EspacoRequestDTO.builder()
                .nome("Nome Novo")
                .descricao("Descrição Nova")
                .capacidade(15)
                .precoDiaria(new BigDecimal("200.00"))
                .ativo(false)
                .urlFotoPrincipal("http://foto.com/nova.jpg")
                .filialId(1)
                .build();

        mapper.updateEntityFromDTO(espaco, dto, filial);

        assertEquals(dto.getNome(), espaco.getNome());
        assertEquals(dto.getDescricao(), espaco.getDescricao());
        assertEquals(dto.getCapacidade(), espaco.getCapacidade().getQuantidade());
        assertEquals(dto.getPrecoDiaria(), espaco.getPrecoDiaria().getValor());
        assertEquals(dto.getAtivo(), espaco.getAtivo());
        assertEquals(dto.getUrlFotoPrincipal(), espaco.getUrlFotoPrincipal());
    }

    @Test
    @DisplayName("Deve manter ativo existente quando DTO não especifica")
    void deveManterAtivoExistenteQuandoDTONaoEspecifica() {
        Espaco espaco = Espaco.builder()
                .id(1)
                .nome("Sala")
                .descricao("Descrição")
                .capacidade(Capacidade.of(10))
                .precoDiaria(ValorMonetario.of(new BigDecimal("150.00")))
                .ativo(true)
                .filial(filial)
                .build();

        EspacoRequestDTO dto = EspacoRequestDTO.builder()
                .nome("Sala Atualizada")
                .descricao("Descrição Atualizada")
                .capacidade(12)
                .precoDiaria(new BigDecimal("180.00"))
                .ativo(null)
                .filialId(1)
                .build();

        mapper.updateEntityFromDTO(espaco, dto, filial);

        assertTrue(espaco.getAtivo());
    }
}

