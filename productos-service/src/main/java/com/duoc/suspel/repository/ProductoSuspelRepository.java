package com.duoc.suspel.repository;

import com.duoc.suspel.entity.ProductoSuspel;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductoSuspelRepository extends JpaRepository<ProductoSuspel, Long> {

	List<ProductoSuspel> findByBodegaOrderByNombre(String bodega);
}
