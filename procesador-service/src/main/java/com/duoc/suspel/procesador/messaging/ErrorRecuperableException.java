package com.duoc.suspel.procesador.messaging;

/** Error transitorio (dependencia no disponible): el mensaje se reintenta hasta max-intentos. */
public class ErrorRecuperableException extends RuntimeException {

	public ErrorRecuperableException(String mensaje) {
		super(mensaje);
	}
}
