package br.edu.catolica.consulta_agendamento.dto;

import java.time.LocalDateTime;

import br.edu.catolica.consulta_agendamento.model.TipoConsulta;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

public record ConsultaRequest(
        @NotNull(message = "O identificador do animal eh obrigatorio") Long animalId,
        @NotNull(message = "O identificador do veterinario eh obrigatorio") Long veterinarioId,
        @NotNull(message = "A data e hora sao obrigatorias") @Future(message = "A data deve estar no futuro") LocalDateTime dataHora,
        @NotNull(message = "O tipo de consulta eh obrigatorio") TipoConsulta tipo) {
}

