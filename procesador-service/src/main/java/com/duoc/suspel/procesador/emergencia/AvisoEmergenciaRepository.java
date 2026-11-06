package com.duoc.suspel.procesador.emergencia;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AvisoEmergenciaRepository extends JpaRepository<AvisoEmergencia, Long> {

	boolean existsByEventoId(String eventoId);
}
