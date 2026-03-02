package com.barberia.barberservices;

import com.barberia.barberservices.dto.BarberServiceResponse;
import com.barberia.barberservices.dto.CreateBarberServiceRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/barberservices")
@RequiredArgsConstructor
public class BarberServiceController {

    private final BarberServiceService barberServiceService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BarberServiceResponse> create(@RequestBody CreateBarberServiceRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(barberServiceService.create(request));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'BARBER', 'CLIENT')")
    public ResponseEntity<List<BarberServiceResponse>> getAll() {
        return ResponseEntity.ok(barberServiceService.getAll());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BarberServiceResponse> update(@PathVariable Long id, @RequestBody CreateBarberServiceRequest request) {
        return ResponseEntity.ok(barberServiceService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        barberServiceService.softDelete(id);
        return ResponseEntity.noContent().build();
    }
}