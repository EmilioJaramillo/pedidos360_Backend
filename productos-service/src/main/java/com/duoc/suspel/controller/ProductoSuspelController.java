package com.duoc.suspel.controller;

import com.duoc.suspel.entity.ProductoSuspel;
import com.duoc.suspel.service.ProductoSuspelService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/productos")
public class ProductoSuspelController {

	private final ProductoSuspelService service;

	public ProductoSuspelController(ProductoSuspelService service) {
		this.service = service;
	}

	@GetMapping
	public List<ProductoSuspel> listar() {
		return service.listar();
	}

	@GetMapping("/{id}")
	public ResponseEntity<ProductoSuspel> obtener(@PathVariable Long id) {
		return service.buscarPorId(id)
				.map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}

	@PostMapping
	public ResponseEntity<ProductoSuspel> crear(@RequestBody ProductoSuspel producto) {
		return ResponseEntity.status(HttpStatus.CREATED).body(service.guardar(producto));
	}

	@PutMapping("/{id}")
	public ResponseEntity<ProductoSuspel> editar(@PathVariable Long id, @RequestBody ProductoSuspel producto) {
		return service.actualizar(id, producto)
				.map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> eliminar(@PathVariable Long id) {
		if (service.eliminar(id)) {
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.notFound().build();
	}
}
