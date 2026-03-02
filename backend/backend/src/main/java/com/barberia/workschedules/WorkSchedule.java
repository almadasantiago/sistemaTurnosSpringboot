package com.barberia.workschedules;

import java.time.DayOfWeek;
import java.time.LocalTime;

import com.barberia.users.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "work_schedule")
@Getter
@Setter
@NoArgsConstructor
public class WorkSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "barbero_id")
    private User barbero;

    @Enumerated(EnumType.STRING)
    @Column(name = "dia_semana")
    private DayOfWeek diaSemana;

    @Column(name = "hora_entrada")
    private LocalTime horaEntrada;

    @Column(name = "hora_salida")
    private LocalTime horaSalida;
}