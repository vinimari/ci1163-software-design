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
import com.seucantinho.api.feature.espaco.domain.port.out.EspacoRepositoryPort;
import com.seucantinho.api.feature.reserva.domain.port.out.ReservaRepositoryPort;
import com.seucantinho.api.feature.usuario.domain.port.out.UsuarioRepositoryPort;
import com.seucantinho.api.feature.reserva.domain.port.in.ReservaServicePort;
import com.seucantinho.api.feature.reserva.domain.service.ReservaAvailabilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservaService implements ReservaServicePort {

    private final ReservaRepositoryPort reservaRepositoryPort;
    private final UsuarioRepositoryPort usuarioRepositoryPort;
    private final EspacoRepositoryPort espacoRepositoryPort;
    private final ReservaMapper reservaMapper;
    private final ReservaAvailabilityService reservaAvailabilityService;
    private final ReservaStatusService reservaStatusService;

    @Override
    @Transactional(readOnly = true)
    public List<ReservaResponseDTO> findAll() {
        return reservaRepositoryPort.findAll().stream()
                .map(reservaMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ReservaResponseDTO findById(Integer id) {
        Reserva reserva = reservaRepositoryPort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva não encontrada com ID: " + id));
        return reservaMapper.toResponseDTO(reserva);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservaResponseDTO> findByUsuarioId(Integer usuarioId) {
        return reservaRepositoryPort.findByUsuarioId(usuarioId).stream()
                .map(reservaMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservaResponseDTO> findByEspacoId(Integer espacoId) {
        return reservaRepositoryPort.findByEspacoId(espacoId).stream()
                .map(reservaMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ReservaResponseDTO create(ReservaRequestDTO requestDTO) {
        Usuario usuario = findUsuarioById(requestDTO.getUsuarioId());
        Espaco espaco = findEspacoById(requestDTO.getEspacoId());

        Reserva reserva = reservaMapper.toEntity(requestDTO, usuario, espaco);

        // Usar validações centralizadas no domínio
        reserva.validar();
        reservaAvailabilityService.validarDisponibilidade(
                requestDTO.getEspacoId(), requestDTO.getDataEvento(), null);

        Reserva savedReserva = reservaRepositoryPort.save(reserva);
        return reservaMapper.toResponseDTO(savedReserva);
    }

    @Override
    @Transactional
    public ReservaResponseDTO update(Integer id, ReservaRequestDTO requestDTO) {
        Reserva reserva = findReservaById(id);

        if (!reserva.getEspaco().getId().equals(requestDTO.getEspacoId()) ||
            !reserva.getDataEvento().getData().equals(requestDTO.getDataEvento())) {

            reservaAvailabilityService.validarDisponibilidade(
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
                reserva.transitionToStatus(requestDTO.getStatus());
            }
        }

        reserva.validar();
        Reserva updatedReserva = reservaRepositoryPort.save(reserva);
        return reservaMapper.toResponseDTO(updatedReserva);
    }

    @Override
    @Transactional
    public ReservaResponseDTO updateStatus(Integer id, StatusReservaEnum novoStatus) {
        Reserva reserva = findReservaById(id);

        if (novoStatus == StatusReservaEnum.CANCELADA) {
            reservaStatusService.cancelReservation(reserva);
        } else {
            reserva.transitionToStatus(novoStatus);
        }

        Reserva updatedReserva = reservaRepositoryPort.save(reserva);
        return reservaMapper.toResponseDTO(updatedReserva);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        if (!reservaRepositoryPort.existsById(id)) {
            throw new ResourceNotFoundException("Reserva não encontrada com ID: " + id);
        }
        reservaRepositoryPort.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservaResponseDTO> findByAcessoPorEmail(String email) {
        Usuario usuario = usuarioRepositoryPort.findByEmail(email)
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
            // Método não existe no port, vamos usar findByEspacoId como alternativa
            return reservaRepositoryPort.findAll().stream()
                    .filter(r -> r.getEspaco() != null && r.getEspaco().getFilial().getId().equals(filialId))
                    .map(reservaMapper::toResponseDTO)
                    .collect(Collectors.toList());
        }

        return reservaRepositoryPort.findByUsuarioId(usuario.getId()).stream()
                .map(reservaMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    private Reserva findReservaById(Integer id) {
        return reservaRepositoryPort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva não encontrada com ID: " + id));
    }

    private Usuario findUsuarioById(Integer usuarioId) {
        return usuarioRepositoryPort.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com ID: " + usuarioId));
    }

    private Espaco findEspacoById(Integer espacoId) {
        return espacoRepositoryPort.findById(espacoId)
                .orElseThrow(() -> new ResourceNotFoundException("Espaço não encontrado com ID: " + espacoId));
    }
}
