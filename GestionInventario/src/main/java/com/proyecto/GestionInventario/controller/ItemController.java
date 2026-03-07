package com.proyecto.GestionInventario.controller;

import com.proyecto.GestionInventario.domain.Item;
import com.proyecto.GestionInventario.service.CategoriaService;
import com.proyecto.GestionInventario.service.ItemService;
import com.proyecto.GestionInventario.service.ProveedorService;
import jakarta.validation.Valid;
import java.util.Locale;
import java.util.Optional;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/item")
public class ItemController {

    private final ItemService itemService;
    private final CategoriaService categoriaService;
    private final ProveedorService proveedorService;
    private final MessageSource messageSource;

    public ItemController(ItemService itemService,
                          CategoriaService categoriaService,
                          ProveedorService proveedorService,
                          MessageSource messageSource) {
        this.itemService = itemService;
        this.categoriaService = categoriaService;
        this.proveedorService = proveedorService;
        this.messageSource = messageSource;
    }

    private void cargarListas(Model model) {
        model.addAttribute("categorias", categoriaService.getCategorias());
        model.addAttribute("proveedores", proveedorService.getProveedores());
    }

    @GetMapping("/listado")
    public String listado(
            @RequestParam(required = false) String idCategoria,
            @RequestParam(required = false) String idProveedor,
            @RequestParam(required = false) String activo,
            Model model) {

        Long catId = (idCategoria != null && !idCategoria.isEmpty()) ? Long.parseLong(idCategoria) : null;
        Integer provId = (idProveedor != null && !idProveedor.isEmpty()) ? Integer.parseInt(idProveedor) : null;
        Boolean activoBool = (activo != null && !activo.isEmpty()) ? Boolean.parseBoolean(activo) : null;

        var items = itemService.filtrar(catId, provId, activoBool);
        model.addAttribute("items", items);
        model.addAttribute("totalItems", items.size());
        model.addAttribute("idCategoriaSeleccionada", catId);
        model.addAttribute("idProveedorSeleccionado", provId);
        model.addAttribute("activoSeleccionado", activoBool);

        cargarListas(model);
        return "/item/listado";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("item", new Item());
        cargarListas(model);
        return "/item/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid Item item, BindingResult result,
                          Model model, RedirectAttributes redirectAttributes) {

        if (item.isTieneCaducidad() && item.getFechaCaducidad() == null) {
            result.rejectValue("fechaCaducidad", "error",
                    "La fecha de caducidad es obligatoria cuando el ítem posee caducidad.");
        }

        if (result.hasErrors()) {
            cargarListas(model);
            return "/item/formulario";
        }

        itemService.save(item);
        redirectAttributes.addFlashAttribute(
                "todoOk",
                messageSource.getMessage("mensaje.actualizado", null, Locale.getDefault())
        );
        return "redirect:/item/listado";
    }

    @GetMapping("/modificar/{id}")
    public String modificar(@PathVariable Long id, Model model,
                            RedirectAttributes redirectAttributes) {

        Optional<Item> itemOpt = itemService.getItem(id);

        if (itemOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "El ítem no existe.");
            return "redirect:/item/listado";
        }

        model.addAttribute("item", itemOpt.get());
        cargarListas(model);
        return "/item/formulario";
    }

    
    @PostMapping("/eliminar")
    public String eliminar(@RequestParam Long idItem, RedirectAttributes redirectAttributes) {

        String titulo = "todoOk";
        String detalle = "mensaje.eliminado";

        try {
            itemService.delete(idItem);
        } catch (IllegalArgumentException e) {
            titulo = "error";
            detalle = "item.error01";
        } catch (IllegalStateException e) {
            titulo = "error";
            detalle = "item.error02";
        } catch (Exception e) {
            titulo = "error";
            detalle = "item.error03";
        }

        redirectAttributes.addFlashAttribute(
                titulo,
                messageSource.getMessage(detalle, null, Locale.getDefault())
        );
        return "redirect:/item/listado";
    }
}