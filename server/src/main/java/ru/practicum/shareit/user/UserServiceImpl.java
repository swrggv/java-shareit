package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.EntityAlreadyExistException;
import ru.practicum.shareit.exception.ModelNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper mapper;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, UserMapper mapper) {
        this.userRepository = userRepository;
        this.mapper = mapper;
    }

    @Transactional
    @Override
    public UserDto addUser(UserDto userDto) {
        if (checkExistingEmail(userDto.getEmail())) {
            throw new EntityAlreadyExistException("User with email already exist");
        } else {
            User saved = userRepository.save(mapper.toUser(userDto));
            return mapper.toUserDto(saved);
        }
    }

    private boolean checkExistingEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Transactional
    @Override
    public UserDto updateUser(Long userId, UserDto patchUSer) {
        User oldUser = mapper.toUser(getUser(userId));
        User result = patch(oldUser, mapper.toUser(patchUSer));
        UserDto userDto = mapper.toUserDto(result);
        if (isValidPatch(userDto)) {
            userRepository.save(result);
            return userDto;
        } else {
            throw new EntityAlreadyExistException("User with email already exist");
        }
    }

    @Override
    public UserDto getUser(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            return mapper.toUserDto(user.get());
        } else {
            throw new ModelNotFoundException(String.format("User %d not found", userId));
        }
    }

    @Transactional
    @Override
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    @Override
    public List<UserDto> getAllUsers() {
        return mapper.toListUserDto(userRepository.findAll());
    }

    private boolean isValidPatch(UserDto userDto) {
        long number = getAllUsers().stream().filter(x -> x.getEmail().equals(userDto.getEmail()))
                .filter(x -> x.getId() != (userDto.getId()))
                .count();
        return number < 1;
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
}
