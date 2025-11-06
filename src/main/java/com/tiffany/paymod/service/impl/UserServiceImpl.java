package com.tiffany.paymod.service.impl;

import com.tiffany.paymod.model.User;
import com.tiffany.paymod.repository.UserRepository;
import com.tiffany.paymod.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public List<User> fetchAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> fetchUser(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> fetchByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public void addUser(User user) {
        userRepository.save(user);
    }

    @Override
    public boolean updateUser(Long id, User updatedUser) {
        return userRepository.findById(id)
                .map(existingUser -> {
                    existingUser.setEmail(updatedUser.getEmail());
                    userRepository.save(existingUser);
                    return true;
                })
                .orElse(false);
    }

    @Override
    @Transactional
    public boolean patchUser(Long id, Map<String, Object> payload) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) return false;
        User user = optionalUser.get();

        if (payload.containsKey("firstName")) user.setFirstName(asString(payload.get("firstName")));
        if (payload.containsKey("lastName"))  user.setLastName(asString(payload.get("lastName")));
        if (payload.containsKey("email")) {
            String email = asString(payload.get("email"));
            if (email != null && !email.equalsIgnoreCase(user.getEmail())) {
                if (userRepository.existsByEmail(email)) return false;
                user.setEmail(email);
            }
        }
        userRepository.save(user);
        return true;
    }

    private static String asString(Object value) { return value == null ? null : value.toString().trim(); }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}