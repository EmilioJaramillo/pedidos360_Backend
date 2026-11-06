package com.duoc.suspel.rabbitadmin.controller;

import com.duoc.suspel.rabbitadmin.dto.CrearExchangeRequest;
import com.duoc.suspel.rabbitadmin.dto.ExchangeResponse;
import com.duoc.suspel.rabbitadmin.dto.ReglasValidacion;
import com.duoc.suspel.rabbitadmin.service.RabbitAdminService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/exchanges")
public class ExchangeController {

	private final RabbitAdminService service;

	public ExchangeController(RabbitAdminService service) {
		this.service = service;
	}

	@GetMapping
	public List<ExchangeResponse> listar() {
		return service.listarExchanges();
	}

	@PostMapping
	public ResponseEntity<ExchangeResponse> crear(@Valid @RequestBody CrearExchangeRequest request) {
		ExchangeResponse exchange = service.crearExchange(request);
		return ResponseEntity.created(URI.create("/api/admin/exchanges/" + exchange.nombre())).body(exchange);
	}

	@DeleteMapping("/{nombre}")
	public ResponseEntity<Void> eliminar(
			@PathVariable @Pattern(regexp = ReglasValidacion.NOMBRE, message = ReglasValidacion.NOMBRE_MENSAJE) String nombre) {
		service.eliminarExchange(nombre);
		return ResponseEntity.noContent().build();
	}
}
