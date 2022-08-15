package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class UserRepositoryInMemory implements UserRepository {
    private HashMap<Long, User> allUsers = new HashMap<>();

    @Override
    public User addUser(User user) {
        user.setId(IdUserGenerator.generateId());
        allUsers.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        return allUsers.put(user.getId(), user);
    }

    @Override
    public User getUser(String userId) {
        return allUsers.get(userId);
    }

    @Override
    public void deleteUser(String userId) {
        allUsers.remove(userId);
    }

    @Override
    public List<User> getAllUsers() {
        return allUsers.values().stream().collect(Collectors.toList());
    }

    static class IdUserGenerator {
        private static long id;

        private static long generateId() {
            return ++id;
        }
    }
}
