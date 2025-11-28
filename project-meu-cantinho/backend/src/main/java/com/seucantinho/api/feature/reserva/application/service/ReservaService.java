package com.seucantinho.api.feature.reserva.application.service;

import com.seucantinho.api.feature.espaco.domain.Espaco;
import com.seucantinho.api.feature.reserva.domain.Reserva;
import com.seucantinho.api.feature.usuario.domain.Usuario;
import com.seucantinho.api.feature.funcionario.domain.Funcionario;
import com.seucantinho.api.feature.reserva.domain.enums.StatusReservaEnum;
import com.seucantinho.api.feature.reserva.domain.valueobject.DataEvento;
import com.seucantinho.api.shared.domain.valueobject.ValorMonetario;
import com.seucantinho.api.feature.reserva.application.dto.ReservaRequestDTO;
import com.seucantinho.api.feature.reserva.application.dto.ReservaResponseDTO;
import com.seucantinho.api.shared.domain.exception.ResourceNotFoundException;
import com.seucantinho.api.feature.reserva.infrastructure.mapper.ReservaMapper;
import com.seucantinho.api.feature.espaco.infrastructure.persistence.EspacoRepository;
import com.seucantinho.api.feature.reserva.infrastructure.persistence.ReservaRepository;
import com.seucantinho.api.feature.usuario.infrastructure.persistence.UsuarioRepository;
import com.seucantinho.api.feature.reserva.application.port.in.IReservaService;
import com.seucantinho.api.feature.reserva.application.validator.ReservaValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservaService implements IReservaService {

    private final ReservaRepository reservaRepository;
    private final UsuarioRepository usuarioRepository;
    private final EspacoRepository espacoRepository;
    private final ReservaMapper reservaMapper;
    private final ReservaValidator reservaValidator;
    private final ReservaStatusService reservaStatusService;

    @Override
    @Transactional(readOnly = true)
    public List<ReservaResponseDTO> findAll() {
        return reservaRepository.findAll().stream()
                .map(reservaMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ReservaResponseDTO findById(Integer id) {
        Reserva reserva = reservaRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva não encontrada com ID: " + id));
        return reservaMapper.toResponseDTO(reserva);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservaResponseDTO> findByUsuarioId(Integer usuarioId) {
        return reservaRepository.findByUsuarioId(usuarioId).stream()
                .map(reservaMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservaResponseDTO> findByEspacoId(Integer espacoId) {
        return reservaRepository.findByEspacoId(espacoId).stream()
                .map(reservaMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ReservaResponseDTO create(ReservaRequestDTO requestDTO) {
        Usuario usuario = findUsuarioById(requestDTO.getUsuarioId());
        Espaco espaco = findEspacoById(requestDTO.getEspacoId());

        reservaValidator.validateEspacoAtivo(espaco);
        reservaValidator.validateDisponibilidade(
                requestDTO.getEspacoId(),
                requestDTO.getDataEvento(),
                null
        );

        Reserva reserva = reservaMapper.toEntity(requestDTO, usuario, espaco);
        reservaValidator.validateValorTotal(reserva);
        Reserva savedReserva = reservaRepository.save(reserva);
        return reservaMapper.toResponseDTO(savedReserva);
    }

    @Override
    @Transactional
    public ReservaResponseDTO update(Integer id, ReservaRequestDTO requestDTO) {
        Reserva reserva = findReservaById(id);

        if (!reserva.getEspaco().getId().equals(requestDTO.getEspacoId()) ||
            !reserva.getDataEvento().getData().equals(requestDTO.getDataEvento())) {

            reservaValidator.validateDisponibilidade(
                    requestDTO.getEspacoId(),
                    requestDTO.getDataEvento(),
                    id
            );
        }

        reserva.setDataEvento(DataEvento.of(requestDTO.getDataEvento()));
        reserva.setValorTotal(ValorMonetario.of(requestDTO.getValorTotal()));
        reserva.setObservacoes(requestDTO.getObservacoes());

        if (requestDTO.getStatus() != null) {
            if (requestDTO.getStatus() == StatusReservaEnum.CANCELADA) {
                reservaStatusService.cancelReservation(reserva);
            } else {
                reserva.setStatus(requestDTO.getStatus());
            }
        }

        Reserva updatedReserva = reservaRepository.save(reserva);
        return reservaMapper.toResponseDTO(updatedReserva);
    }

    @Override
    @Transactional
    public ReservaResponseDTO updateStatus(Integer id, StatusReservaEnum novoStatus) {
        Reserva reserva = findReservaById(id);

        if (novoStatus == StatusReservaEnum.CANCELADA) {
            reservaStatusService.cancelReservation(reserva);
        } else {
            reserva.setStatus(novoStatus);
        }

        Reserva updatedReserva = reservaRepository.save(reserva);
        return reservaMapper.toResponseDTO(updatedReserva);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        if (!reservaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Reserva não encontrada com ID: " + id);
        }
        reservaRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservaResponseDTO> findByAcessoPorEmail(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com email: " + email));

        if (usuario.getPerfil() != null && usuario.getPerfil().name().equalsIgnoreCase("ADMIN")) {
            return findAll();
        }

        if (usuario instanceof Funcionario) {
            Funcionario funcionario = (Funcionario) usuario;
            if (funcionario.getFilial() == null) {
                return Collections.emptyList();
            }
            Integer filialId = funcionario.getFilial().getId();
            return reservaRepository.findByEspacoFilialId(filialId).stream()
                    .map(reservaMapper::toResponseDTO)
                    .collect(Collectors.toList());
        }

        return reservaRepository.findByUsuarioId(usuario.getId()).stream()
                .map(reservaMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    private Reserva findReservaById(Integer id) {
        return reservaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva não encontrada com ID: " + id));
    }

    private Usuario findUsuarioById(Integer usuarioId) {
        return usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com ID: " + usuarioId));
    }

    private Espaco findEspacoById(Integer espacoId) {
        return espacoRepository.findById(espacoId)
                .orElseThrow(() -> new ResourceNotFoundException("Espaço não encontrado com ID: " + espacoId));
    }
}
