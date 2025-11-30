package com.seucantinho.api.feature.espaco.application.service;

import com.seucantinho.api.feature.espaco.domain.Espaco;
import com.seucantinho.api.feature.filial.domain.Filial;
import com.seucantinho.api.feature.espaco.application.dto.EspacoRequestDTO;
import com.seucantinho.api.feature.espaco.application.dto.EspacoResponseDTO;
import com.seucantinho.api.shared.domain.exception.ResourceNotFoundException;
import com.seucantinho.api.feature.espaco.infrastructure.mapper.EspacoMapper;
import com.seucantinho.api.feature.espaco.domain.port.out.EspacoRepositoryPort;
import com.seucantinho.api.feature.filial.domain.port.out.FilialRepositoryPort;
import com.seucantinho.api.feature.espaco.domain.port.in.EspacoServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EspacoService implements EspacoServicePort {

    private final EspacoRepositoryPort espacoRepositoryPort;
    private final FilialRepositoryPort filialRepositoryPort;
    private final EspacoMapper espacoMapper;

    @Override
    @Transactional(readOnly = true)
    public List<EspacoResponseDTO> findAll() {
        return espacoRepositoryPort.findAll().stream()
                .map(espacoMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EspacoResponseDTO> findByFilialId(Integer filialId) {
        return espacoRepositoryPort.findByFilialId(filialId).stream()
                .map(espacoMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EspacoResponseDTO> findAtivos() {
        return espacoRepositoryPort.findByAtivoTrue().stream()
                .map(espacoMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EspacoResponseDTO> findDisponiveisPorData(LocalDate data, Integer capacidadeMinima) {
        return espacoRepositoryPort.findEspacosDisponiveisPorData(data, capacidadeMinima).stream()
                .map(espacoMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public EspacoResponseDTO findById(Integer id) {
        Espaco espaco = espacoRepositoryPort.findByIdWithFilial(id)
                .orElseThrow(() -> new ResourceNotFoundException("Espaço não encontrado com ID: " + id));
        return espacoMapper.toResponseDTO(espaco);
    }

    @Override
    @Transactional
    public EspacoResponseDTO create(EspacoRequestDTO requestDTO) {
        Filial filial = findFilialById(requestDTO.getFilialId());
        Espaco espaco = espacoMapper.toEntity(requestDTO, filial);
        espaco.validar();
        Espaco savedEspaco = espacoRepositoryPort.save(espaco);
        return espacoMapper.toResponseDTO(savedEspaco);
    }

    @Override
    @Transactional
    public EspacoResponseDTO update(Integer id, EspacoRequestDTO requestDTO) {
        Espaco espaco = findEspacoById(id);
        Filial filial = findFilialById(requestDTO.getFilialId());
        espacoMapper.updateEntityFromDTO(espaco, requestDTO, filial);
        espaco.validar();
        Espaco updatedEspaco = espacoRepositoryPort.save(espaco);
        return espacoMapper.toResponseDTO(updatedEspaco);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        Espaco espaco = findEspacoById(id);
        Filial filial = espaco.getFilial();

        if (filial != null) {
            // Remove a associação bidirecional
            filial.getEspacos().remove(espaco);
            espaco.setFilial(null);

            // Força a deleção do espaço
            espacoRepositoryPort.deleteById(id);
        } else {
            espacoRepositoryPort.deleteById(id);
        }
    }

    private Espaco findEspacoById(Integer id) {
        return espacoRepositoryPort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Espaço não encontrado com ID: " + id));
    }

    private Filial findFilialById(Integer filialId) {
        return filialRepositoryPort.findById(filialId)
                .orElseThrow(() -> new ResourceNotFoundException("Filial não encontrada com ID: " + filialId));
    }
}
