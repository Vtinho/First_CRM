package com.vitorM.CRM.repository;

import com.vitorM.CRM.model.Cliente;
import com.vitorM.CRM.model.StatusCliente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    Optional<Cliente> findByEmail(String email);

    Page<Cliente> findByNomeContainingIgnoreCase(String nome, Pageable pageable);

    List<Cliente> findByStatus(StatusCliente status);

    @Query("SELECT c FROM Cliente c WHERE " +
            "LOWER(c.nome) LIKE LOWER(CONCAT('%', :busca, '%')) OR " +
            "LOWER(c.email) LIKE LOWER(CONCAT('%', :busca, '%')) OR " +
            "LOWER(c.empresa) LIKE LOWER(CONCAT('%', :busca, '%'))")
    Page<Cliente> buscarClientes(@Param("busca") String busca, Pageable pageable);
}

