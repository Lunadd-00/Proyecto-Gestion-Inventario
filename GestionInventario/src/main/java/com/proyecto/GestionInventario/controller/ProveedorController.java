package com.proyecto.GestionInventario.controller;

import com.proyecto.GestionInventario.domain.Proveedor;
import com.proyecto.GestionInventario.service.ProveedorService;
import jakarta.validation.Valid;
import java.util.Locale;
import java.util.Optional;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 *
 * @author lunad
 */
@Controller
@RequestMapping("/proveedor")
public class ProveedorController {

    private final ProveedorService proveedorService;
    private final MessageSource messageSource;

    public ProveedorController(ProveedorService proveedorService, MessageSource messageSource) {
        this.proveedorService = proveedorService;
        this.messageSource = messageSource;
    }

    @GetMapping("/listado")
    public String listado(Model model) {
        var proveedores = proveedorService.getProveedores();
        model.addAttribute("proveedores", proveedores);
        model.addAttribute("totalProveedores", proveedores.size());
        return "/proveedor/listado";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid Proveedor proveedor, RedirectAttributes redirectAttributes) {
        proveedorService.save(proveedor);
        redirectAttributes.addFlashAttribute(
                "todoOk",
                messageSource.getMessage("mensaje.actualizado", null, Locale.getDefault())
        );
        return "redirect:/proveedor/listado";
    }

    @PostMapping("/eliminar")
    public String eliminar(@RequestParam Integer id, RedirectAttributes redirectAttributes) {

        String titulo = "todoOk";
        String detalle = "mensaje.eliminado";

        try {
            proveedorService.delete(id);
        } catch (IllegalArgumentException e) {
            titulo = "error";
            detalle = "proveedor.error01";
        } catch (IllegalStateException e) {
            titulo = "error";
            detalle = "proveedor.error02";
        } catch (Exception e) {
            titulo = "error";
            detalle = "proveedor.error03";
        }
        redirectAttributes.addFlashAttribute(
                titulo,
                messageSource.getMessage(detalle, null, Locale.getDefault())
        );
        return "redirect:/proveedor/listado";
    }

    @GetMapping("/modificar/{id}")
    public String modificar(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {

        Optional<Proveedor> proveedorOpt = proveedorService.getProveedor(id);

        if (proveedorOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute(
                    "error",
                    messageSource.getMessage("proveedor.error01", null, Locale.getDefault())
            );
            return "redirect:/proveedor/listado";
        }
        model.addAttribute("proveedor", proveedorOpt.get());
        return "/proveedor/modificar";
    }
}
