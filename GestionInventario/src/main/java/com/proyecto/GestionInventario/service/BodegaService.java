package com.proyecto.GestionInventario.service;

import com.proyecto.GestionInventario.domain.Bodega;
import com.proyecto.GestionInventario.repository.BodegaRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.DataIntegrityViolationException;
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

    @Transactional(readOnly = true)
    public Optional<Bodega> getBodega(Long id) {
        return bodegaRepository.findById(id);
    }

    @Transactional
    public void save(Bodega bodega) {
        bodegaRepository.save(bodega);
    }

    @Transactional
    public void delete(Long id) {
        Bodega bodega = bodegaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Bodega no encontrada."));
        try {
            bodegaRepository.delete(bodega);
            bodegaRepository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException(
                    "No se puede eliminar la bodega porque tiene lotes o movimientos asociados.");
        }
    }
}
