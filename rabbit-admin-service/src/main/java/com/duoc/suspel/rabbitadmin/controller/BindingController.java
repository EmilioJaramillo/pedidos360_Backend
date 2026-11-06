package com.duoc.suspel.rabbitadmin.controller;

import com.duoc.suspel.rabbitadmin.dto.BindingResponse;
import com.duoc.suspel.rabbitadmin.dto.CrearBindingRequest;
import com.duoc.suspel.rabbitadmin.dto.ReglasValidacion;
import com.duoc.suspel.rabbitadmin.service.RabbitAdminService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/bindings")
public class BindingController {

	private final RabbitAdminService service;

	public BindingController(RabbitAdminService service) {
		this.service = service;
	}

	@GetMapping
	public List<BindingResponse> listar() {
		return service.listarBindings();
	}

	@PostMapping
	public ResponseEntity<BindingResponse> crear(@Valid @RequestBody CrearBindingRequest request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(service.crearBinding(request));
	}

	@DeleteMapping
	public ResponseEntity<Void> eliminar(
			@RequestParam @Pattern(regexp = ReglasValidacion.NOMBRE, message = ReglasValidacion.NOMBRE_MENSAJE) String exchange,
			@RequestParam @Pattern(regexp = ReglasValidacion.NOMBRE, message = ReglasValidacion.NOMBRE_MENSAJE) String cola,
			@RequestParam(defaultValue = "") @Pattern(regexp = ReglasValidacion.ROUTING_KEY,
					message = ReglasValidacion.ROUTING_KEY_MENSAJE) String routingKey) {
		service.eliminarBinding(exchange, cola, routingKey);
		return ResponseEntity.noContent().build();
	}
}
