package com.seucantinho.api.repository;

import com.seucantinho.api.domain.entity.Pagamento;
import com.seucantinho.api.domain.enums.TipoPagamentoEnum;
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
