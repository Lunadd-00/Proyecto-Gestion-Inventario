package com.proyecto.GestionInventario.controller;

import com.proyecto.GestionInventario.domain.Usuario;
import com.proyecto.GestionInventario.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
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
            HttpSession session,
            Model model) {

        Usuario usuario = usuarioService.login(correo, password);

        if (usuario != null) {
            session.setAttribute("usuarioLogueado", usuario);
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
