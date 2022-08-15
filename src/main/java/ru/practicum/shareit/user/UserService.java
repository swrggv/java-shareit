package ru.practicum.shareit.user;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;

public interface UserService {
    User addUser(User user);

    User updateUser(String userId, User user) throws JsonProcessingException;

    User getUser(String userId);

    void deleteUser(String userId);

    List<User> getAllUsers();
}
