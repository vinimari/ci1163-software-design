package com.seucantinho.api.feature.usuario.infrastructure.mapper;

import com.seucantinho.api.feature.administrador.application.dto.AdministradorResponseDTO;
import com.seucantinho.api.feature.administrador.domain.Administrador;
import com.seucantinho.api.feature.cliente.application.dto.ClienteResponseDTO;
import com.seucantinho.api.feature.cliente.domain.Cliente;
import com.seucantinho.api.feature.filial.domain.Filial;
import com.seucantinho.api.feature.funcionario.application.dto.FuncionarioResponseDTO;
import com.seucantinho.api.feature.funcionario.domain.Funcionario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes do UsuarioMapper")
class UsuarioMapperTest {

    private UsuarioMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new UsuarioMapper();
    }

    @Test
    @DisplayName("Deve retornar null quando usuário for null")
    void deveRetornarNullQuandoUsuarioForNull() {
        var resultado = mapper.toResponseDTO(null);
        assertNull(resultado);
    }

    @Test
    @DisplayName("Deve mapear Cliente para ClienteResponseDTO")
    void deveMapearClienteParaResponseDTO() {
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

        var resultado = mapper.toResponseDTO(cliente);

        assertNotNull(resultado);
        assertInstanceOf(ClienteResponseDTO.class, resultado);
        ClienteResponseDTO clienteDTO = (ClienteResponseDTO) resultado;
        assertEquals(cliente.getId(), clienteDTO.getId());
        assertEquals(cliente.getNome(), clienteDTO.getNome());
        assertEquals(cliente.getEmail(), clienteDTO.getEmail());
        assertEquals(cliente.getCpf(), clienteDTO.getCpf());
        assertEquals(cliente.getTelefone(), clienteDTO.getTelefone());
        assertEquals(cliente.getAtivo(), clienteDTO.getAtivo());
        assertEquals(0, clienteDTO.getQuantidadeReservas());
    }

    @Test
    @DisplayName("Deve mapear Cliente com reservas para ClienteResponseDTO")
    void deveMapearClienteComReservasParaResponseDTO() {
        Cliente cliente = Cliente.builder()
                .id(1)
                .nome("João Silva")
                .email("joao@email.com")
                .cpf("12345678900")
                .telefone("11999999999")
                .ativo(true)
                .dataCadastro(LocalDateTime.now())
                .build();
        cliente.setReservas(new ArrayList<>());
        cliente.getReservas().add(new com.seucantinho.api.feature.reserva.domain.Reserva());
        cliente.getReservas().add(new com.seucantinho.api.feature.reserva.domain.Reserva());

        var resultado = mapper.toResponseDTO(cliente);

        assertNotNull(resultado);
        ClienteResponseDTO clienteDTO = (ClienteResponseDTO) resultado;
        assertEquals(2, clienteDTO.getQuantidadeReservas());
    }

    @Test
    @DisplayName("Deve mapear Funcionario para FuncionarioResponseDTO")
    void deveMapearFuncionarioParaResponseDTO() {
        Filial filial = Filial.builder()
                .id(1)
                .nome("Filial Centro")
                .cidade("São Paulo")
                .estado("SP")
                .build();

        Funcionario funcionario = new Funcionario();
        funcionario.setId(2);
        funcionario.setNome("Maria Santos");
        funcionario.setEmail("maria@email.com");
        funcionario.setCpf("98765432100");
        funcionario.setTelefone("11988888888");
        funcionario.setAtivo(true);
        funcionario.setDataCadastro(LocalDateTime.now());
        funcionario.setMatricula("MAT001");
        funcionario.setFilial(filial);

        var resultado = mapper.toResponseDTO(funcionario);

        assertNotNull(resultado);
        assertInstanceOf(FuncionarioResponseDTO.class, resultado);
        FuncionarioResponseDTO funcionarioDTO = (FuncionarioResponseDTO) resultado;
        assertEquals(funcionario.getId(), funcionarioDTO.getId());
        assertEquals(funcionario.getNome(), funcionarioDTO.getNome());
        assertEquals(funcionario.getEmail(), funcionarioDTO.getEmail());
        assertEquals(funcionario.getCpf(), funcionarioDTO.getCpf());
        assertEquals(funcionario.getTelefone(), funcionarioDTO.getTelefone());
        assertEquals(funcionario.getAtivo(), funcionarioDTO.getAtivo());
        assertEquals(funcionario.getMatricula(), funcionarioDTO.getMatricula());
        assertNotNull(funcionarioDTO.getFilial());
        assertEquals(filial.getId(), funcionarioDTO.getFilial().getId());
        assertEquals(filial.getNome(), funcionarioDTO.getFilial().getNome());
    }

    @Test
    @DisplayName("Deve mapear Funcionario sem filial para FuncionarioResponseDTO")
    void deveMapearFuncionarioSemFilialParaResponseDTO() {
        Funcionario funcionario = new Funcionario();
        funcionario.setId(2);
        funcionario.setNome("Maria Santos");
        funcionario.setEmail("maria@email.com");
        funcionario.setCpf("98765432100");
        funcionario.setTelefone("11988888888");
        funcionario.setAtivo(true);
        funcionario.setDataCadastro(LocalDateTime.now());
        funcionario.setMatricula("MAT001");
        funcionario.setFilial(null);

        var resultado = mapper.toResponseDTO(funcionario);

        assertNotNull(resultado);
        FuncionarioResponseDTO funcionarioDTO = (FuncionarioResponseDTO) resultado;
        assertNull(funcionarioDTO.getFilial());
    }

    @Test
    @DisplayName("Deve mapear Administrador para AdministradorResponseDTO")
    void deveMapearAdministradorParaResponseDTO() {
        Administrador admin = new Administrador();
        admin.setId(3);
        admin.setNome("Carlos Admin");
        admin.setEmail("carlos@email.com");
        admin.setCpf("11122233344");
        admin.setTelefone("11977777777");
        admin.setAtivo(true);
        admin.setDataCadastro(LocalDateTime.now());

        var resultado = mapper.toResponseDTO(admin);

        assertNotNull(resultado);
        assertInstanceOf(AdministradorResponseDTO.class, resultado);
        AdministradorResponseDTO adminDTO = (AdministradorResponseDTO) resultado;
        assertEquals(admin.getId(), adminDTO.getId());
        assertEquals(admin.getNome(), adminDTO.getNome());
        assertEquals(admin.getEmail(), adminDTO.getEmail());
        assertEquals(admin.getCpf(), adminDTO.getCpf());
        assertEquals(admin.getTelefone(), adminDTO.getTelefone());
        assertEquals(admin.getAtivo(), adminDTO.getAtivo());
    }
}

