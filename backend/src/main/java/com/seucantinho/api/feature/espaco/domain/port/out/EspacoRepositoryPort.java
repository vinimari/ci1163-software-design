package com.seucantinho.api.feature.espaco.domain.port.out;

import com.seucantinho.api.feature.espaco.domain.Espaco;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface EspacoRepositoryPort {

    List<Espaco> findAll();

    Optional<Espaco> findById(Integer id);

    List<Espaco> findByFilialId(Integer filialId);

    List<Espaco> findByAtivoTrue();

    List<Espaco> findByFilialIdAndAtivoTrue(Integer filialId);

    Optional<Espaco> findByIdWithFilial(Integer id);

    List<Espaco> findEspacosDisponiveisPorData(LocalDate data, Integer capacidadeMinima);

    Espaco save(Espaco espaco);

    void deleteById(Integer id);

    boolean existsById(Integer id);
}