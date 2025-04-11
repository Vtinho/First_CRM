package com.vitorM.CRM.repository;

import com.vitorM.CRM.model.Contato;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ContatoRepository extends JpaRepository<Contato, Long> {

    List<Contato> findByClienteId(Long clienteId);

    Page<Contato> findByClienteId(Long clienteId, Pageable pageable);

    List<Contato> findByUsuarioId(Long usuarioId);

    List<Contato> findByDataContatoBetween(LocalDateTime inicio, LocalDateTime fim);
}
