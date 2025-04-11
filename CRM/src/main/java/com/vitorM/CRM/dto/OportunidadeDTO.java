package com.vitorM.CRM.dto;

import com.vitorM.CRM.model.StatusOportunidade;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OportunidadeDTO {

    private Long id;

    @NotBlank(message = "Título é obrigatório")
    private String titulo;

    private String descricao;

    @NotNull(message = "Valor é obrigatório")
    @Positive(message = "Valor deve ser positivo")
    private BigDecimal valor;

    private StatusOportunidade status;

    private LocalDate dataFechamentoPrevista;

    private LocalDate dataFechamentoReal;

    @NotNull(message = "Cliente é obrigatório")
    private Long clienteId;

    private Long usuarioId;

    private String nomeCliente;

    private String nomeUsuario;
}
