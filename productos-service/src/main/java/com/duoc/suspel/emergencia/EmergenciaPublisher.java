package com.duoc.suspel.emergencia;

public interface EmergenciaPublisher {

	/** Publica el aviso; lanza excepcion si el broker no lo recibe (una emergencia no se puede perder en silencio). */
	void publicar(EmergenciaEvento evento);
}
