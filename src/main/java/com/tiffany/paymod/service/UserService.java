package com.tiffany.paymod.service;

import com.tiffany.paymod.dto.CreateUserRequest;
import com.tiffany.paymod.dto.UpdateUserRequest;
import com.tiffany.paymod.dto.UserDto;

import java.util.List;
import java.util.Optional;

public interface UserService {

    List<UserDto> fetchAllUsers();

    Optional<UserDto> fetchUser(Long id);

    UserDto fetchOrCreate(String email, String firstName, String lastName);

    void addUser(CreateUserRequest request);

    boolean updateUser(Long id, UpdateUserRequest request);

    boolean patchUser(Long id, UpdateUserRequest request);

    void deleteUser(Long id);
}
