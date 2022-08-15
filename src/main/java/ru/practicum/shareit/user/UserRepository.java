package ru.practicum.shareit.user;

import java.util.List;

public interface UserRepository {
    User addUser(User user);

    User updateUser(User user);

    User getUser(Long userId);

    void deleteUser(Long userId);

    List<User> getAllUsers();
}
