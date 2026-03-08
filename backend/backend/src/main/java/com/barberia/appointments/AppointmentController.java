package com.barberia.appointments;

import com.barberia.appointments.dto.AppointmentResponse;
import com.barberia.appointments.dto.CreateAppointmentRequest;
import com.barberia.appointments.dto.UpdateAppointmentStatusRequest;
import com.barberia.users.User;
import com.barberia.users.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final UserService userService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT')")
    public ResponseEntity<AppointmentResponse> create(@RequestBody CreateAppointmentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(appointmentService.create(request));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AppointmentResponse>> getAll() {
        return ResponseEntity.ok(appointmentService.getAll());
    }

    @GetMapping("/my/pending")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<List<AppointmentResponse>> getMyPending(
            @AuthenticationPrincipal UserDetails userDetails) {
        User client = userService.findByEmail(userDetails.getUsername());
        return ResponseEntity.ok(appointmentService.getPendingByCliente(client.getId()));
    }

    @GetMapping("/barber/{barberoId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'BARBER')")
    public ResponseEntity<List<AppointmentResponse>> getByBarbero(@PathVariable Long barberoId) {
        return ResponseEntity.ok(appointmentService.getByBarbero(barberoId));
    }

    @GetMapping("/client/{clienteId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT')")
    public ResponseEntity<List<AppointmentResponse>> getByCliente(@PathVariable Long clienteId) {
        return ResponseEntity.ok(appointmentService.getByCliente(clienteId));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'BARBER')")
    public ResponseEntity<AppointmentResponse> updateStatus(@PathVariable Long id, @RequestBody UpdateAppointmentStatusRequest request) {
        return ResponseEntity.ok(appointmentService.updateStatus(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        appointmentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}