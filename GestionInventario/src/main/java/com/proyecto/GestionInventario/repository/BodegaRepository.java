package com.proyecto.GestionInventario.repository;

import com.proyecto.GestionInventario.domain.Bodega;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BodegaRepository extends JpaRepository<Bodega, Long> {
}
