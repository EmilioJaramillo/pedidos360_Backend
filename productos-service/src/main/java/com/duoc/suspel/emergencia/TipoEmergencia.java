package com.duoc.suspel.emergencia;

/** Tipo de emergencia y su prioridad en la cola (0-9, mayor se atiende primero). */
public enum TipoEmergencia {
	INCENDIO(9),
	FUGA_GAS(7),
	DERRAME(5);

	private final int prioridad;

	TipoEmergencia(int prioridad) {
		this.prioridad = prioridad;
	}

	public int prioridad() {
		return prioridad;
	}
}
