package ru.practicum.shareit.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EntityAlreadyExist;
import ru.practicum.shareit.exception.ValidationException;

import javax.json.Json;
import javax.json.JsonMergePatch;
import javax.json.JsonValue;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User addUser(User user) {
        if(isValid(user)) {
            return userRepository.addUser(user);
        } else {
            throw new ValidationException("Validation exception");
        }
    }

    @Override
    public User updateUser(String userId, User user) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String one = mapper.writeValueAsString(getUser(userId));
        String two = mapper.writeValueAsString(user);
        JsonValue source = Json.createValue(one);
        JsonValue target = Json.createValue(two);
        JsonMergePatch patch = Json.createMergeDiff(source, target);
        JsonValue jsonValue = patch.apply(source);
        User freshUser = mapper.convertValue(jsonValue, User.class);
        return userRepository.updateUser(freshUser);
    }

    @Override
    public User getUser(String userId) {
        return userRepository.getUser(userId);
    }

    @Override
    public void deleteUser(String userId) {
        userRepository.deleteUser(userId);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.getAllUsers();
    }

    private boolean isValid(User user) {
        if(getAllUsers().stream().anyMatch(x -> x.getEmail().equals(user.getEmail()))) {
            throw new ValidationException("Incorrect email. User with that email already exist");
        }
        return true;
    }
}
