package com.duoc.suspel.procesador.notificacion;

import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {

	boolean existsByEventoId(String eventoId);
}
