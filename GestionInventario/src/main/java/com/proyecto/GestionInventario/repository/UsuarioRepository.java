package com.proyecto.GestionInventario.repository;

import com.proyecto.GestionInventario.domain.Usuario;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author abbyc
 */
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    Optional<Usuario> findByCorreo(String correo);

    List<Usuario> findByActivo(Boolean activo);

    Optional<Usuario> findByCorreoAndActivoTrue(String correo);
}
