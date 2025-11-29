package com.seucantinho.api.feature.filial.infrastructure.mapper;

import com.seucantinho.api.feature.espaco.domain.Espaco;
import com.seucantinho.api.feature.filial.application.dto.FilialRequestDTO;
import com.seucantinho.api.feature.filial.application.dto.FilialResponseDTO;
import com.seucantinho.api.feature.filial.domain.Filial;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes do FilialMapper")
class FilialMapperTest {

    private FilialMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new FilialMapper();
    }

    @Test
    @DisplayName("Deve converter FilialRequestDTO para Filial")
    void deveConverterRequestDTOParaEntity() {
        FilialRequestDTO dto = FilialRequestDTO.builder()
                .nome("Filial Centro")
                .cidade("São Paulo")
                .estado("SP")
                .endereco("Rua Central, 123")
                .telefone("11999999999")
                .build();

        Filial filial = mapper.toEntity(dto);

        assertNotNull(filial);
        assertEquals(dto.getNome(), filial.getNome());
        assertEquals(dto.getCidade(), filial.getCidade());
        assertEquals(dto.getEstado(), filial.getEstado());
        assertEquals(dto.getEndereco(), filial.getEndereco());
        assertEquals(dto.getTelefone(), filial.getTelefone());
    }

    @Test
    @DisplayName("Deve converter Filial para FilialResponseDTO")
    void deveConverterEntityParaResponseDTO() {
        Filial filial = Filial.builder()
                .id(1)
                .nome("Filial Centro")
                .cidade("São Paulo")
                .estado("SP")
                .endereco("Rua Central, 123")
                .telefone("11999999999")
                .dataCadastro(LocalDateTime.now())
                .espacos(new ArrayList<>())
                .build();

        FilialResponseDTO dto = mapper.toResponseDTO(filial);

        assertNotNull(dto);
        assertEquals(filial.getId(), dto.getId());
        assertEquals(filial.getNome(), dto.getNome());
        assertEquals(filial.getCidade(), dto.getCidade());
        assertEquals(filial.getEstado(), dto.getEstado());
        assertEquals(filial.getEndereco(), dto.getEndereco());
        assertEquals(filial.getTelefone(), dto.getTelefone());
        assertEquals(0, dto.getQuantidadeEspacos());
    }

    @Test
    @DisplayName("Deve converter Filial com espaços para FilialResponseDTO")
    void deveConverterFilialComEspacosParaResponseDTO() {
        List<Espaco> espacos = new ArrayList<>();
        espacos.add(new Espaco());
        espacos.add(new Espaco());
        espacos.add(new Espaco());

        Filial filial = Filial.builder()
                .id(1)
                .nome("Filial Centro")
                .cidade("São Paulo")
                .estado("SP")
                .endereco("Rua Central, 123")
                .telefone("11999999999")
                .dataCadastro(LocalDateTime.now())
                .espacos(espacos)
                .build();

        FilialResponseDTO dto = mapper.toResponseDTO(filial);

        assertEquals(3, dto.getQuantidadeEspacos());
    }

    @Test
    @DisplayName("Deve atualizar entidade existente a partir do DTO")
    void deveAtualizarEntityAPartirDoDTO() {
        Filial filial = Filial.builder()
                .id(1)
                .nome("Nome Antigo")
                .cidade("Cidade Antiga")
                .estado("AA")
                .endereco("Endereço Antigo")
                .telefone("11888888888")
                .build();

        FilialRequestDTO dto = FilialRequestDTO.builder()
                .nome("Nome Novo")
                .cidade("Cidade Nova")
                .estado("NN")
                .endereco("Endereço Novo")
                .telefone("11777777777")
                .build();

        mapper.updateEntityFromDTO(filial, dto);

        assertEquals(dto.getNome(), filial.getNome());
        assertEquals(dto.getCidade(), filial.getCidade());
        assertEquals(dto.getEstado(), filial.getEstado());
        assertEquals(dto.getEndereco(), filial.getEndereco());
        assertEquals(dto.getTelefone(), filial.getTelefone());
    }
}

