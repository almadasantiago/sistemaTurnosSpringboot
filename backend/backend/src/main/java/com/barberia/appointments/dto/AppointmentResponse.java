package com.barberia.appointments.dto;

import java.time.LocalDateTime;

import com.barberia.appointments.AppointmentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AppointmentResponse {
    private Long id;
    private Long clienteId;
    private String nombreCliente;
    private Long barberoId;
    private String nombreBarbero;
    private Long serviceId;
    private String nombreServicio;
    private LocalDateTime fechaHoraInicio;
    private LocalDateTime fechaHoraFin;
    private AppointmentStatus estado;
    private Double precioFinal;
    private Double comisionBarberia;
    private Double gananciaBarbero;
}