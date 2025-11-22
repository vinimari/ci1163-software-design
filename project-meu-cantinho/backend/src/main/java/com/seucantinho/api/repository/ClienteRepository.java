package com.seucantinho.api.repository;

import com.seucantinho.api.domain.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Integer> {

    Optional<Cliente> findByEmail(String email);

    Optional<Cliente> findByCpf(String cpf);

    @Query("SELECT c FROM Cliente c LEFT JOIN FETCH c.reservas WHERE c.id = :id")
    Optional<Cliente> findByIdWithReservas(Integer id);

    List<Cliente> findByAtivoTrue();
}
