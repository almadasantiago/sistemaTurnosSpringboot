package com.barberia.barberservices.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BarberServiceResponse {
    private Long id;
    private String nombre;
    private Double precio;
    private Integer duracionMinutos;
    private Boolean finished;
}