package com.barberia.appointments.dto;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateAppointmentRequest {
    private Long clienteId;
    private Long barberoId;
    private Long serviceId;
    private LocalDateTime fechaHoraInicio;
}