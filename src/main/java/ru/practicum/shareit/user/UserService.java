package ru.practicum.shareit.user;


import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto addUser(UserDto user);
    UserDto updateUser(Long userId, UserDto patchUser);
    UserDto getUser(Long userId);
    void deleteUser(Long userId);
    List<UserDto> getAllUsers();
}
