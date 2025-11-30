package com.seucantinho.api.feature.administrador.infrastructure.persistence;

import com.seucantinho.api.feature.administrador.domain.Administrador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdministradorRepository extends JpaRepository<Administrador, Integer> {

    Optional<Administrador> findByEmail(String email);
}
