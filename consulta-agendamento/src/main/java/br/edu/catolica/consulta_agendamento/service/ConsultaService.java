package br.edu.catolica.consulta_agendamento.service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import br.edu.catolica.consulta_agendamento.client.AnimalClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.catolica.consulta_agendamento.dto.CancelamentoRequest;
import br.edu.catolica.consulta_agendamento.dto.ConsultaRequest;
import br.edu.catolica.consulta_agendamento.dto.ConsultaResponse;
import br.edu.catolica.consulta_agendamento.dto.DisponibilidadeResponse;
import br.edu.catolica.consulta_agendamento.exception.BusinessException;
import br.edu.catolica.consulta_agendamento.exception.NotFoundException;
import br.edu.catolica.consulta_agendamento.model.Consulta;
import br.edu.catolica.consulta_agendamento.model.StatusConsulta;
import br.edu.catolica.consulta_agendamento.model.TipoConsulta;
import br.edu.catolica.consulta_agendamento.model.Veterinario;
import br.edu.catolica.consulta_agendamento.repository.ConsultaRepository;
import br.edu.catolica.consulta_agendamento.repository.VeterinarioRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ConsultaService {

    private static final Duration PRAZO_MINIMO_CANCELAMENTO = Duration.ofHours(2);

    private final ConsultaRepository consultaRepository;
    private final VeterinarioRepository veterinarioRepository;
    private final DisponibilidadeService disponibilidadeService;
    private final AnimalClient animalClient;

    public ConsultaResponse agendar(ConsultaRequest request) {
        boolean existe = animalClient.animalExiste(request.animalId());
        if (!existe) {
            throw new BusinessException("Animal não encontrado no serviço de animais");
        }

        validarDataFutura(request.dataHora());
        Veterinario veterinario = obterVeterinario(request.veterinarioId());
        validarDisponibilidade(veterinario.getId(), request.dataHora(), null);

        Consulta consulta = Consulta.builder()
                .animalId(request.animalId())
                .veterinario(veterinario)
                .dataHora(request.dataHora())
                .tipo(request.tipo())
                .status(StatusConsulta.AGENDADA)
                .build();

        Consulta salva = consultaRepository.save(consulta);
        return toResponse(salva);
    }

    public ConsultaResponse atualizar(Long id, ConsultaRequest request) {
        boolean existe = animalClient.animalExiste(request.animalId());
        if (!existe) {
            throw new BusinessException("Animal não encontrado no serviço de animais");
        }

        Consulta consulta = obterConsulta(id);
        if (consulta.getStatus() == StatusConsulta.CANCELADA) {
            throw new BusinessException("Nao e possivel atualizar uma consulta cancelada");
        }
        if (consulta.getStatus() == StatusConsulta.REALIZADA) {
            throw new BusinessException("Nao e possivel atualizar uma consulta ja realizada");
        }

        validarDataFutura(request.dataHora());
        Veterinario veterinario = obterVeterinario(request.veterinarioId());
        validarDisponibilidade(veterinario.getId(), request.dataHora(), consulta.getId());

        consulta.setAnimalId(request.animalId());
        consulta.setVeterinario(veterinario);
        consulta.setDataHora(request.dataHora());
        consulta.setTipo(request.tipo());

        return toResponse(consulta);
    }

    public void cancelar(Long id, CancelamentoRequest request) {
        Consulta consulta = obterConsulta(id);
        if (consulta.getStatus() == StatusConsulta.CANCELADA) {
            throw new BusinessException("Consulta ja cancelada");
        }
        if (consulta.getStatus() == StatusConsulta.REALIZADA) {
            throw new BusinessException("Nao e possivel cancelar uma consulta realizada");
        }

        LocalDateTime limiteCancelamento = consulta.getDataHora().minus(PRAZO_MINIMO_CANCELAMENTO);
        if (!limiteCancelamento.isAfter(LocalDateTime.now())) {
            throw new BusinessException(String.format(
                    "Cancelamentos so sao permitidos ate %d horas antes do horario agendado",
                    PRAZO_MINIMO_CANCELAMENTO.toHours()));
        }

        consulta.setStatus(StatusConsulta.CANCELADA);
    }

    @Transactional(readOnly = true)
    public ConsultaResponse buscarPorId(Long id) {
        Consulta consulta = obterConsulta(id);
        return toResponse(consulta);
    }

    @Transactional(readOnly = true)
    public List<ConsultaResponse> listarPorVeterinario(Long veterinarioId) {
        if (!veterinarioRepository.existsById(veterinarioId)) {
            throw new NotFoundException("Veterinario nao encontrado");
        }
        return consultaRepository.findByVeterinario_IdOrderByDataHoraAsc(veterinarioId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public DisponibilidadeResponse consultarDisponibilidade(Long veterinarioId, LocalDate data) {
        return disponibilidadeService.calcularDisponibilidade(veterinarioId, data);
    }


    private void validarDataFutura(LocalDateTime dataHora) {
        if (dataHora == null) {
            throw new BusinessException("A data e hora da consulta sao obrigatorias");
        }
        if (!dataHora.isAfter(LocalDateTime.now())) {
            throw new BusinessException("A consulta deve ser agendada para o futuro");
        }
    }

    private Veterinario obterVeterinario(Long id) {
        return veterinarioRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Veterinario nao encontrado"));
    }

    private Consulta obterConsulta(Long id) {
        return consultaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Consulta nao encontrada"));
    }

    private void validarDisponibilidade(Long veterinarioId, LocalDateTime dataHora, Long consultaIdParaIgnorar) {
        consultaRepository.findByVeterinario_IdAndDataHora(veterinarioId, dataHora)
                .filter(consulta -> consulta.getStatus() != StatusConsulta.CANCELADA)
                .filter(consulta -> !Objects.equals(consulta.getId(), consultaIdParaIgnorar))
                .ifPresent(consulta -> {
                    throw new BusinessException("O veterinario ja possui uma consulta nesse horario");
                });
    }

    private ConsultaResponse toResponse(Consulta consulta) {
        Long animalId = consulta.getAnimalId();
        Veterinario veterinario = consulta.getVeterinario();
        TipoConsulta tipo = consulta.getTipo();
        StatusConsulta status = consulta.getStatus();
        return new ConsultaResponse(
                consulta.getId(),
                animalId,
                veterinario != null ? veterinario.getId() : null,
                veterinario != null ? veterinario.getNome() : null,
                consulta.getDataHora(),
                tipo,
                status,
                consulta.getCriadoEm(),
                consulta.getAtualizadoEm());
    }
}