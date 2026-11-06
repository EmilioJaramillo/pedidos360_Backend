package com.duoc.suspel.procesador.auditoria;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.Instant;

@Entity
public class RegistroAuditoria {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(unique = true, nullable = false)
	private String eventoId;

	private String tipo;
	private Long productoId;
	private String producto;
	private String usuario;
	private Instant fecha;

	protected RegistroAuditoria() {
	}

	public RegistroAuditoria(String eventoId, String tipo, Long productoId, String producto, String usuario,
			Instant fecha) {
		this.eventoId = eventoId;
		this.tipo = tipo;
		this.productoId = productoId;
		this.producto = producto;
		this.usuario = usuario;
		this.fecha = fecha;
	}

	public Long getId() {
		return id;
	}

	public String getEventoId() {
		return eventoId;
	}

	public String getTipo() {
		return tipo;
	}

	public Long getProductoId() {
		return productoId;
	}

	public String getProducto() {
		return producto;
	}

	public String getUsuario() {
		return usuario;
	}

	public Instant getFecha() {
		return fecha;
	}
}
