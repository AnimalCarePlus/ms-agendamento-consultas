package br.edu.catolica.consulta_agendamento.service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.catolica.consulta_agendamento.dto.DisponibilidadeResponse;
import br.edu.catolica.consulta_agendamento.exception.NotFoundException;
import br.edu.catolica.consulta_agendamento.model.Consulta;
import br.edu.catolica.consulta_agendamento.model.StatusConsulta;
import br.edu.catolica.consulta_agendamento.repository.ConsultaRepository;
import br.edu.catolica.consulta_agendamento.repository.VeterinarioRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DisponibilidadeService {

    private static final LocalTime INICIO_EXPEDIENTE = LocalTime.of(8, 0);
    private static final LocalTime FIM_EXPEDIENTE = LocalTime.of(18, 0);
    private static final Duration DURACAO_PADRAO = Duration.ofHours(1);

    private final ConsultaRepository consultaRepository;
    private final VeterinarioRepository veterinarioRepository;

    public DisponibilidadeResponse calcularDisponibilidade(Long veterinarioId, LocalDate data) {
        validarVeterinarioExiste(veterinarioId);

        LocalDateTime inicioDia = data.atStartOfDay();
        LocalDateTime fimDia = data.atTime(LocalTime.MAX);
        List<Consulta> consultas = consultaRepository.findAllWithinPeriod(veterinarioId, inicioDia, fimDia);

        Set<LocalDateTime> horariosOcupados = new HashSet<>();
        for (Consulta consulta : consultas) {
            if (consulta.getStatus() != StatusConsulta.CANCELADA) {
                horariosOcupados.add(consulta.getDataHora());
            }
        }

        List<LocalDateTime> horariosLivres = new ArrayList<>();
        LocalDateTime agora = LocalDateTime.now();

        LocalDateTime slot = data.atTime(INICIO_EXPEDIENTE);
        LocalDateTime limite = data.atTime(FIM_EXPEDIENTE);
        while (slot.isBefore(limite)) {
            if (!slot.isBefore(agora) && !horariosOcupados.contains(slot)) {
                horariosLivres.add(slot);
            }
            slot = slot.plus(DURACAO_PADRAO);
        }

        return new DisponibilidadeResponse(veterinarioId, data, horariosLivres);
    }

    private void validarVeterinarioExiste(Long veterinarioId) {
        if (!veterinarioRepository.existsById(veterinarioId)) {
            throw new NotFoundException("Veterinario nao encontrado");
        }
    }
}
