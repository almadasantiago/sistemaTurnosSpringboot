package com.barberia.appointments;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByBarberoId(Long barberoId);
    List<Appointment> findByClienteId(Long clienteId);
    List<Appointment> findByClienteIdAndEstado(Long clienteId, AppointmentStatus estado);
    List<Appointment> findByBarberoIdAndFechaHoraInicioBetween(Long barberoId, LocalDateTime start, LocalDateTime end);
}