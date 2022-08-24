package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.IdGenerator;
import ru.practicum.shareit.exception.EntityAlreadyExistException;
import ru.practicum.shareit.exception.ValidationException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final IdGenerator idGenerator;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, IdGenerator idGenerator) {
        this.userRepository = userRepository;
        this.idGenerator = idGenerator;
    }

    @Override
    public UserDto addUser(UserDto userDto) {
        if (isValid(userDto)) {
            User user = UserMapper.toUser(userDto);
            user.setId(idGenerator.getId());
            return UserMapper.toUserDto(userRepository.addUser(user));
        } else {
            throw new ValidationException("Validation exception. Wrong email");
        }
    }

    @Override
    public UserDto updateUser(Long userId, UserDto patchUSer) {
        User oldUser = UserMapper.toUser(getUser(userId));
        User result = patch(oldUser, UserMapper.toUser(patchUSer));
        UserDto userDto = UserMapper.toUserDto(result);
        if (isValidPatch(userDto)) {
            userRepository.updateUser(result, userId);
            return userDto;
        } else {
            throw new EntityAlreadyExistException("User with email already exist");
        }
    }

    private User patch(User user, User patchUser) {
        if (patchUser.getName() != null) {
            user.setName(patchUser.getName());
        }
        if (patchUser.getEmail() != null) {
            user.setEmail(patchUser.getEmail());
        }
        return user;
    }

    @Override
    public UserDto getUser(Long userId) {
        User user = userRepository.getUser(userId);
        return UserMapper.toUserDto(user);
    }

    @Override
    public void deleteUser(Long userId) {
        userRepository.deleteUser(userId);
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.getAllUsers().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    private boolean isValid(UserDto user) {
        return userRepository.checkEmail(user.getEmail());
    }

    private boolean isValidPatch(UserDto userDto) {
        long number = getAllUsers().stream().filter(x -> x.getEmail().equals(userDto.getEmail()))
                .filter(x -> x.getId() != (userDto.getId()))
                .count();
        return number < 1;
    }
}
