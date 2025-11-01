package com.tiffany.paymod.service.impl;

import com.tiffany.paymod.model.User;
import com.tiffany.paymod.repository.UserRepository;
import com.tiffany.paymod.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
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
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}