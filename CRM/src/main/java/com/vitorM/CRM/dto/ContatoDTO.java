package com.vitorM.CRM.dto;

import com.vitorM.CRM.model.TipoContato;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContatoDTO {

    private Long id;

    @NotBlank(message = "Descrição é obrigatória")
    private String descricao;

    @NotNull(message = "Tipo de contato é obrigatório")
    private TipoContato tipo;

    private LocalDateTime dataContato;

    @NotNull(message = "Cliente é obrigatório")
    private Long clienteId;

    private Long usuarioId;

    private String nomeCliente;

    private String nomeUsuario;
}
