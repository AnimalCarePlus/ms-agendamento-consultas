package br.edu.catolica.consulta_agendamento.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.catolica.consulta_agendamento.exception.NotFoundException;
import br.edu.catolica.consulta_agendamento.model.Veterinario;
import br.edu.catolica.consulta_agendamento.repository.VeterinarioRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class VeterinarioService {

    private final VeterinarioRepository veterinarioRepository;

    public Veterinario salvar(Veterinario veterinario) {
        return veterinarioRepository.save(veterinario);
    }

    @Transactional(readOnly = true)
    public Veterinario buscarPorId(Long id) {
        return veterinarioRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Veterinario nao encontrado"));
    }

    @Transactional(readOnly = true)
    public List<Veterinario> listarTodos() {
        return veterinarioRepository.findAll();
    }

    public void remover(Long id) {
        Veterinario existente = buscarPorId(id);
        veterinarioRepository.delete(existente);
    }
}
