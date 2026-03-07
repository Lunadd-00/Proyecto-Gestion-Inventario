package com.proyecto.GestionInventario.service;

import com.proyecto.GestionInventario.domain.Proveedor;
import com.proyecto.GestionInventario.repository.ProveedorRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author lunad
 */
@Service
public class ProveedorService {

    private final ProveedorRepository proveedorRepository;

    public ProveedorService(ProveedorRepository proveedorRepository) {
        this.proveedorRepository = proveedorRepository;
    }

    @Transactional(readOnly = true)
    public List<Proveedor> getProveedores() {
        return proveedorRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Proveedor> getProveedor(Integer id) {
        return proveedorRepository.findById(id);
    }

    @Transactional
    public void save(Proveedor proveedor) {
        proveedorRepository.save(proveedor);
    }

    @Transactional
    public void delete(Integer id) {

        if (!proveedorRepository.existsById(id)) {
            throw new IllegalArgumentException("El proveedor con ID " + id + " no existe.");
        }

        try {
            proveedorRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("No se puede eliminar el proveedor porque tiene datos asociados.", e);
        }
    }

}
