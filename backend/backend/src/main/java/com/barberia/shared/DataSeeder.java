package com.barberia.shared;

import com.barberia.barberservices.BarberService;
import com.barberia.barberservices.BarberServiceRepository;
import com.barberia.users.Role;
import com.barberia.users.User;
import com.barberia.users.UserRepository;
import com.barberia.workschedules.WorkSchedule;
import com.barberia.workschedules.WorkScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final BarberServiceRepository barberServiceRepository;
    private final WorkScheduleRepository workScheduleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

        // ADMIN
        if (!userRepository.existsByEmail("admin@barberia.com")) {
            User admin = User.builder()
                    .name("Admin")
                    .email("admin@barberia.com")
                    .password(passwordEncoder.encode("admin123"))
                    .role(Role.ADMIN)
                    .commissionRate(null)
                    .build();
            userRepository.save(admin);
            System.out.println(">>> DataSeeder: usuario ADMIN creado");
        }

        // BARBERO
        if (!userRepository.existsByEmail("rodrigo@barberia.com")) {
            User barbero = User.builder()
                    .name("Rodrigo Méndez")
                    .email("rodrigo@barberia.com")
                    .password(passwordEncoder.encode("barber123"))
                    .role(Role.BARBER)
                    .commissionRate(0.60)
                    .build();
            userRepository.save(barbero);

            // WORK SCHEDULES del barbero
            User savedBarbero = userRepository.findByEmail("rodrigo@barberia.com").get();
            List<DayOfWeek> dias = List.of(
                    DayOfWeek.MONDAY,
                    DayOfWeek.TUESDAY,
                    DayOfWeek.WEDNESDAY,
                    DayOfWeek.THURSDAY,
                    DayOfWeek.FRIDAY,
                    DayOfWeek.SATURDAY
            );
            for (DayOfWeek dia : dias) {
                WorkSchedule ws = new WorkSchedule();
                ws.setBarbero(savedBarbero);
                ws.setDiaSemana(dia);
                ws.setHoraEntrada(LocalTime.of(9, 0));
                ws.setHoraSalida(LocalTime.of(18, 0));
                workScheduleRepository.save(ws);
            }
            System.out.println(">>> DataSeeder: barbero Rodrigo Méndez creado con horarios");
        }

        // SERVICIOS
        if (barberServiceRepository.count() == 0) {
            BarberService corte = new BarberService();
            corte.setNombre("Corte de cabello");
            corte.setPrecio(13000.0);
            corte.setDuracionMinutos(30);
            corte.setFinished(false);

            BarberService corteBarba = new BarberService();
            corteBarba.setNombre("Corte y barba");
            corteBarba.setPrecio(15000.0);
            corteBarba.setDuracionMinutos(45);
            corteBarba.setFinished(false);

            barberServiceRepository.saveAll(List.of(corte, corteBarba));
            System.out.println(">>> DataSeeder: servicios creados");
        }
    }
}