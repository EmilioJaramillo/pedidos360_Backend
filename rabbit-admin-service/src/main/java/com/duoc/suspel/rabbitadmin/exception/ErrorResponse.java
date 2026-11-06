package com.duoc.suspel.rabbitadmin.exception;

import java.time.Instant;
import java.util.Map;

public record ErrorResponse(int status, String error, String mensaje, Map<String, String> campos, Instant fecha) {

	static ErrorResponse de(int status, String error, String mensaje) {
		return new ErrorResponse(status, error, mensaje, Map.of(), Instant.now());
	}

	static ErrorResponse de(int status, String error, String mensaje, Map<String, String> campos) {
		return new ErrorResponse(status, error, mensaje, campos, Instant.now());
	}
}
