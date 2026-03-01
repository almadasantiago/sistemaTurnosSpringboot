package com.barberia.appointments;

import java.time.LocalDateTime;

import com.barberia.barberservices.BarberService;
import com.barberia.users.User;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
public class Appointment {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User cliente; 

    @ManyToOne
    private User barbero; 

    @ManyToOne
    private BarberService service; 

    private LocalDateTime fechaHoraInicio; 
    private LocalDateTime fechaHoraFin; 

    private String estado; 

    private Double precioFinal; 
    private Double comisionBarberia; 
    private Double gananciaBarbero; 
}
