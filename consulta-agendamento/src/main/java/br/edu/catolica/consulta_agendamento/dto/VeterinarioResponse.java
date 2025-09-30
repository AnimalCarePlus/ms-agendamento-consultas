package br.edu.catolica.consulta_agendamento.dto;

public record VeterinarioResponse(
        Long id,
        String nome,
        String registroProfissional,
        String especialidade) {
}
