package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;
import ru.practicum.shareit.exception.EntityAlreadyExist;
import ru.practicum.shareit.exception.ValidationException;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final ObjectMapper mapper;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper, ObjectMapper mapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.mapper = mapper;
    }

    @Override
    public UserDto addUser(UserDto userDto) {
        if (isValid(userDto)) {
            User user = userMapper.toUser(userDto);
            return userMapper.toUserDto(userRepository.addUser(user));
        } else {
            throw new ValidationException("Validation exception");
        }
    }

    @Override
    public UserDto updateUser(Long userId, Map<Object, Object> fields) {
        User user = userMapper.toUser(getUser(userId));
        fields.forEach((k, v) -> {
            Field field = ReflectionUtils.findField(User.class, String.valueOf(k));
            field.setAccessible(true);
            ReflectionUtils.setField(field, user, v);
        });
        UserDto userDto = userMapper.toUserDto(user);
        userRepository.updateUser(user);
        return userDto;

        /*if(isValid(userDto)) {
            userRepository.updateUser(user);
            return userDto;
        } else {
            throw new EntityAlreadyExist("User with that email already exist");
        }*/
    }

    @Override
    public UserDto getUser(Long userId) {
        User user = userRepository.getUser(userId);
        return userMapper.toUserDto(user);
    }

    @Override
    public void deleteUser(Long userId) {
        userRepository.deleteUser(userId);
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.getAllUsers().stream()
                .map(userMapper::toUserDto)
                .collect(Collectors.toList());
    }

    private boolean isValid(UserDto user) {
        if (getAllUsers().stream().anyMatch(x -> x.getEmail().equals(user.getEmail()))) {
            throw new ValidationException("Incorrect email. User with that email already exist");
        }
        return true;
    }

    /*private boolean isValidPatch(UserDto userDto) {
        long number = getAllUsers().stream().filter(x -> x.getEmail().equals(userDto.getEmail()))
                .filter(x -> !x.getId().equals(userDto.getId()))
                .count();
        if(number >= 1) {
            return false;
        } else {
            return true;
        }
    }*/
}
