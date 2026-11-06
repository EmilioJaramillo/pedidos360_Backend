package com.duoc.suspel.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

/** Usuario de la peticion en curso segun el JWT de Cognito (claim "username"). */
public final class UsuarioActual {

	private static final String SIN_SESION = "sistema";

	private UsuarioActual() {
	}

	public static String nombre() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth instanceof JwtAuthenticationToken jwt) {
			String username = jwt.getToken().getClaimAsString("username");
			return username != null ? username : jwt.getName();
		}
		return SIN_SESION;
	}
}
