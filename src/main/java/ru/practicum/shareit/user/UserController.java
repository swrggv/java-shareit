package ru.practicum.shareit.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.json.JsonMergePatch;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * // TODO .
 */
@RestController
@RequestMapping(path = "/users")
@Slf4j
public class UserController {
    UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public UserDto addUser(@Valid @RequestBody UserDto user) {
        return userService.addUser(user);
    }

    @PatchMapping(path = "/{userId}")
    public UserDto updateUser(@PathVariable Long userId, @Valid @RequestBody Map<Object, Object> fields) throws JsonProcessingException {
        return userService.updateUser(userId, fields);
    }

    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable Long userId) {
        return userService.getUser(userId);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        return userService.getAllUsers();
    }
}
