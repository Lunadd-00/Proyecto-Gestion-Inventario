package com.proyecto.GestionInventario.service;

import com.proyecto.GestionInventario.domain.Bodega;
import com.proyecto.GestionInventario.repository.BodegaRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BodegaService {

    private final BodegaRepository bodegaRepository;

    public BodegaService(BodegaRepository bodegaRepository) {
        this.bodegaRepository = bodegaRepository;
    }

    @Transactional(readOnly = true)
    public List<Bodega> getBodegas() {
        return bodegaRepository.findAll();
    }
}
