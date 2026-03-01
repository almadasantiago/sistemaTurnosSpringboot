package com.barberia.users.dto;

import jakarta.validation.constraints.*;

public record CreateBarberRequest(

    @NotBlank(message = "El nombre es obligatorio")
    String name,

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email no tiene un formato válido")
    String email,

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    String password,

    Double commissionRate
) {}