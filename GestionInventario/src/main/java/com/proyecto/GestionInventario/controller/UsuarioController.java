package com.proyecto.GestionInventario.controller;

import com.proyecto.GestionInventario.domain.Usuario;
import com.proyecto.GestionInventario.service.UsuarioService;
import jakarta.validation.Valid;
import java.util.Locale;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 *
 * @author abbyc
 */
@Controller
@RequestMapping("/usuario")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final MessageSource messageSource;

    public UsuarioController(UsuarioService usuarioService, MessageSource messageSource) {
        this.usuarioService = usuarioService;
        this.messageSource = messageSource;
    }

    @GetMapping("/listado")
    public String listado(@RequestParam(required = false) Boolean activo, Model model) {
        var usuarios = usuarioService.getUsuarios(activo);
        model.addAttribute("usuarios", usuarios);
        model.addAttribute("totalUsuarios", usuarios.size());
        model.addAttribute("activoSeleccionado", activo);
        model.addAttribute("usuarioNuevo", new Usuario());
        return "/usuario/listado";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid Usuario usuario, RedirectAttributes redirectAttributes) {
        usuarioService.save(usuario);

        redirectAttributes.addFlashAttribute("todoOk",
                messageSource.getMessage("mensaje.creado", null, Locale.getDefault()));
        return "redirect:/usuario/listado";
    }

    @PostMapping("/desactivar")
    public String desactivar(@RequestParam Integer id, RedirectAttributes redirectAttributes) {
        usuarioService.toggleActivo(id);
        redirectAttributes.addFlashAttribute("todoOk",
                messageSource.getMessage("usuario.estado.cambiado", null, Locale.getDefault()));
        return "redirect:/usuario/listado";
    }

    @GetMapping("/modificar/{id}")
    public String modificar(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {

        try {
            Usuario usuario = usuarioService.getUsuario(id);
            model.addAttribute("usuario", usuario);
            return "/usuario/modificar";

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error",
                    messageSource.getMessage("usuario.error01", null, Locale.getDefault()));
            return "redirect:/usuario/listado";
        }
    }
}
