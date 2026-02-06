package com.barberia.entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data

public class Services {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;
    
    private String nombre; 
    private Double precio;
    private Integer duracionMinutos; 
}
