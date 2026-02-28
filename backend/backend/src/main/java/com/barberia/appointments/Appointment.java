package com.barberia.appointments;

import java.time.LocalDateTime;

import com.barberia.barberservices.Service;
import com.barberia.users.Users;

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
    private Users cliente; 

    @ManyToOne
    private Users barbero; 

    @ManyToOne
    private Service service; 

    private LocalDateTime fechaHoraInicio; 
    private LocalDateTime fechaHoraFin; 

    private String estado; 

    private Double precioFinal; 
    private Double comisionBarberia; 
    private Double gananciaBarbero; 
}
