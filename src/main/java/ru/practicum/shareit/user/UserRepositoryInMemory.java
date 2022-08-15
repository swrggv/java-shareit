package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Repository
public class UserRepositoryInMemory implements UserRepository {
    private final HashMap<Long, User> allUsers = new HashMap<>();

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
    public User getUser(Long userId) {
        return allUsers.get(userId);
    }

    @Override
    public void deleteUser(Long userId) {
        allUsers.remove(userId);
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(allUsers.values());
    }

    static class IdUserGenerator {
        private static long id = 0;

        private static long generateId() {
            return ++id;
        }
    }
}
