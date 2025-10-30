package com.tiffany.paymod.service.impl;

import com.tiffany.paymod.model.User;
import com.tiffany.paymod.service.UserService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final List<User> userList = new ArrayList<>();
    private Long nextId = 1L;

    @Override
    public List<User> fetchAllUsers() {
        return userList;
    }

    @Override
    public Optional<User> fetchUser(Long id) {
        return userList.stream().filter(user -> user.getId().equals(id)).findFirst();
    }

    @Override
    public void addUser(User user) {
        user.setId(nextId++);
        userList.add(user);
    }

    @Override
    public boolean updateUser(Long id, User updatedUser) {
        return userList.stream()
                .filter(user -> user.getId().equals(id)).findFirst()
                .map(existingUser -> {
                    existingUser.setEmail(updatedUser.getEmail());
                    return true;
                })
                .orElse(false);
    }

    @Override
    public boolean deleteUser(Long id) {
        return userList.removeIf(user -> user.getId().equals(id));
    }
}