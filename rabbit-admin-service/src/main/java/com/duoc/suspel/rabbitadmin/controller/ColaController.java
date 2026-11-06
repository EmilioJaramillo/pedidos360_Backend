package com.duoc.suspel.rabbitadmin.controller;

import com.duoc.suspel.rabbitadmin.dto.ColaResponse;
import com.duoc.suspel.rabbitadmin.dto.CrearColaRequest;
import com.duoc.suspel.rabbitadmin.dto.PurgaResponse;
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
@RequestMapping("/api/admin/queues")
public class ColaController {

	private final RabbitAdminService service;

	public ColaController(RabbitAdminService service) {
		this.service = service;
	}

	@GetMapping
	public List<ColaResponse> listar() {
		return service.listarColas();
	}

	@GetMapping("/{nombre}")
	public ColaResponse obtener(
			@PathVariable @Pattern(regexp = ReglasValidacion.NOMBRE, message = ReglasValidacion.NOMBRE_MENSAJE) String nombre) {
		return service.obtenerCola(nombre);
	}

	@PostMapping
	public ResponseEntity<ColaResponse> crear(@Valid @RequestBody CrearColaRequest request) {
		ColaResponse cola = service.crearCola(request);
		return ResponseEntity.created(URI.create("/api/admin/queues/" + cola.nombre())).body(cola);
	}

	@DeleteMapping("/{nombre}")
	public ResponseEntity<Void> eliminar(
			@PathVariable @Pattern(regexp = ReglasValidacion.NOMBRE, message = ReglasValidacion.NOMBRE_MENSAJE) String nombre) {
		service.eliminarCola(nombre);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/{nombre}/messages")
	public PurgaResponse purgar(
			@PathVariable @Pattern(regexp = ReglasValidacion.NOMBRE, message = ReglasValidacion.NOMBRE_MENSAJE) String nombre) {
		return service.purgarCola(nombre);
	}
}
