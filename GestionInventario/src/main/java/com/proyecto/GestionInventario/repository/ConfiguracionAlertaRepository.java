package com.proyecto.GestionInventario.repository;

import com.proyecto.GestionInventario.domain.ConfiguracionAlerta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfiguracionAlertaRepository extends JpaRepository<ConfiguracionAlerta, Long> {
}
