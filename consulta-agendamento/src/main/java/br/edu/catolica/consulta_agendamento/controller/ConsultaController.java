package br.edu.catolica.consulta_agendamento.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.edu.catolica.consulta_agendamento.dto.CancelamentoRequest;
import br.edu.catolica.consulta_agendamento.dto.ConsultaRequest;
import br.edu.catolica.consulta_agendamento.dto.ConsultaResponse;
import br.edu.catolica.consulta_agendamento.dto.DisponibilidadeResponse;
import br.edu.catolica.consulta_agendamento.service.ConsultaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/consultas")
@RequiredArgsConstructor
@Validated
public class ConsultaController {

    private final ConsultaService consultaService;

    @PostMapping
    public ResponseEntity<ConsultaResponse> agendar(@RequestBody @Valid ConsultaRequest request) {
        ConsultaResponse response = consultaService.agendar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ConsultaResponse> atualizar(@PathVariable Long id,
            @RequestBody @Valid ConsultaRequest request) {
        ConsultaResponse response = consultaService.atualizar(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelar(@PathVariable Long id,
            @RequestBody @Valid CancelamentoRequest request) {
        consultaService.cancelar(id, request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConsultaResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(consultaService.buscarPorId(id));
    }

    @GetMapping("/veterinarios/{veterinarioId}")
    public ResponseEntity<List<ConsultaResponse>> listarPorVeterinario(@PathVariable Long veterinarioId) {
        return ResponseEntity.ok(consultaService.listarPorVeterinario(veterinarioId));
    }

    @GetMapping("/disponibilidade")
    public ResponseEntity<DisponibilidadeResponse> disponibilidade(
            @RequestParam Long veterinarioId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data) {
        return ResponseEntity.ok(consultaService.consultarDisponibilidade(veterinarioId, data));
    }
}
