package com.proyecto.GestionInventario.controller;

import com.proyecto.GestionInventario.domain.Usuario;
import com.proyecto.GestionInventario.service.UsuarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author abbyc
 */
@Controller
public class LoginController {

    private final UsuarioService usuarioService;

    public LoginController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping({"/", "/login"})
    public String loginPage() {
        return "login"; 
    }

    @PostMapping("/procesar-login")
    public String procesarLogin(@RequestParam String correo,
            @RequestParam String password,
            Model model) {

        Usuario usuario = usuarioService.login(correo, password);

        if (usuario != null) {
            return "redirect:/index";
        } else {
            model.addAttribute("error", "Credenciales incorrectas o usuario inactivo");
            return "login";
        }
    }

    @GetMapping("/index")
    public String homePage() {
        return "dashboard/index"; 
    }
}
