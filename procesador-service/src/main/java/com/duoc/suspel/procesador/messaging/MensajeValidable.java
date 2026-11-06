package com.duoc.suspel.procesador.messaging;

/** Mensaje que sabe verificar sus campos obligatorios antes de procesarse. */
public interface MensajeValidable {

	/** @throws MensajeInvalidoException si falta un dato obligatorio */
	void validar();
}
