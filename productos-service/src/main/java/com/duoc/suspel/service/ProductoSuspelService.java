package com.duoc.suspel.service;

import com.duoc.suspel.entity.ProductoSuspel;
import java.util.List;
import java.util.Optional;

public interface ProductoSuspelService {

	List<ProductoSuspel> listar();

	Optional<ProductoSuspel> buscarPorId(Long id);

	ProductoSuspel guardar(ProductoSuspel producto);

	Optional<ProductoSuspel> actualizar(Long id, ProductoSuspel producto);

	boolean eliminar(Long id);
}
