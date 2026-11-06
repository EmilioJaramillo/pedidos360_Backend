package com.duoc.suspel.rabbitadmin.dto;

public final class ReglasValidacion {

	public static final String NOMBRE = "^(?!amq\\.)[a-z0-9][a-z0-9._-]{2,63}$";

	public static final String NOMBRE_MENSAJE =
			"debe tener entre 3 y 64 caracteres (minusculas, numeros, '.', '_' o '-') y no comenzar con 'amq.'";

	public static final String ROUTING_KEY = "^[a-zA-Z0-9._*#-]{0,255}$";

	public static final String ROUTING_KEY_MENSAJE =
			"solo admite letras, numeros, '.', '_', '-', '*' y '#' (maximo 255 caracteres)";

	private ReglasValidacion() {
	}
}
