package com.barberia.barberservices;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data

public class Service {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;
    
    private String nombre; 
    private Double precio;
    private Integer duracionMinutos; 
}
