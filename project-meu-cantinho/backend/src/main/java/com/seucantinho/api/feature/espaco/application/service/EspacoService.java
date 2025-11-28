package com.seucantinho.api.feature.espaco.application.service;

import com.seucantinho.api.feature.espaco.domain.Espaco;
import com.seucantinho.api.feature.filial.domain.Filial;
import com.seucantinho.api.feature.espaco.application.dto.EspacoRequestDTO;
import com.seucantinho.api.feature.espaco.application.dto.EspacoResponseDTO;
import com.seucantinho.api.shared.domain.exception.ResourceNotFoundException;
import com.seucantinho.api.feature.espaco.infrastructure.mapper.EspacoMapper;
import com.seucantinho.api.feature.espaco.infrastructure.persistence.EspacoRepository;
import com.seucantinho.api.feature.filial.infrastructure.persistence.FilialRepository;
import com.seucantinho.api.feature.espaco.application.port.in.IEspacoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EspacoService implements IEspacoService {

    private final EspacoRepository espacoRepository;
    private final FilialRepository filialRepository;
    private final EspacoMapper espacoMapper;

    @Override
    @Transactional(readOnly = true)
    public List<EspacoResponseDTO> findAll() {
        return espacoRepository.findAll().stream()
                .map(espacoMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EspacoResponseDTO> findByFilialId(Integer filialId) {
        return espacoRepository.findByFilialId(filialId).stream()
                .map(espacoMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EspacoResponseDTO> findAtivos() {
        return espacoRepository.findByAtivoTrue().stream()
                .map(espacoMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EspacoResponseDTO> findDisponiveisPorData(LocalDate data, Integer capacidadeMinima) {
        return espacoRepository.findEspacosDisponiveisPorData(data, capacidadeMinima).stream()
                .map(espacoMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public EspacoResponseDTO findById(Integer id) {
        Espaco espaco = espacoRepository.findByIdWithFilial(id)
                .orElseThrow(() -> new ResourceNotFoundException("Espaço não encontrado com ID: " + id));
        return espacoMapper.toResponseDTO(espaco);
    }

    @Override
    @Transactional
    public EspacoResponseDTO create(EspacoRequestDTO requestDTO) {
        Filial filial = findFilialById(requestDTO.getFilialId());
        Espaco espaco = espacoMapper.toEntity(requestDTO, filial);
        Espaco savedEspaco = espacoRepository.save(espaco);
        return espacoMapper.toResponseDTO(savedEspaco);
    }

    @Override
    @Transactional
    public EspacoResponseDTO update(Integer id, EspacoRequestDTO requestDTO) {
        Espaco espaco = findEspacoById(id);
        Filial filial = findFilialById(requestDTO.getFilialId());
        espacoMapper.updateEntityFromDTO(espaco, requestDTO, filial);
        Espaco updatedEspaco = espacoRepository.save(espaco);
        return espacoMapper.toResponseDTO(updatedEspaco);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        if (!espacoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Espaço não encontrado com ID: " + id);
        }
        espacoRepository.deleteById(id);
    }

    private Espaco findEspacoById(Integer id) {
        return espacoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Espaço não encontrado com ID: " + id));
    }

    private Filial findFilialById(Integer filialId) {
        return filialRepository.findById(filialId)
                .orElseThrow(() -> new ResourceNotFoundException("Filial não encontrada com ID: " + filialId));
    }
}
