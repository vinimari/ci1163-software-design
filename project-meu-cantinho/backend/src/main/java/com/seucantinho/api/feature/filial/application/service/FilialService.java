package com.seucantinho.api.feature.filial.application.service;

import com.seucantinho.api.feature.filial.domain.Filial;
import com.seucantinho.api.feature.filial.application.dto.FilialRequestDTO;
import com.seucantinho.api.feature.filial.application.dto.FilialResponseDTO;
import com.seucantinho.api.shared.domain.exception.ResourceNotFoundException;
import com.seucantinho.api.feature.filial.infrastructure.mapper.FilialMapper;
import com.seucantinho.api.feature.filial.infrastructure.persistence.FilialRepository;
import com.seucantinho.api.feature.filial.application.port.in.IFilialService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FilialService implements IFilialService {

    private final FilialRepository filialRepository;
    private final FilialMapper filialMapper;

    @Override
    @Transactional(readOnly = true)
    public List<FilialResponseDTO> findAll() {
        return filialRepository.findAll().stream()
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
        Filial savedFilial = filialRepository.save(filial);
        return filialMapper.toResponseDTO(savedFilial);
    }

    @Override
    @Transactional
    public FilialResponseDTO update(Integer id, FilialRequestDTO requestDTO) {
        Filial filial = findFilialById(id);
        filialMapper.updateEntityFromDTO(filial, requestDTO);
        Filial updatedFilial = filialRepository.save(filial);
        return filialMapper.toResponseDTO(updatedFilial);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        if (!filialRepository.existsById(id)) {
            throw new ResourceNotFoundException("Filial não encontrada com ID: " + id);
        }
        filialRepository.deleteById(id);
    }

    private Filial findFilialById(Integer id) {
        return filialRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Filial não encontrada com ID: " + id));
    }
}
