package com.barberia.appointments;

import java.time.LocalDateTime;

import com.barberia.barberservices.BarberService;
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
@Table(name = "appointments")
@Getter
@Setter
@NoArgsConstructor
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private User cliente;

    @ManyToOne
    @JoinColumn(name = "barbero_id")
    private User barbero;

    @ManyToOne
    @JoinColumn(name = "service_id")
    private BarberService service;

    @Column(name = "fecha_hora_inicio")
    private LocalDateTime fechaHoraInicio;

    @Column(name = "fecha_hora_fin")
    private LocalDateTime fechaHoraFin;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AppointmentStatus estado = AppointmentStatus.PENDING;

    @Column(name = "precio_final")
    private Double precioFinal;

    @Column(name = "comision_barberia")
    private Double comisionBarberia;

    @Column(name = "ganancia_barbero")
    private Double gananciaBarbero;
}