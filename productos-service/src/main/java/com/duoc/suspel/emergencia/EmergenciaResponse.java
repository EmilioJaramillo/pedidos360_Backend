package com.duoc.suspel.emergencia;

public record EmergenciaResponse(String eventoId, String bodega, TipoEmergencia tipo, int prioridad, int sustancias,
		String mensaje) {
}
