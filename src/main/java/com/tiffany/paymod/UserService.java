package com.tiffany.paymod;

import java.util.List;
import java.util.Optional;

public interface UserService {

    List<User> fetchAllUsers();

    Optional<User> fetchUser(Long id);

    void addUser(User user);

    boolean updateUser(Long id, User updatedUser);

    boolean deleteUser(Long id);
}
