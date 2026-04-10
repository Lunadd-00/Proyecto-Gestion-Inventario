package com.proyecto.GestionInventario.service;

import com.proyecto.GestionInventario.domain.Usuario;
import com.proyecto.GestionInventario.repository.UsuarioRepository;
import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author abbyc
 */
@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<Usuario> getUsuarios(Boolean activo) {
        if (activo == null) {
            return usuarioRepository.findAll();
        }
        return usuarioRepository.findByActivo(activo);
    }

    @Transactional(readOnly = true)
    public Usuario getUsuario(Integer idUsuario) {
        return usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
    }

    @Transactional
    public void save(Usuario usuario) {

        if (!usuario.getPassword().startsWith("$2a$")) {
            usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        }

        usuarioRepository.save(usuario);
    }

    @Transactional
    public void toggleActivo(Integer idUsuario) {
        Usuario usuario = getUsuario(idUsuario);

        usuario.setActivo(!usuario.getActivo());

        usuarioRepository.save(usuario);
    }

    @Transactional(readOnly = true)
    public Usuario login(String correo, String password) {

        Usuario usuario = usuarioRepository
                .findByCorreoAndActivoTrue(correo)
                .orElse(null);

        if (usuario != null
                && passwordEncoder.matches(password, usuario.getPassword())) {
            return usuario;
        }
        return null;
    }
}
