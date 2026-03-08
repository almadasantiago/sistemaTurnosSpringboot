package com.barberia.shared.config;

import com.barberia.users.*;
import com.barberia.barberservices.*;
import com.barberia.appointments.*;
import com.barberia.workschedules.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Configuration
@RequiredArgsConstructor
public class DataSeeder {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final BarberServiceRepository barberServiceRepository;
	private final AppointmentRepository appointmentRepository;
	private final WorkScheduleRepository workScheduleRepository;

	@Bean
	public CommandLineRunner seedData() {
		return args -> {
			if (userRepository.count() == 0) {
				// Usuarios
				User admin = User.builder()
						.name("Admin")
						.email("admin@barber.com")
						.password(passwordEncoder.encode("admin123"))
						.role(Role.ADMIN)
						.build();
				User barber = User.builder()
						.name("Barbero Juan")
						.email("barber@barber.com")
						.password(passwordEncoder.encode("barber123"))
						.role(Role.BARBER)
						.commissionRate(0.3)
						.build();
				User client = User.builder()
						.name("Cliente Pedro")
						.email("client@barber.com")
						.password(passwordEncoder.encode("client123"))
						.role(Role.CLIENT)
						.build();
				userRepository.save(admin);
				userRepository.save(barber);
				userRepository.save(client);
			}

			if (barberServiceRepository.count() == 0) {
				BarberService corte = new BarberService();
				corte.setName("Corte de pelo");
				corte.setPrice(1500.0);
				corte.setDurationMinutes(30);
				corte.setFinished(false);
				barberServiceRepository.save(corte);

				BarberService barba = new BarberService();
				barba.setName("Afeitado de barba");
				barba.setPrice(1000.0);
				barba.setDurationMinutes(20);
				barba.setFinished(false);
				barberServiceRepository.save(barba);
			}

			if (workScheduleRepository.count() == 0) {
				User barber = userRepository.findByEmail("barber@barber.com").orElse(null);
				if (barber != null) {
					for (DayOfWeek day : DayOfWeek.values()) {
						WorkSchedule ws = new WorkSchedule();
						ws.setBarbero(barber);
						ws.setDiaSemana(day);
						ws.setHoraEntrada(LocalTime.of(9, 0));
						ws.setHoraSalida(LocalTime.of(18, 0));
						workScheduleRepository.save(ws);
					}
				}
			}

			if (appointmentRepository.count() == 0) {
				User client = userRepository.findByEmail("client@barber.com").orElse(null);
				User barber = userRepository.findByEmail("barber@barber.com").orElse(null);
				BarberService corte = barberServiceRepository.findAll().stream().findFirst().orElse(null);
				if (client != null && barber != null && corte != null) {
					Appointment appt = new Appointment();
					appt.setCliente(client);
					appt.setBarbero(barber);
					appt.setService(corte);
					appt.setFechaHoraInicio(LocalDateTime.now().plusDays(1).withHour(10).withMinute(0));
					appt.setFechaHoraFin(LocalDateTime.now().plusDays(1).withHour(10).withMinute(30));
					appt.setEstado(AppointmentStatus.PENDING);
					appointmentRepository.save(appt);
				}
			}
		};
	}
}
