package com.barberia.entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data

public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; 

    private String nombre; 
    private String email; 
    private String password; 

    @Enumerated(EnumType.STRING)
    private Rol rol; 

    private Double porcentajeComision; 
}
