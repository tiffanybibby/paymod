package com.tiffany.paymod.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CreateUserRequest (
        @NotBlank
        @Email
        String email,
        String firstName,
        String lastName,
        String billingPostalCode,
        String billingCountry
){
}
