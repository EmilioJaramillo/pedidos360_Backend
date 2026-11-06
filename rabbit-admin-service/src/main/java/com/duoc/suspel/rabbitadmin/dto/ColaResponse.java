package com.duoc.suspel.rabbitadmin.dto;

import java.util.Map;

public record ColaResponse(String nombre, boolean durable, int mensajes, int consumidores, Map<String, Object> argumentos) {
}
