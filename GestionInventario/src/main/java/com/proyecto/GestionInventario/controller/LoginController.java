package com.proyecto.GestionInventario.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 *
 * @author abbyc
 */
@Controller
public class LoginController {

    @GetMapping({"/", "/login"})
    public String loginPage() {
        return "login";
    }

    @GetMapping("/index")
    public String homePage() {
        return "dashboard/index";
    }

    @GetMapping("/acceso_denegado")
    public String accesoDenegado() {
        return "error/acceso_denegado";
    }
}
