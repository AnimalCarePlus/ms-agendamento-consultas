package br.edu.catolica.consulta_agendamento.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record DisponibilidadeResponse(
        Long veterinarioId,
        LocalDate data,
        List<LocalDateTime> horariosLivres) {
}
