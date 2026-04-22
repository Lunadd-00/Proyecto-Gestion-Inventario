package com.proyecto.GestionInventario.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;
import lombok.Data;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

@Data
@Entity
@Table(name = "lote")
public class Lote implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_lote", length = 100, unique = true)
    private String numeroLote;

    @Column(nullable = false)
    @NotNull(message = "La cantidad no puede estar vacía.")
    @Min(value = 1, message = "La cantidad debe ser mayor a 0.")
    private Integer cantidad;

    @Column(name = "fecha_ingreso", nullable = false)
    private LocalDate fechaIngreso;

    @Column(name = "fecha_caducidad")
    private LocalDate fechaCaducidad;

    @Column(nullable = false)
    private boolean activo = true;

    @ManyToOne
    @JoinColumn(name = "item_id", nullable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    private Item item;

    @ManyToOne
    @JoinColumn(name = "bodega_id")
    @NotNull(message = "Debe seleccionar una bodega.")
    private Bodega bodega;

    @PrePersist
    public void prePersist() {
        this.fechaIngreso = LocalDate.now();
    }
}
