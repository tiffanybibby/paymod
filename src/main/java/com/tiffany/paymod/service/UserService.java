package com.tiffany.paymod.service;

import com.tiffany.paymod.model.User;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface UserService {

    List<User> fetchAllUsers();

    Optional<User> fetchUser(Long id);

    Optional<User> fetchByEmail(String email);

    void addUser(User user);

    boolean updateUser(Long id, User updatedUser);

    boolean patchUser(Long id, Map<String, Object> payload);

    void deleteUser(Long id);
}
