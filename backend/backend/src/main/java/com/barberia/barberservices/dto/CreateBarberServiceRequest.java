package com.barberia.barberservices.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateBarberServiceRequest {
    private String name;
    private Double price;
    private Integer durationMinutes;
}