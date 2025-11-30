package com.seucantinho.api.feature.pagamento.application.service;

import com.seucantinho.api.feature.pagamento.domain.Pagamento;
import com.seucantinho.api.feature.reserva.domain.Reserva;
import com.seucantinho.api.feature.pagamento.application.dto.PagamentoRequestDTO;
import com.seucantinho.api.feature.pagamento.application.dto.PagamentoResponseDTO;
import com.seucantinho.api.shared.domain.exception.ResourceNotFoundException;
import com.seucantinho.api.feature.pagamento.infrastructure.mapper.PagamentoMapper;
import com.seucantinho.api.feature.pagamento.domain.port.out.PagamentoRepositoryPort;
import com.seucantinho.api.feature.reserva.domain.port.out.ReservaRepositoryPort;
import com.seucantinho.api.feature.reserva.application.service.ReservaStatusService;
import com.seucantinho.api.feature.pagamento.domain.port.in.PagamentoServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PagamentoService implements PagamentoServicePort {

    private final PagamentoRepositoryPort pagamentoRepositoryPort;
    private final ReservaRepositoryPort reservaRepositoryPort;
    private final PagamentoMapper pagamentoMapper;
    private final ReservaStatusService reservaStatusService;

    @Override
    @Transactional(readOnly = true)
    public List<PagamentoResponseDTO> findAll() {
        return pagamentoRepositoryPort.findAll().stream()
                .map(pagamentoMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PagamentoResponseDTO findById(Integer id) {
        Pagamento pagamento = findPagamentoById(id);
        return pagamentoMapper.toResponseDTO(pagamento);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PagamentoResponseDTO> findByReservaId(Integer reservaId) {
        return pagamentoRepositoryPort.findByReservaId(reservaId).stream()
                .map(pagamentoMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PagamentoResponseDTO create(PagamentoRequestDTO requestDTO) {
        Reserva reserva = reservaRepositoryPort.findByIdWithPagamentos(requestDTO.getReservaId())
                .orElseThrow(() -> new ResourceNotFoundException("Reserva não encontrada com ID: " + requestDTO.getReservaId()));

        Pagamento pagamento = pagamentoMapper.toEntity(requestDTO, reserva);
        pagamento.validar();
        Pagamento savedPagamento = pagamentoRepositoryPort.save(pagamento);

        reservaStatusService.updateStatusAfterPayment(reserva, savedPagamento);
        reservaRepositoryPort.save(reserva);

        return pagamentoMapper.toResponseDTO(savedPagamento);
    }

    private Pagamento findPagamentoById(Integer id) {
        return pagamentoRepositoryPort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pagamento não encontrado com ID: " + id));
    }
}
