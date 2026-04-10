package com.proyecto.GestionInventario.repository;

import com.proyecto.GestionInventario.domain.Ruta;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 *
 * @author abbyc
 */
public interface RutaRepository extends JpaRepository<Ruta, Integer> {

    List<Ruta> findAllByOrderByRequiereRolAsc();

}
