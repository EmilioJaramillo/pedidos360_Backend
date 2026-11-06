package com.duoc.suspel.service;

import com.duoc.suspel.entity.ProductoSuspel;
import com.duoc.suspel.repository.ProductoSuspelRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class ProductoSuspelServiceImpl implements ProductoSuspelService {

	private final ProductoSuspelRepository repository;

	public ProductoSuspelServiceImpl(ProductoSuspelRepository repository) {
		this.repository = repository;
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
		return repository.save(producto);
	}

	@Override
	public Optional<ProductoSuspel> actualizar(Long id, ProductoSuspel producto) {
		return repository.findById(id).map(existente -> {
			existente.setNombre(producto.getNombre());
			existente.setImo(producto.getImo());
			existente.setBodega(producto.getBodega());
			existente.setCantidad(producto.getCantidad());
			return repository.save(existente);
		});
	}

	@Override
	public boolean eliminar(Long id) {
		if (!repository.existsById(id)) {
			return false;
		}
		repository.deleteById(id);
		return true;
	}
}
