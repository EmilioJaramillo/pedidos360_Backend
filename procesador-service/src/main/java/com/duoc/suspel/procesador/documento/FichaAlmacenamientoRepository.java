package com.duoc.suspel.procesador.documento;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FichaAlmacenamientoRepository extends JpaRepository<FichaAlmacenamiento, Long> {

	boolean existsByEventoId(String eventoId);
}
