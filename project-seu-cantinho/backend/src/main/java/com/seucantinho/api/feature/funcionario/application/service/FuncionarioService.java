package com.seucantinho.api.feature.funcionario.application.service;

import com.seucantinho.api.feature.funcionario.domain.Funcionario;
import com.seucantinho.api.feature.funcionario.application.dto.FuncionarioRequestDTO;
import com.seucantinho.api.feature.funcionario.application.dto.FuncionarioResponseDTO;
import com.seucantinho.api.feature.funcionario.domain.port.in.FuncionarioServicePort;
import com.seucantinho.api.feature.funcionario.domain.port.out.FuncionarioRepositoryPort;
import com.seucantinho.api.feature.funcionario.domain.service.FuncionarioUniquenessService;
import com.seucantinho.api.feature.funcionario.infrastructure.mapper.FuncionarioMapper;
import com.seucantinho.api.feature.filial.domain.Filial;
import com.seucantinho.api.feature.filial.domain.port.out.FilialRepositoryPort;
import com.seucantinho.api.shared.domain.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FuncionarioService implements FuncionarioServicePort {

    private final FuncionarioRepositoryPort funcionarioRepositoryPort;
    private final FilialRepositoryPort filialRepositoryPort;
    private final FuncionarioMapper funcionarioMapper;
    private final FuncionarioUniquenessService funcionarioUniquenessService;

    @Override
    @Transactional(readOnly = true)
    public List<FuncionarioResponseDTO> findAll() {
        return funcionarioRepositoryPort.findAll().stream()
                .map(funcionarioMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<FuncionarioResponseDTO> findByFilialId(Integer filialId) {
        return funcionarioRepositoryPort.findByFilialId(filialId).stream()
                .map(funcionarioMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public FuncionarioResponseDTO findById(Integer id) {
        Funcionario funcionario = findFuncionarioById(id);
        return funcionarioMapper.toResponseDTO(funcionario);
    }

    @Override
    @Transactional
    public FuncionarioResponseDTO create(FuncionarioRequestDTO requestDTO) {
        // Validações de unicidade
        funcionarioUniquenessService.validarEmailUnico(requestDTO.getEmail());
        funcionarioUniquenessService.validarMatriculaUnica(requestDTO.getMatricula());

        // Valida filial
        Filial filial = filialRepositoryPort.findById(requestDTO.getFilialId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Filial não encontrada com ID: " + requestDTO.getFilialId()));

        Funcionario funcionario = funcionarioMapper.toEntity(requestDTO);
        funcionario.setFilial(filial);

        Funcionario savedFuncionario = funcionarioRepositoryPort.save(funcionario);
        return funcionarioMapper.toResponseDTO(savedFuncionario);
    }

    @Override
    @Transactional
    public FuncionarioResponseDTO update(Integer id, FuncionarioRequestDTO requestDTO) {
        Funcionario funcionario = findFuncionarioById(id);

        // Validações de unicidade para atualização
        funcionarioUniquenessService.validarEmailUnicoParaAtualizacao(requestDTO.getEmail(), id);
        funcionarioUniquenessService.validarMatriculaUnicaParaAtualizacao(requestDTO.getMatricula(), id);

        // Valida filial se mudou
        if (!funcionario.getFilial().getId().equals(requestDTO.getFilialId())) {
            Filial novaFilial = filialRepositoryPort.findById(requestDTO.getFilialId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Filial não encontrada com ID: " + requestDTO.getFilialId()));
            funcionario.setFilial(novaFilial);
        }

        funcionarioMapper.updateEntityFromDTO(funcionario, requestDTO);
        Funcionario updatedFuncionario = funcionarioRepositoryPort.save(funcionario);
        return funcionarioMapper.toResponseDTO(updatedFuncionario);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        if (funcionarioRepositoryPort.findById(id).isEmpty()) {
            throw new ResourceNotFoundException("Funcionário não encontrado com ID: " + id);
        }
        funcionarioRepositoryPort.deleteById(id);
    }

    @Override
    @Transactional
    public FuncionarioResponseDTO toggleAtivo(Integer id, Boolean ativo) {
        Funcionario funcionario = findFuncionarioById(id);
        funcionario.setAtivo(ativo);
        Funcionario updatedFuncionario = funcionarioRepositoryPort.save(funcionario);
        return funcionarioMapper.toResponseDTO(updatedFuncionario);
    }

    @Override
    @Transactional
    public FuncionarioResponseDTO trocarFilial(Integer id, Integer novaFilialId) {
        Funcionario funcionario = findFuncionarioById(id);

        Filial novaFilial = filialRepositoryPort.findById(novaFilialId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Filial não encontrada com ID: " + novaFilialId));

        funcionario.setFilial(novaFilial);
        Funcionario updatedFuncionario = funcionarioRepositoryPort.save(funcionario);
        return funcionarioMapper.toResponseDTO(updatedFuncionario);
    }

    private Funcionario findFuncionarioById(Integer id) {
        return funcionarioRepositoryPort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Funcionário não encontrado com ID: " + id));
    }
}
