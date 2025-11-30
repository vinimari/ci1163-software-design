package com.seucantinho.api.feature.pagamento.domain.port.out;

import com.seucantinho.api.feature.pagamento.domain.Pagamento;

import java.util.List;
import java.util.Optional;

public interface PagamentoRepositoryPort {

    List<Pagamento> findAll();

    Optional<Pagamento> findById(Integer id);

    List<Pagamento> findByReservaId(Integer reservaId);

    Pagamento save(Pagamento pagamento);

    void deleteById(Integer id);

    boolean existsById(Integer id);
}