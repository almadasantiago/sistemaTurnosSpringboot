package com.barberia.workschedules.dto;

import java.time.DayOfWeek;
import java.time.LocalTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateWorkScheduleRequest {
    private Long barberoId;
    private DayOfWeek diaSemana;
    private LocalTime horaEntrada;
    private LocalTime horaSalida;
}