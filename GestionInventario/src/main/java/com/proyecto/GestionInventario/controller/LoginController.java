/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.proyecto.GestionInventario.controller;
import com.proyecto.GestionInventario.domain.Usuario;
import com.proyecto.GestionInventario.service.UsuarioService;
import java.util.Locale;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
/**
 *
 * @author abbyc
 */
@Controller
public class LoginController {

    private final UsuarioService usuarioService;
    private final MessageSource messageSource;

    public LoginController(UsuarioService usuarioService, MessageSource messageSource) {
        this.usuarioService = usuarioService;
        this.messageSource = messageSource;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @PostMapping("/login")
    public String iniciarSesion(String correo, String password, Model model) {

        Usuario usuario = usuarioService.login(correo, password);

        if (usuario == null) {

            String mensajeError = messageSource.getMessage(
                    "login.error",
                    null,
                    Locale.getDefault()
            );

            model.addAttribute("error", mensajeError);
            return "login";
        }

        if (usuario.getRol().name().equals("ADMIN")) {
            return "redirect:/admin/dashboard";
        }

        return "redirect:/inventario";
    }
}
