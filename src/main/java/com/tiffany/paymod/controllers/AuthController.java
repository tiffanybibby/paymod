package com.tiffany.paymod.controllers;

import com.tiffany.paymod.dto.LoginRequest;
import com.tiffany.paymod.dto.UserDto;
import com.tiffany.paymod.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AuthController {

    private final UserService userService;

    @PostMapping("/auth/login")
    public ResponseEntity<UserDto> login(@Valid @RequestBody LoginRequest request) {

        return ResponseEntity.ok(userService.fetchOrCreate(
                request.email(),
                request.firstName(),
                request.lastName()
        ));
    }

}
