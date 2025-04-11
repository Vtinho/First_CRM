package com.vitorM.CRM.repository;

import com.vitorM.CRM.model.Oportunidade;
import com.vitorM.CRM.model.StatusOportunidade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface OportunidadeRepository extends JpaRepository<Oportunidade, Long> {

    List<Oportunidade> findByClienteId(Long clienteId);

    Page<Oportunidade> findByClienteId(Long clienteId, Pageable pageable);

    List<Oportunidade> findByUsuarioId(Long usuarioId);

    List<Oportunidade> findByStatus(StatusOportunidade status);

    List<Oportunidade> findByDataFechamentoPrevistaBetween(LocalDate inicio, LocalDate fim);

    @Query("SELECT SUM(o.valor) FROM Oportunidade o WHERE o.status = :status")
    BigDecimal calcularValorTotalPorStatus(StatusOportunidade status);
}