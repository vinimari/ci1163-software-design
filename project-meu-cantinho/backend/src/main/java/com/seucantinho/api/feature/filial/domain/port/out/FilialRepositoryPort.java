package com.seucantinho.api.feature.filial.domain.port.out;

import com.seucantinho.api.feature.filial.domain.Filial;

import java.util.List;
import java.util.Optional;

public interface FilialRepositoryPort {

    List<Filial> findAll();

    Optional<Filial> findById(Integer id);

    Filial save(Filial filial);

    void deleteById(Integer id);

    boolean existsById(Integer id);
}