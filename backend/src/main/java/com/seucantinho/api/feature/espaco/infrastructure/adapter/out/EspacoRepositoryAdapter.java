package com.seucantinho.api.feature.espaco.infrastructure.adapter.out;

import com.seucantinho.api.feature.espaco.domain.Espaco;
import com.seucantinho.api.feature.espaco.domain.port.out.EspacoRepositoryPort;
import com.seucantinho.api.feature.espaco.infrastructure.persistence.EspacoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class EspacoRepositoryAdapter implements EspacoRepositoryPort {

    private final EspacoRepository espacoRepository;

    @Override
    public List<Espaco> findAll() {
        return espacoRepository.findAll();
    }

    @Override
    public Optional<Espaco> findById(Integer id) {
        return espacoRepository.findById(id);
    }

    @Override
    public List<Espaco> findByFilialId(Integer filialId) {
        return espacoRepository.findByFilialId(filialId);
    }

    @Override
    public List<Espaco> findByAtivoTrue() {
        return espacoRepository.findByAtivoTrue();
    }

    @Override
    public List<Espaco> findByFilialIdAndAtivoTrue(Integer filialId) {
        return espacoRepository.findByFilialIdAndAtivoTrue(filialId);
    }

    @Override
    public Optional<Espaco> findByIdWithFilial(Integer id) {
        return espacoRepository.findByIdWithFilial(id);
    }

    @Override
    public List<Espaco> findEspacosDisponiveisPorData(LocalDate data, Integer capacidadeMinima) {
        return espacoRepository.findEspacosDisponiveisPorData(data, capacidadeMinima);
    }

    @Override
    public Espaco save(Espaco espaco) {
        return espacoRepository.save(espaco);
    }

    @Override
    public void deleteById(Integer id) {
        espacoRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Integer id) {
        return espacoRepository.existsById(id);
    }
}