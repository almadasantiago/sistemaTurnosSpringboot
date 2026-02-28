package com.barberia.workschedules;

import java.time.LocalTime;

import com.barberia.users.Users;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

public class WorkSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; 

    @ManyToOne
    @JoinColumn(name = "barbero_id") 
    
    private Users barbero; 
    
    private Integer diaSemana; 
    private LocalTime horaEntrada; 
    private LocalTime horaSalida; 

}
