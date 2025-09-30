package br.edu.catolica.consulta_agendamento.dto;

import jakarta.validation.constraints.NotBlank;

public record VeterinarioRequest(
        @NotBlank(message = "O nome e obrigatorio") String nome,
        @NotBlank(message = "O registro profissional e obrigatorio") String registroProfissional,
        String especialidade) {
}
