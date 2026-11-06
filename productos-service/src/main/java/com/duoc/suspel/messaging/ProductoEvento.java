package com.duoc.suspel.messaging;

import java.time.Instant;

public record ProductoEvento(
		String eventoId,
		TipoEvento tipo,
		Long productoId,
		String nombre,
		String imo,
		String bodega,
		Integer cantidad,
		String usuario,
		Instant fecha) {
}
