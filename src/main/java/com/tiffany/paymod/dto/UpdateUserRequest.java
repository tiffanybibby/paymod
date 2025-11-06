package com.tiffany.paymod.dto;

public record UpdateUserRequest(
        String firstName,
        String lastName,
        String email
) {
}
