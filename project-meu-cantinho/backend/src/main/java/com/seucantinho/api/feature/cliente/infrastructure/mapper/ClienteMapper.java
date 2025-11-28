package com.seucantinho.api.feature.cliente.infrastructure.mapper;

import com.seucantinho.api.feature.cliente.domain.Cliente;
import com.seucantinho.api.feature.cliente.application.dto.ClienteRequestDTO;
import com.seucantinho.api.feature.cliente.application.dto.ClienteResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ClienteMapper {

    private final PasswordEncoder passwordEncoder;

    public Cliente toEntity(ClienteRequestDTO dto) {
        return Cliente.builder()
                .nome(dto.getNome())
                .email(dto.getEmail())
                .senhaHash(passwordEncoder.encode(dto.getSenha()))
                .cpf(dto.getCpf())
                .telefone(dto.getTelefone())
                .ativo(dto.getAtivo() != null ? dto.getAtivo() : true)
                .build();
    }

    public ClienteResponseDTO toResponseDTO(Cliente cliente) {
        return ClienteResponseDTO.builder()
                .id(cliente.getId())
                .nome(cliente.getNome())
                .email(cliente.getEmail())
                .cpf(cliente.getCpf())
                .telefone(cliente.getTelefone())
                .ativo(cliente.getAtivo())
                .dataCadastro(cliente.getDataCadastro())
                .quantidadeReservas(cliente.getReservas() != null ? cliente.getReservas().size() : 0)
                .build();
    }

    public void updateEntityFromDTO(Cliente cliente, ClienteRequestDTO dto) {
        cliente.setNome(dto.getNome());
        cliente.setEmail(dto.getEmail());
        if (dto.getSenha() != null && !dto.getSenha().isEmpty()) {
            cliente.setSenhaHash(passwordEncoder.encode(dto.getSenha()));
        }
        cliente.setCpf(dto.getCpf());
        cliente.setTelefone(dto.getTelefone());
        cliente.setAtivo(dto.getAtivo() != null ? dto.getAtivo() : cliente.getAtivo());
    }
}
