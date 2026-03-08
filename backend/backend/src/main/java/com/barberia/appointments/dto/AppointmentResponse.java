package com.barberia.appointments.dto;

import com.barberia.appointments.AppointmentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class AppointmentResponse {
    private Long id;
    private ClientSummary client;
    private ClientSummary barber;
    private ServiceSummary barberService;
    private LocalDateTime dateTime;
    private LocalDateTime endDateTime;
    private AppointmentStatus status;
    private Double priceLocked;
    private Double shopCommission;
    private Double barberEarnings;

    @Getter
    @AllArgsConstructor
    public static class ClientSummary {
        private Long id;
        private String name;
    }

    @Getter
    @AllArgsConstructor
    public static class ServiceSummary {
        private Long id;
        private String name;
        private Double price;
        private Integer durationMinutes;
    }
}