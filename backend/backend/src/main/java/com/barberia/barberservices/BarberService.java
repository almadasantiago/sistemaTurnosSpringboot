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

    private String name;
    private Double price;
    private Integer durationMinutes;

    private Boolean finished = false;
}