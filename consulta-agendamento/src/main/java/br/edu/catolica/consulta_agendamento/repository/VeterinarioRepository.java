package br.edu.catolica.consulta_agendamento.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.edu.catolica.consulta_agendamento.model.Veterinario;

public interface VeterinarioRepository extends JpaRepository<Veterinario, Long> {

    Optional<Veterinario> findByRegistroProfissional(String registroProfissional);
}
