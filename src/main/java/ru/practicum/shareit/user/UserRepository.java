package ru.practicum.shareit.user;

import java.util.List;

public interface UserRepository {
    User addUser(User user);

    User updateUser(User user);

    User getUser(String userId);

    void deleteUser(String userId);

    List<User> getAllUsers();
}
