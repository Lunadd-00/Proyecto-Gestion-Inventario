package com.proyecto.GestionInventario.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.io.Serializable;
import lombok.Data;

@Data
@Entity
@Table(name = "configuracion_alerta")
public class ConfiguracionAlerta implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private Long id = 1L;

    @Column(name = "hora_vencimiento", nullable = false)
    @Min(0) @Max(23)
    private int horaVencimiento = 8;

    @Column(name = "minuto_vencimiento", nullable = false)
    @Min(0) @Max(59)
    private int minutoVencimiento = 0;

    @Column(name = "hora_stock", nullable = false)
    @Min(0) @Max(23)
    private int horaStock = 8;

    @Column(name = "minuto_stock", nullable = false)
    @Min(0) @Max(59)
    private int minutoStock = 5;
}
