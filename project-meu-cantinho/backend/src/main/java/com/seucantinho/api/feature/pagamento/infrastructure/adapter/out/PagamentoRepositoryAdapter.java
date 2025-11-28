package com.seucantinho.api.feature.pagamento.infrastructure.adapter.out;

import com.seucantinho.api.feature.pagamento.domain.Pagamento;
import com.seucantinho.api.feature.pagamento.domain.port.out.PagamentoRepositoryPort;
import com.seucantinho.api.feature.pagamento.infrastructure.persistence.PagamentoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PagamentoRepositoryAdapter implements PagamentoRepositoryPort {

    private final PagamentoRepository pagamentoRepository;

    @Override
    public List<Pagamento> findAll() {
        return pagamentoRepository.findAll();
    }

    @Override
    public Optional<Pagamento> findById(Integer id) {
        return pagamentoRepository.findById(id);
    }

    @Override
    public List<Pagamento> findByReservaId(Integer reservaId) {
        return pagamentoRepository.findByReservaId(reservaId);
    }

    @Override
    public Pagamento save(Pagamento pagamento) {
        return pagamentoRepository.save(pagamento);
    }

    @Override
    public void deleteById(Integer id) {
        pagamentoRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Integer id) {
        return pagamentoRepository.existsById(id);
    }
}