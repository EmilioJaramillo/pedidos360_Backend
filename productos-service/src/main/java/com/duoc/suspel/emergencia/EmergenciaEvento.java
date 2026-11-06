package com.duoc.suspel.emergencia;

import java.time.Instant;
import java.util.List;

/** Aviso de emergencia con las sustancias peligrosas presentes en la bodega afectada. */
public record EmergenciaEvento(
		String eventoId,
		TipoEmergencia tipo,
		int prioridad,
		String bodega,
		String detalle,
		List<Sustancia> sustancias,
		String usuario,
		Instant fecha) {

	public record Sustancia(String nombre, String imo, Integer cantidad) {
	}
}
