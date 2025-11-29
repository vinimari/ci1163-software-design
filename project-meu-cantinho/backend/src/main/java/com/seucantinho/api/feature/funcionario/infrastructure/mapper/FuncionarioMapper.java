package com.seucantinho.api.feature.funcionario.infrastructure.mapper;

import com.seucantinho.api.feature.funcionario.application.dto.FuncionarioRequestDTO;
import com.seucantinho.api.feature.funcionario.application.dto.FuncionarioResponseDTO;
import com.seucantinho.api.feature.funcionario.domain.Funcionario;
import com.seucantinho.api.feature.filial.domain.Filial;
import com.seucantinho.api.feature.filial.infrastructure.mapper.FilialMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FuncionarioMapper {

    private final PasswordEncoder passwordEncoder;
    private final FilialMapper filialMapper;

    public Funcionario toEntity(FuncionarioRequestDTO dto) {
        Funcionario funcionario = new Funcionario();
        funcionario.setNome(dto.getNome());
        funcionario.setEmail(dto.getEmail());
        funcionario.setSenhaHash(passwordEncoder.encode(dto.getSenha()));
        funcionario.setCpf(dto.getCpf());
        funcionario.setTelefone(dto.getTelefone());
        funcionario.setAtivo(dto.getAtivo() != null ? dto.getAtivo() : true);
        funcionario.setMatricula(dto.getMatricula());

        // Filial será setada no service após validação
        return funcionario;
    }

    public FuncionarioResponseDTO toResponseDTO(Funcionario funcionario) {
        return FuncionarioResponseDTO.builder()
                .id(funcionario.getId())
                .nome(funcionario.getNome())
                .email(funcionario.getEmail())
                .cpf(funcionario.getCpf())
                .telefone(funcionario.getTelefone())
                .ativo(funcionario.getAtivo())
                .dataCadastro(funcionario.getDataCadastro())
                .matricula(funcionario.getMatricula())
                .filial(funcionario.getFilial() != null ? filialMapper.toResponseDTO(funcionario.getFilial()) : null)
                .build();
    }

    public void updateEntityFromDTO(Funcionario funcionario, FuncionarioRequestDTO dto) {
        funcionario.setNome(dto.getNome());
        funcionario.setEmail(dto.getEmail());

        // Só atualiza senha se foi fornecida
        if (dto.getSenha() != null && !dto.getSenha().isBlank()) {
            funcionario.setSenhaHash(passwordEncoder.encode(dto.getSenha()));
        }

        funcionario.setCpf(dto.getCpf());
        funcionario.setTelefone(dto.getTelefone());

        if (dto.getAtivo() != null) {
            funcionario.setAtivo(dto.getAtivo());
        }

        funcionario.setMatricula(dto.getMatricula());
        // Filial será atualizada no service após validação
    }
}
