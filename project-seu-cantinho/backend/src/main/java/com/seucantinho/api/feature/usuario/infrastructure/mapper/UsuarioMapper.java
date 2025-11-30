package com.seucantinho.api.feature.usuario.infrastructure.mapper;

import com.seucantinho.api.feature.administrador.domain.Administrador;
import com.seucantinho.api.feature.cliente.domain.Cliente;
import com.seucantinho.api.feature.funcionario.domain.Funcionario;
import com.seucantinho.api.feature.usuario.domain.Usuario;
import com.seucantinho.api.feature.filial.application.dto.FilialResponseDTO;
import com.seucantinho.api.feature.administrador.application.dto.AdministradorResponseDTO;
import com.seucantinho.api.feature.cliente.application.dto.ClienteResponseDTO;
import com.seucantinho.api.feature.funcionario.application.dto.FuncionarioResponseDTO;
import com.seucantinho.api.feature.usuario.application.dto.UsuarioResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapper {

    public UsuarioResponseDTO toResponseDTO(Usuario usuario) {
        if (usuario == null) {
            return null;
        }

        if (usuario instanceof Cliente cliente) {
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

        if (usuario instanceof Funcionario funcionario) {
            FilialResponseDTO filialDTO = null;
            if (funcionario.getFilial() != null) {
                filialDTO = FilialResponseDTO.builder()
                        .id(funcionario.getFilial().getId())
                        .nome(funcionario.getFilial().getNome())
                        .cidade(funcionario.getFilial().getCidade())
                        .estado(funcionario.getFilial().getEstado())
                        .build();
            }

            return FuncionarioResponseDTO.builder()
                    .id(funcionario.getId())
                    .nome(funcionario.getNome())
                    .email(funcionario.getEmail())
                    .cpf(funcionario.getCpf())
                    .telefone(funcionario.getTelefone())
                    .ativo(funcionario.getAtivo())
                    .dataCadastro(funcionario.getDataCadastro())
                    .matricula(funcionario.getMatricula())
                    .filial(filialDTO)
                    .build();
        }

        if (usuario instanceof Administrador administrador) {
            return AdministradorResponseDTO.builder()
                    .id(administrador.getId())
                    .nome(administrador.getNome())
                    .email(administrador.getEmail())
                    .cpf(administrador.getCpf())
                    .telefone(administrador.getTelefone())
                    .ativo(administrador.getAtivo())
                    .dataCadastro(administrador.getDataCadastro())
                    .build();
        }

        throw new IllegalArgumentException("Tipo de usuário não suportado: " + usuario.getClass().getSimpleName());
    }
}
