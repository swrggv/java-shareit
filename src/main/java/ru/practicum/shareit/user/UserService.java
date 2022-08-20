package ru.practicum.shareit.user;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;

public interface UserService {
    UserDto addUser(UserDto user);

    UserDto updateUser(Long userId, UserDto patchUser) throws JsonProcessingException;

    UserDto getUser(Long userId);

    void deleteUser(Long userId);

    List<UserDto> getAllUsers();
}
