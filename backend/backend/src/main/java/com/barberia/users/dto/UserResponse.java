package com.barberia.users.dto;

import com.barberia.users.Role;

public record UserResponse(
    Long id,
    String name,
    String email,
    Role role,
    Double commissionRate
) {}