package ru.practicum.shareit.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "/users")
@Slf4j
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public UserDto addUser(@Valid @RequestBody UserDto user) {
        UserDto result = userService.addUser(user);
        log.info("User {} was added", result);
        return result;
    }

    @PatchMapping(path = "/{userId}")
    public UserDto updateUser(@PathVariable Long userId, @Valid @RequestBody Map<Object, Object> fields)
            throws JsonProcessingException {
        UserDto result = userService.updateUser(userId, fields);
        log.info("User {} was changed", userId);
        return result;
    }

    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable Long userId) {
        UserDto result = userService.getUser(userId);
        log.info("Get user {}", result);
        return result;
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        log.info("User was deleted {}", userId);
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        List<UserDto> result = userService.getAllUsers();
        log.info("Get all users");
        return result;
    }
}
