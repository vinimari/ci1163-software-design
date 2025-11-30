package com.seucantinho.api.feature.funcionario.domain.port.in;

import com.seucantinho.api.feature.funcionario.application.dto.FuncionarioRequestDTO;
import com.seucantinho.api.feature.funcionario.application.dto.FuncionarioResponseDTO;

import java.util.List;

public interface FuncionarioServicePort {

    List<FuncionarioResponseDTO> findAll();

    List<FuncionarioResponseDTO> findByFilialId(Integer filialId);

    FuncionarioResponseDTO findById(Integer id);

    FuncionarioResponseDTO create(FuncionarioRequestDTO requestDTO);

    FuncionarioResponseDTO update(Integer id, FuncionarioRequestDTO requestDTO);

    void delete(Integer id);

    FuncionarioResponseDTO toggleAtivo(Integer id, Boolean ativo);

    FuncionarioResponseDTO trocarFilial(Integer id, Integer novaFilialId);
}
