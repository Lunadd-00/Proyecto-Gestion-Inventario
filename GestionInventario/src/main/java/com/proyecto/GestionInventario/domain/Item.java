package com.proyecto.GestionInventario.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import lombok.Data;

@Data
@Entity
@Table(name = "item")
public class Item implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    @NotBlank(message = "El nombre no puede estar vacío.")
    @Size(max = 150, message = "El nombre no puede tener más de 150 caracteres.")
    private String nombre;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "unidad_medida", nullable = false, length = 50)
    @NotBlank(message = "La unidad de medida no puede estar vacía.")
    @Size(max = 50)
    private String unidadMedida;

    @Column(nullable = false)
    private Integer stock = 0;

    @Column(name = "stock_minimo", nullable = false)
    @NotNull(message = "El stock mínimo no puede estar vacío.")
    @Min(value = 0, message = "El stock mínimo debe ser mayor o igual a 0.")
    private Integer stockMinimo;

    @Column(name = "tiene_caducidad", nullable = false)
    private boolean tieneCaducidad;

    @Column(name = "fecha_caducidad")
    private LocalDate fechaCaducidad;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDate fechaCreacion;

    @Column(nullable = false)
    private boolean activo = true;

    @ManyToOne
    @JoinColumn(name = "categoria_id")
    @NotNull(message = "Debe seleccionar una categoría.")
    private Categoria categoria;

    @ManyToOne
    @JoinColumn(name = "proveedor_id")
    @NotNull(message = "Debe seleccionar un proveedor.")
    private Proveedor proveedor;

    @ManyToOne
    @JoinColumn(name = "bodega_id")
    private Bodega bodega;

    @PrePersist
    public void prePersist() {
        this.fechaCreacion = LocalDate.now();
        if (!this.tieneCaducidad) {
            this.fechaCaducidad = null;
        }
    }

    @PreUpdate
    public void preUpdate() {
        if (!this.tieneCaducidad) {
            this.fechaCaducidad = null;
        }
    }
}