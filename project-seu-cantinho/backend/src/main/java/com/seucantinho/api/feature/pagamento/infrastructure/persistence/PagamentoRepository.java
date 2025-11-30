package com.seucantinho.api.feature.pagamento.infrastructure.persistence;

import com.seucantinho.api.feature.pagamento.domain.Pagamento;
import com.seucantinho.api.feature.pagamento.domain.enums.TipoPagamentoEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface PagamentoRepository extends JpaRepository<Pagamento, Integer> {

    List<Pagamento> findByReservaId(Integer reservaId);

    List<Pagamento> findByTipo(TipoPagamentoEnum tipo);

    @Query("SELECT SUM(p.valor) FROM Pagamento p WHERE p.reserva.id = :reservaId")
    BigDecimal sumValorByReservaId(Integer reservaId);
}
