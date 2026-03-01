package com.barberia.auth.dto;

import jakarta.validation.constraints.*;

public record LoginRequest(

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email no tiene un formato válido")
    String email,

    @NotBlank(message = "La contraseña es obligatoria")
    String password

) {}