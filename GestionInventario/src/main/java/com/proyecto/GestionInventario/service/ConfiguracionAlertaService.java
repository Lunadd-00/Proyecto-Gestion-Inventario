package com.proyecto.GestionInventario.service;

import com.proyecto.GestionInventario.domain.ConfiguracionAlerta;
import com.proyecto.GestionInventario.repository.ConfiguracionAlertaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ConfiguracionAlertaService {

    private final ConfiguracionAlertaRepository repository;

    public ConfiguracionAlertaService(ConfiguracionAlertaRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public ConfiguracionAlerta getConfiguracion() {
        return repository.findById(1L).orElseGet(ConfiguracionAlerta::new);
    }

    @Transactional
    public void guardar(ConfiguracionAlerta config) {
        config.setId(1L);
        repository.save(config);
    }
}
