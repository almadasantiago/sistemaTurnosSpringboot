package com.barberia.barberservices;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "barber_services")
public class BarberService {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private Double precio;
    private Integer duracionMinutos;

    private Boolean finished = false;
}