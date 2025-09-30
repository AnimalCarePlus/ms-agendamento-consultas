package br.edu.catolica.consulta_agendamento.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.edu.catolica.consulta_agendamento.dto.VeterinarioRequest;
import br.edu.catolica.consulta_agendamento.dto.VeterinarioResponse;
import br.edu.catolica.consulta_agendamento.model.Veterinario;
import br.edu.catolica.consulta_agendamento.service.VeterinarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/veterinarios")
@RequiredArgsConstructor
@Validated
public class VeterinarioController {

    private final VeterinarioService veterinarioService;

    @PostMapping
    public ResponseEntity<VeterinarioResponse> criar(@RequestBody @Valid VeterinarioRequest request) {
        Veterinario veterinario = Veterinario.builder()
                .nome(request.nome())
                .registroProfissional(request.registroProfissional())
                .especialidade(request.especialidade())
                .build();
        Veterinario salvo = veterinarioService.salvar(veterinario);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapToResponse(salvo));
    }

    @GetMapping("/{id}")
    public ResponseEntity<VeterinarioResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(mapToResponse(veterinarioService.buscarPorId(id)));
    }

    @GetMapping
    public ResponseEntity<List<VeterinarioResponse>> listar() {
        return ResponseEntity.ok(veterinarioService.listarTodos().stream()
                .map(this::mapToResponse)
                .toList());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> remover(@PathVariable Long id) {
        veterinarioService.remover(id);
        return ResponseEntity.ok(Map.of("message", "Veterinario removido com sucesso"));
    }

    private VeterinarioResponse mapToResponse(Veterinario veterinario) {
        return new VeterinarioResponse(
                veterinario.getId(),
                veterinario.getNome(),
                veterinario.getRegistroProfissional(),
                veterinario.getEspecialidade());
    }
}


