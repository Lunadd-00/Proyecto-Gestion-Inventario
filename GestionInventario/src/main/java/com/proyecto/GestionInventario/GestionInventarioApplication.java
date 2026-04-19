package com.proyecto.GestionInventario;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class GestionInventarioApplication {

	public static void main(String[] args) {
		SpringApplication.run(GestionInventarioApplication.class, args);
	}

}
