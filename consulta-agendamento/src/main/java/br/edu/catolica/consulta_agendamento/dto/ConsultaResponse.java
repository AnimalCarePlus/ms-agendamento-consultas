package br.edu.catolica.consulta_agendamento.dto;

import java.time.LocalDateTime;

import br.edu.catolica.consulta_agendamento.model.StatusConsulta;
import br.edu.catolica.consulta_agendamento.model.TipoConsulta;

public record ConsultaResponse(
        Long id,
        Long animalId,
        Long veterinarioId,
        String veterinarioNome,
        LocalDateTime dataHora,
        TipoConsulta tipo,
        StatusConsulta status,
        LocalDateTime criadoEm,
        LocalDateTime atualizadoEm) {
}

