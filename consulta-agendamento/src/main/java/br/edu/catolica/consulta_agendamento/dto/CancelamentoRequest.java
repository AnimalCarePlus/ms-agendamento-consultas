package br.edu.catolica.consulta_agendamento.dto;

import jakarta.validation.constraints.NotBlank;

public record CancelamentoRequest(
        @NotBlank(message = "O motivo e obrigatorio") String motivo) {
}
