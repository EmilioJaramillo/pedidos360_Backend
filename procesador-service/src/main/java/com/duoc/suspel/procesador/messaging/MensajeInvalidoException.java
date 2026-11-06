package com.duoc.suspel.procesador.messaging;

/** Error no recuperable: reintentar no cambia el resultado, el mensaje va directo a la DLQ. */
public class MensajeInvalidoException extends RuntimeException {

	public MensajeInvalidoException(String mensaje) {
		super(mensaje);
	}
}
