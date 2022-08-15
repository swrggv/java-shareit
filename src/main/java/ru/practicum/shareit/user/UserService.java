package ru.practicum.shareit.user;

import com.fasterxml.jackson.core.JsonProcessingException;

import javax.json.JsonMergePatch;
import java.util.List;
import java.util.Map;

public interface UserService {
    UserDto addUser(UserDto user);

    UserDto updateUser(Long userId, Map<Object, Object> fields) throws JsonProcessingException;

    UserDto getUser(Long userId);

    void deleteUser(Long userId);

    List<UserDto> getAllUsers();
}
