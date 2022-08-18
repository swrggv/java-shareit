package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;
import ru.practicum.shareit.exception.EntityAlreadyExistException;
import ru.practicum.shareit.exception.ValidationException;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public UserDto addUser(UserDto userDto) {
        if (isValid(userDto)) {
            User user = userMapper.toUser(userDto);
            return userMapper.toUserDto(userRepository.addUser(user));
        } else {
            log.error("User with email {} already exist", userDto.getEmail() );
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
        if(isValidPatch(userDto)) {
            userRepository.updateUser(user);
            return userDto;
        } else {
            log.error("User with email {} already exist", userDto.getEmail());
            throw new EntityAlreadyExistException("User with that email already exist");
        }
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
        return getAllUsers().stream().noneMatch(x -> x.getEmail().equals(user.getEmail()));
    }

    private boolean isValidPatch(UserDto userDto) {
        long number = getAllUsers().stream().filter(x -> x.getEmail().equals(userDto.getEmail()))
                .filter(x -> x.getId() != (userDto.getId()))
                .count();
        return number < 1;
    }
}
