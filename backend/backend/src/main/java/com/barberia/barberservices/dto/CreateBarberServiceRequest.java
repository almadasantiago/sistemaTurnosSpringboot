package com.barberia.barberservices.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateBarberServiceRequest {
    private String nombre;
    private Double precio;
    private Integer duracionMinutos;
}