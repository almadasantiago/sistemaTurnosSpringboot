package com.barberia.appointments;

import com.barberia.appointments.dto.AppointmentResponse;
import com.barberia.appointments.dto.CreateAppointmentRequest;
import com.barberia.appointments.dto.UpdateAppointmentStatusRequest;
import com.barberia.barberservices.BarberService;
import com.barberia.barberservices.BarberServiceRepository;
import com.barberia.users.User;
import com.barberia.users.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final BarberServiceRepository barberServiceRepository;

    public AppointmentResponse create(CreateAppointmentRequest request) {
        User cliente = userRepository.findById(request.getClienteId())
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        User barbero = userRepository.findById(request.getBarberoId())
                .orElseThrow(() -> new RuntimeException("Barbero no encontrado"));

        BarberService service = barberServiceRepository.findById(request.getServiceId())
                .orElseThrow(() -> new RuntimeException("Servicio no encontrado"));

        LocalDateTime fechaHoraFin = request.getFechaHoraInicio()
                .plusMinutes(service.getDurationMinutes());

        Double precioFinal = service.getPrice();
        Double gananciaBarbero = precioFinal * barbero.getCommissionRate();
        Double comisionBarberia = precioFinal - gananciaBarbero;

        Appointment appointment = new Appointment();
        appointment.setCliente(cliente);
        appointment.setBarbero(barbero);
        appointment.setService(service);
        appointment.setFechaHoraInicio(request.getFechaHoraInicio());
        appointment.setFechaHoraFin(fechaHoraFin);
        appointment.setEstado(AppointmentStatus.PENDING);
        appointment.setPrecioFinal(precioFinal);
        appointment.setComisionBarberia(comisionBarberia);
        appointment.setGananciaBarbero(gananciaBarbero);

        return toResponse(appointmentRepository.save(appointment));
    }

    public List<AppointmentResponse> getAll() {
        return appointmentRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<AppointmentResponse> getByBarbero(Long barberoId) {
        return appointmentRepository.findByBarberoId(barberoId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<AppointmentResponse> getByCliente(Long clienteId) {
        return appointmentRepository.findByClienteId(clienteId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<AppointmentResponse> getPendingByCliente(Long clienteId) {
        return appointmentRepository.findByClienteIdAndEstado(clienteId, AppointmentStatus.PENDING)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public AppointmentResponse updateStatus(Long id, UpdateAppointmentStatusRequest request) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Turno no encontrado"));

        appointment.setEstado(request.getEstado());
        return toResponse(appointmentRepository.save(appointment));
    }

    public void delete(Long id) {
        appointmentRepository.deleteById(id);
    }

    private AppointmentResponse toResponse(Appointment appointment) {
        return new AppointmentResponse(
                appointment.getId(),
                new AppointmentResponse.ClientSummary(
                        appointment.getCliente().getId(),
                        appointment.getCliente().getName()
                ),
                new AppointmentResponse.ClientSummary(
                        appointment.getBarbero().getId(),
                        appointment.getBarbero().getName()
                ),
                new AppointmentResponse.ServiceSummary(
                        appointment.getService().getId(),
                        appointment.getService().getName(),
                        appointment.getService().getPrice(),
                        appointment.getService().getDurationMinutes()
                ),
                appointment.getFechaHoraInicio(),
                appointment.getFechaHoraFin(),
                appointment.getEstado(),
                appointment.getPrecioFinal(),
                appointment.getComisionBarberia(),
                appointment.getGananciaBarbero()
        );
    }
}