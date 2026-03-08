package com.barberia.barberservices.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BarberServiceResponse {
    private Long id;
    private String name;
    private Double price;
    private Integer durationMinutes;
    private Boolean finished;
}