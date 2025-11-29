package com.seucantinho.api.feature.cliente.infrastructure.mapper;

import com.seucantinho.api.feature.auth.domain.port.out.PasswordEncoderPort;
import com.seucantinho.api.feature.cliente.application.dto.ClienteRequestDTO;
import com.seucantinho.api.feature.cliente.application.dto.ClienteResponseDTO;
import com.seucantinho.api.feature.cliente.domain.Cliente;
import com.seucantinho.api.feature.reserva.domain.Reserva;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do ClienteMapper")
class ClienteMapperTest {

    @Mock
    private PasswordEncoderPort passwordEncoderPort;

    private ClienteMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ClienteMapper(passwordEncoderPort);
    }

    @Test
    @DisplayName("Deve converter ClienteRequestDTO para Cliente")
    void deveConverterRequestDTOParaEntity() {
        ClienteRequestDTO dto = ClienteRequestDTO.builder()
                .nome("João Silva")
                .email("joao@email.com")
                .senha("senha123")
                .cpf("12345678900")
                .telefone("11999999999")
                .ativo(true)
                .build();

        when(passwordEncoderPort.encode(anyString())).thenReturn("senhaHasheada");

        Cliente cliente = mapper.toEntity(dto);

        assertNotNull(cliente);
        assertEquals(dto.getNome(), cliente.getNome());
        assertEquals(dto.getEmail(), cliente.getEmail());
        assertEquals("senhaHasheada", cliente.getSenhaHash());
        assertEquals(dto.getCpf(), cliente.getCpf());
        assertEquals(dto.getTelefone(), cliente.getTelefone());
        assertEquals(dto.getAtivo(), cliente.getAtivo());
    }

    @Test
    @DisplayName("Deve usar ativo true como padrão quando não especificado")
    void deveUsarAtivoTrueComoPadrao() {
        ClienteRequestDTO dto = ClienteRequestDTO.builder()
                .nome("João Silva")
                .email("joao@email.com")
                .senha("senha123")
                .cpf("12345678900")
                .telefone("11999999999")
                .ativo(null)
                .build();

        when(passwordEncoderPort.encode(anyString())).thenReturn("senhaHasheada");

        Cliente cliente = mapper.toEntity(dto);

        assertTrue(cliente.getAtivo());
    }

    @Test
    @DisplayName("Deve converter Cliente para ClienteResponseDTO")
    void deveConverterEntityParaResponseDTO() {
        Cliente cliente = Cliente.builder()
                .id(1)
                .nome("João Silva")
                .email("joao@email.com")
                .cpf("12345678900")
                .telefone("11999999999")
                .ativo(true)
                .dataCadastro(LocalDateTime.now())
                .reservas(new ArrayList<>())
                .build();

        ClienteResponseDTO dto = mapper.toResponseDTO(cliente);

        assertNotNull(dto);
        assertEquals(cliente.getId(), dto.getId());
        assertEquals(cliente.getNome(), dto.getNome());
        assertEquals(cliente.getEmail(), dto.getEmail());
        assertEquals(cliente.getCpf(), dto.getCpf());
        assertEquals(cliente.getTelefone(), dto.getTelefone());
        assertEquals(cliente.getAtivo(), dto.getAtivo());
        assertEquals(0, dto.getQuantidadeReservas());
    }

    @Test
    @DisplayName("Deve converter Cliente com reservas para ClienteResponseDTO")
    void deveConverterClienteComReservasParaResponseDTO() {
        List<Reserva> reservas = new ArrayList<>();
        reservas.add(new Reserva());
        reservas.add(new Reserva());
        reservas.add(new Reserva());

        Cliente cliente = Cliente.builder()
                .id(1)
                .nome("João Silva")
                .email("joao@email.com")
                .cpf("12345678900")
                .telefone("11999999999")
                .ativo(true)
                .dataCadastro(LocalDateTime.now())
                .reservas(reservas)
                .build();

        ClienteResponseDTO dto = mapper.toResponseDTO(cliente);

        assertEquals(3, dto.getQuantidadeReservas());
    }

    @Test
    @DisplayName("Deve atualizar entidade existente a partir do DTO")
    void deveAtualizarEntityAPartirDoDTO() {
        Cliente cliente = Cliente.builder()
                .id(1)
                .nome("Nome Antigo")
                .email("antigo@email.com")
                .senhaHash("senhaAntigaHasheada")
                .cpf("11111111111")
                .telefone("11888888888")
                .ativo(true)
                .build();

        ClienteRequestDTO dto = ClienteRequestDTO.builder()
                .nome("Nome Novo")
                .email("novo@email.com")
                .senha("novaSenha123")
                .cpf("22222222222")
                .telefone("11777777777")
                .ativo(false)
                .build();

        when(passwordEncoderPort.encode("novaSenha123")).thenReturn("novaSenhaHasheada");

        mapper.updateEntityFromDTO(cliente, dto);

        assertEquals(dto.getNome(), cliente.getNome());
        assertEquals(dto.getEmail(), cliente.getEmail());
        assertEquals("novaSenhaHasheada", cliente.getSenhaHash());
        assertEquals(dto.getCpf(), cliente.getCpf());
        assertEquals(dto.getTelefone(), cliente.getTelefone());
        assertEquals(dto.getAtivo(), cliente.getAtivo());
    }

    @Test
    @DisplayName("Não deve atualizar senha quando não fornecida")
    void naoDeveAtualizarSenhaQuandoNaoFornecida() {
        Cliente cliente = Cliente.builder()
                .id(1)
                .nome("Nome Antigo")
                .email("antigo@email.com")
                .senhaHash("senhaAntigaHasheada")
                .cpf("11111111111")
                .telefone("11888888888")
                .ativo(true)
                .build();

        ClienteRequestDTO dto = ClienteRequestDTO.builder()
                .nome("Nome Novo")
                .email("novo@email.com")
                .senha(null)
                .cpf("22222222222")
                .telefone("11777777777")
                .ativo(true)
                .build();

        mapper.updateEntityFromDTO(cliente, dto);

        assertEquals("senhaAntigaHasheada", cliente.getSenhaHash());
    }

    @Test
    @DisplayName("Não deve atualizar senha quando vazia")
    void naoDeveAtualizarSenhaQuandoVazia() {
        Cliente cliente = Cliente.builder()
                .id(1)
                .nome("Nome Antigo")
                .email("antigo@email.com")
                .senhaHash("senhaAntigaHasheada")
                .cpf("11111111111")
                .telefone("11888888888")
                .ativo(true)
                .build();

        ClienteRequestDTO dto = ClienteRequestDTO.builder()
                .nome("Nome Novo")
                .email("novo@email.com")
                .senha("")
                .cpf("22222222222")
                .telefone("11777777777")
                .ativo(true)
                .build();

        mapper.updateEntityFromDTO(cliente, dto);

        assertEquals("senhaAntigaHasheada", cliente.getSenhaHash());
    }

    @Test
    @DisplayName("Deve manter ativo existente quando DTO não especifica")
    void deveManterAtivoExistenteQuandoDTONaoEspecifica() {
        Cliente cliente = Cliente.builder()
                .id(1)
                .nome("João Silva")
                .email("joao@email.com")
                .senhaHash("senhaHasheada")
                .cpf("12345678900")
                .telefone("11999999999")
                .ativo(true)
                .build();

        ClienteRequestDTO dto = ClienteRequestDTO.builder()
                .nome("João Silva Atualizado")
                .email("joao.novo@email.com")
                .cpf("12345678900")
                .telefone("11999999999")
                .ativo(null)
                .build();

        mapper.updateEntityFromDTO(cliente, dto);

        assertTrue(cliente.getAtivo());
    }
}

