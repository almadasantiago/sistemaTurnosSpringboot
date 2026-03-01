package com.barberia.users.dto;

import jakarta.validation.constraints.*;

public record UpdateUserRequest(

    @NotBlank(message = "El nombre es obligatorio")
    String name,

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email no tiene un formato válido")
    String email,

    Double commissionRate

) {}