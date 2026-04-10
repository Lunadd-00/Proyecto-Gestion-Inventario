package com.proyecto.GestionInventario.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

/**
 *
 * @author abbyc
 */
@Entity
@Table(name = "ruta")
@Data
public class Ruta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String ruta;

    @Column(name = "requiere_rol")
    private boolean requiereRol;

    @Enumerated(EnumType.STRING)
    private Rol rol;
}
