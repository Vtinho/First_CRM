package com.vitorM.CRM.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "oportunidades")

public class Oportunidade {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Título é obrigatório")
    private String titulo;

    private String descricao;

    @NotNull(message = "Valor é obrigatório")
    private BigDecimal valor;

    @Enumerated(EnumType.STRING)
    private StatusOportunidade status = StatusOportunidade.ABERTA;

    @Column(name = "data_abertura")
    private LocalDateTime dataAbertura = LocalDateTime.now();

    @Column(name = "data_fechamento_prevista")
    private LocalDate dataFechamentoPrevista;

    @Column(name = "data_fechamento_real")
    private LocalDate dataFechamentoReal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

}
