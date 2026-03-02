package com.barberia.workschedules.dto;

import java.time.DayOfWeek;
import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class WorkScheduleResponse {
    private Long id;
    private Long barberoId;
    private String nombreBarbero;
    private DayOfWeek diaSemana;
    private LocalTime horaEntrada;
    private LocalTime horaSalida;
}