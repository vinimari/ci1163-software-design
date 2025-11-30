package com.seucantinho.api.feature.usuario.domain.port.out;

import com.seucantinho.api.feature.usuario.domain.Usuario;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepositoryPort {

    List<Usuario> findAll();

    Optional<Usuario> findById(Integer id);

    Optional<Usuario> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByCpf(String cpf);

    Usuario save(Usuario usuario);

    void deleteById(Integer id);

    boolean existsById(Integer id);
}