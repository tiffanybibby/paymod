package com.tiffany.paymod.service.impl;

import com.tiffany.paymod.dto.CreateUserRequest;
import com.tiffany.paymod.dto.UpdateUserRequest;
import com.tiffany.paymod.dto.UserDto;
import com.tiffany.paymod.utility.ApiMapperUtil;
import com.tiffany.paymod.model.User;
import com.tiffany.paymod.repository.UserRepository;
import com.tiffany.paymod.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> fetchAllUsers() {
        return userRepository.findAll().stream().map(ApiMapperUtil::toUserDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserDto> fetchUser(Long id) {
        return userRepository.findById(id).map(ApiMapperUtil::toUserDto);
    }

    @Override
    @Transactional
    public UserDto fetchOrCreate(String email, String firstName, String lastName) {
        Optional<User> existingUser = userRepository.findByEmail(email);
        if (existingUser.isPresent()) {
            return ApiMapperUtil.toUserDto(existingUser.get());
        } else {
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setFirstName(firstName);
            newUser.setLastName(lastName);
            return ApiMapperUtil.toUserDto(userRepository.save(newUser));
        }
    }

    @Override
    @Transactional
    public void addUser(CreateUserRequest request) {
        Optional<User> existingUser = userRepository.findByEmail(request.email());
        if (existingUser.isPresent()) {
            throw new IllegalArgumentException("User with email " + request.email() + " already exists.");
        } else {
            User newUser = new User();
            newUser.setEmail(request.email());
            newUser.setFirstName(request.firstName());
            newUser.setLastName(request.lastName());
            newUser.setBillingPostalCode(request.billingPostalCode());
            newUser.setBillingCountry(request.billingCountry());
            userRepository.save(newUser);
        }
    }

    @Override
    @Transactional
    public boolean updateUser(Long id, UpdateUserRequest request) {
        return userRepository.findById(id)
                .map(existingUser -> {
                    existingUser.setEmail(request.email());
                    existingUser.setFirstName(request.firstName());
                    existingUser.setLastName(request.lastName());
                    userRepository.save(existingUser);
                    return true;
                })
                .orElse(false);
    }

    @Override
    @Transactional
    public boolean patchUser(Long id, UpdateUserRequest request) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) return false;
        User user = optionalUser.get();

        if (request.email() != null && !request.email().isBlank()) {
            user.setEmail(request.email());
        }
        if (request.firstName() != null) {
            user.setFirstName(request.firstName());
        }
        if (request.lastName() != null) {
            user.setLastName(request.lastName());
        }
        userRepository.save(user);
        return true;
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}