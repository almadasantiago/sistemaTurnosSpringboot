package com.barberia.users;

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


    private Double porcentajeComision; 
}
