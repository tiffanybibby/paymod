package com.tiffany.paymod.controllers;

import com.tiffany.paymod.model.User;
import com.tiffany.paymod.service.UserService;
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
    public ResponseEntity<User> login(@RequestBody java.util.Map<String,Object> payload) {
        String email = payload.get("email") == null ? null : payload.get("email").toString().trim();
        String first = payload.get("firstName") == null ? null : payload.get("firstName").toString().trim();
        String last  = payload.get("lastName")  == null ? null : payload.get("lastName").toString().trim();
        if (email == null || email.isBlank()) return ResponseEntity.badRequest().build();

        return userService.fetchByEmail(email)
                .map(ResponseEntity::ok)
                .orElseGet(() -> {
                    User u = new User();
                    u.setEmail(email);
                    u.setFirstName(first);
                    u.setLastName(last);
                    userService.addUser(u);
                    return ResponseEntity.ok(u);
                });
    }

}
