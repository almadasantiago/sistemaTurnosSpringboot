package com.barberia.workschedules;

import com.barberia.workschedules.dto.CreateWorkScheduleRequest;
import com.barberia.workschedules.dto.WorkScheduleResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/workschedules")
@RequiredArgsConstructor
public class WorkScheduleController {

    private final WorkScheduleService workScheduleService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<WorkScheduleResponse> create(@RequestBody CreateWorkScheduleRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(workScheduleService.create(request));
    }

    @GetMapping("/barber/{barberoId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'BARBER')")
    public ResponseEntity<List<WorkScheduleResponse>> getByBarbero(@PathVariable Long barberoId) {
        return ResponseEntity.ok(workScheduleService.getByBarbero(barberoId));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        workScheduleService.delete(id);
        return ResponseEntity.noContent().build();
    }
}