package com.proyecto.GestionInventario.controller;

import com.proyecto.GestionInventario.service.DashboardService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping
    public String verDashboard(Model model) {

        model.addAttribute("stockData", dashboardService.obtenerStockPorProducto());
        model.addAttribute("vencimientosData", dashboardService.obtenerEstadoVencimientos());
        model.addAttribute("movimientosData", dashboardService.obtenerMovimientos());

        model.addAttribute("metricas", dashboardService.obtenerMetricas());
        model.addAttribute("lotesPorVencer", dashboardService.obtenerLotesPorVencer());
        model.addAttribute("stockBajo", dashboardService.obtenerStockBajo());

        return "dashboard/index";
    }
}
