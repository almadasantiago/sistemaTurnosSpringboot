package com.barberia.workschedules;

import com.barberia.users.User;
import com.barberia.users.UserRepository;
import com.barberia.workschedules.dto.CreateWorkScheduleRequest;
import com.barberia.workschedules.dto.WorkScheduleResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkScheduleService {

    private final WorkScheduleRepository workScheduleRepository;
    private final UserRepository userRepository;

    public WorkScheduleResponse create(CreateWorkScheduleRequest request) {
        User barbero = userRepository.findById(request.getBarberoId())
                .orElseThrow(() -> new RuntimeException("Barbero no encontrado"));

        WorkSchedule schedule = new WorkSchedule();
        schedule.setBarbero(barbero);
        schedule.setDiaSemana(request.getDiaSemana());
        schedule.setHoraEntrada(request.getHoraEntrada());
        schedule.setHoraSalida(request.getHoraSalida());

        WorkSchedule saved = workScheduleRepository.save(schedule);
        return toResponse(saved);
    }

    public List<WorkScheduleResponse> getByBarbero(Long barberoId) {
        return workScheduleRepository.findByBarberoId(barberoId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public void delete(Long id) {
        workScheduleRepository.deleteById(id);
    }

    private WorkScheduleResponse toResponse(WorkSchedule schedule) {
        return new WorkScheduleResponse(
                schedule.getId(),
                schedule.getBarbero().getId(),
                schedule.getBarbero().getName(),
                schedule.getDiaSemana(),
                schedule.getHoraEntrada(),
                schedule.getHoraSalida()
        );
    }
}