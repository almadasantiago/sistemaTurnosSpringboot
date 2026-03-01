package com.barberia.auth.dto;

import com.barberia.users.Role;

public record AuthResponse(
    String token,
    Long id,
    String name,
    String email,
    Role role
) {}