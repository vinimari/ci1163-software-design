package com.seucantinho.api.feature.filial.infrastructure.adapter.out;

import com.seucantinho.api.feature.filial.domain.Filial;
import com.seucantinho.api.feature.filial.domain.port.out.FilialRepositoryPort;
import com.seucantinho.api.feature.filial.infrastructure.persistence.FilialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class FilialRepositoryAdapter implements FilialRepositoryPort {

    private final FilialRepository filialRepository;

    @Override
    public List<Filial> findAll() {
        return filialRepository.findAll();
    }

    @Override
    public Optional<Filial> findById(Integer id) {
        return filialRepository.findById(id);
    }

    @Override
    public Filial save(Filial filial) {
        return filialRepository.save(filial);
    }

    @Override
    public void deleteById(Integer id) {
        filialRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Integer id) {
        return filialRepository.existsById(id);
    }
}