package com.duoc.suspel.procesador.notificacion;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.Instant;

@Entity
public class Notificacion {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(unique = true, nullable = false)
	private String eventoId;

	private Long productoId;
	private String producto;
	private String bodega;
	private Integer cantidad;
	private String mensaje;
	private Instant fecha;

	protected Notificacion() {
	}

	public Notificacion(String eventoId, Long productoId, String producto, String bodega, Integer cantidad,
			String mensaje, Instant fecha) {
		this.eventoId = eventoId;
		this.productoId = productoId;
		this.producto = producto;
		this.bodega = bodega;
		this.cantidad = cantidad;
		this.mensaje = mensaje;
		this.fecha = fecha;
	}

	public Long getId() {
		return id;
	}

	public String getEventoId() {
		return eventoId;
	}

	public Long getProductoId() {
		return productoId;
	}

	public String getProducto() {
		return producto;
	}

	public String getBodega() {
		return bodega;
	}

	public Integer getCantidad() {
		return cantidad;
	}

	public String getMensaje() {
		return mensaje;
	}

	public Instant getFecha() {
		return fecha;
	}
}
