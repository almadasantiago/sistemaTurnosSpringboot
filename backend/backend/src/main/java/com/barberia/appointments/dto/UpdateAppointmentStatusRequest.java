package com.barberia.appointments.dto;

import com.barberia.appointments.AppointmentStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateAppointmentStatusRequest {
    private AppointmentStatus estado;
}