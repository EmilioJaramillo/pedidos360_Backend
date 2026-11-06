package com.duoc.suspel.service;

import com.duoc.suspel.entity.ProductoSuspel;
import com.duoc.suspel.messaging.InventarioEventPublisher;
import com.duoc.suspel.messaging.TipoEvento;
import com.duoc.suspel.repository.ProductoSuspelRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ProductoSuspelServiceImpl implements ProductoSuspelService {

	private final ProductoSuspelRepository repository;
	private final InventarioEventPublisher eventos;
	private final int umbralStockBajo;

	public ProductoSuspelServiceImpl(ProductoSuspelRepository repository, InventarioEventPublisher eventos,
			@Value("${suspel.inventario.umbral-stock-bajo}") int umbralStockBajo) {
		this.repository = repository;
		this.eventos = eventos;
		this.umbralStockBajo = umbralStockBajo;
	}

	@Override
	public List<ProductoSuspel> listar() {
		return repository.findAll();
	}

	@Override
	public Optional<ProductoSuspel> buscarPorId(Long id) {
		return repository.findById(id);
	}

	@Override
	public ProductoSuspel guardar(ProductoSuspel producto) {
		producto.setId(null);
		ProductoSuspel guardado = repository.save(producto);
		eventos.publicar(TipoEvento.PRODUCTO_CREADO, guardado);
		eventos.publicar(TipoEvento.FICHA_SOLICITADA, guardado);
		verificarStock(guardado);
		return guardado;
	}

	@Override
	public Optional<ProductoSuspel> actualizar(Long id, ProductoSuspel producto) {
		return repository.findById(id).map(existente -> {
			existente.setNombre(producto.getNombre());
			existente.setImo(producto.getImo());
			existente.setBodega(producto.getBodega());
			existente.setCantidad(producto.getCantidad());
			ProductoSuspel actualizado = repository.save(existente);
			eventos.publicar(TipoEvento.PRODUCTO_ACTUALIZADO, actualizado);
			verificarStock(actualizado);
			return actualizado;
		});
	}

	@Override
	public boolean eliminar(Long id) {
		Optional<ProductoSuspel> existente = repository.findById(id);
		if (existente.isEmpty()) {
			return false;
		}
		repository.deleteById(id);
		eventos.publicar(TipoEvento.PRODUCTO_ELIMINADO, existente.get());
		return true;
	}

	private void verificarStock(ProductoSuspel producto) {
		if (producto.getCantidad() != null && producto.getCantidad() < umbralStockBajo) {
			eventos.publicar(TipoEvento.STOCK_BAJO, producto);
		}
	}
}
