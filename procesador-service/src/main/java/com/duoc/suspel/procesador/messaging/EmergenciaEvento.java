package com.duoc.suspel.procesador.messaging;

import java.time.Instant;
import java.util.List;

public record EmergenciaEvento(
		String eventoId,
		String tipo,
		int prioridad,
		String bodega,
		String detalle,
		List<Sustancia> sustancias,
		String usuario,
		Instant fecha) implements MensajeValidable {

	public record Sustancia(String nombre, String imo, Integer cantidad) {
	}

	@Override
	public void validar() {
		if (eventoId == null || tipo == null || tipo.isBlank() || bodega == null || bodega.isBlank()) {
			throw new MensajeInvalidoException("faltan campos obligatorios (eventoId, tipo, bodega)");
		}
	}
}
