package br.edu.catolica.consulta_agendamento.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.edu.catolica.consulta_agendamento.model.Consulta;
import br.edu.catolica.consulta_agendamento.model.StatusConsulta;

public interface ConsultaRepository extends JpaRepository<Consulta, Long> {

    Optional<Consulta> findByVeterinario_IdAndDataHora(Long veterinarioId, LocalDateTime dataHora);

    @Query("select c from Consulta c where c.veterinario.id = :veterinarioId and c.dataHora between :inicio and :fim")
    List<Consulta> findAllWithinPeriod(@Param("veterinarioId") Long veterinarioId,
            @Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim);

    List<Consulta> findByStatusAndDataHoraBefore(StatusConsulta status, LocalDateTime dataHora);

    List<Consulta> findByVeterinario_IdOrderByDataHoraAsc(Long veterinarioId);
}
