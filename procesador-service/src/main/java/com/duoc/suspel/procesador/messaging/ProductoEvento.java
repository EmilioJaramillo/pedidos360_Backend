package com.duoc.suspel.procesador.messaging;

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
		Instant fecha) implements MensajeValidable {

	@Override
	public void validar() {
		if (eventoId == null || tipo == null || productoId == null || nombre == null || nombre.isBlank()) {
			throw new MensajeInvalidoException("faltan campos obligatorios (eventoId, tipo, productoId, nombre)");
		}
	}
}
