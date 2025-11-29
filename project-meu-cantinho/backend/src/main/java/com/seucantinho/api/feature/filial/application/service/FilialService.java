package com.seucantinho.api.feature.filial.application.service;

import com.seucantinho.api.feature.filial.domain.Filial;
import com.seucantinho.api.feature.filial.application.dto.FilialRequestDTO;
import com.seucantinho.api.feature.filial.application.dto.FilialResponseDTO;
import com.seucantinho.api.feature.funcionario.infrastructure.persistence.FuncionarioRepository;
import com.seucantinho.api.shared.domain.exception.BusinessException;
import com.seucantinho.api.shared.domain.exception.ResourceNotFoundException;
import com.seucantinho.api.feature.filial.infrastructure.mapper.FilialMapper;
import com.seucantinho.api.feature.filial.domain.port.out.FilialRepositoryPort;
import com.seucantinho.api.feature.filial.domain.port.in.FilialServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FilialService implements FilialServicePort {

    private final FilialRepositoryPort filialRepositoryPort;
    private final FilialMapper filialMapper;
    private final FuncionarioRepository funcionarioRepository;

    @Override
    @Transactional(readOnly = true)
    public List<FilialResponseDTO> findAll() {
        return filialRepositoryPort.findAll().stream()
                .map(filialMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public FilialResponseDTO findById(Integer id) {
        Filial filial = findFilialById(id);
        return filialMapper.toResponseDTO(filial);
    }

    @Override
    @Transactional
    public FilialResponseDTO create(FilialRequestDTO requestDTO) {
        Filial filial = filialMapper.toEntity(requestDTO);
        Filial savedFilial = filialRepositoryPort.save(filial);
        return filialMapper.toResponseDTO(savedFilial);
    }

    @Override
    @Transactional
    public FilialResponseDTO update(Integer id, FilialRequestDTO requestDTO) {
        Filial filial = findFilialById(id);
        filialMapper.updateEntityFromDTO(filial, requestDTO);
        Filial updatedFilial = filialRepositoryPort.save(filial);
        return filialMapper.toResponseDTO(updatedFilial);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        if (filialRepositoryPort.findById(id).isEmpty()) {
            throw new ResourceNotFoundException("Filial não encontrada com ID: " + id);
        }

        if (!funcionarioRepository.findByFilialId(id).isEmpty()) {
            throw new BusinessException("Não é possível excluir a filial pois existem funcionários associados a ela. " +
                    "Remova ou transfira os funcionários antes de excluir a filial.");
        }

        filialRepositoryPort.deleteById(id);
    }

    private Filial findFilialById(Integer id) {
        return filialRepositoryPort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Filial não encontrada com ID: " + id));
    }
}
