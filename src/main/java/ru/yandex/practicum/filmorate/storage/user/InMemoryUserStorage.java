package ru.yandex.practicum.filmorate.storage.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class InMemoryUserStorage implements UserStorage {
    private static final Logger log = LoggerFactory.getLogger(InMemoryUserStorage.class);

    private final Map<Integer, User> users = new HashMap<>();
    private int currentId = 1;

    @Override
    public User create(User user) {
        user.setId(currentId++);
        users.put(user.getId(), user);
        log.info("User created: {}", user);
        return user;
    }

    @Override
    public User update(User user) {
        int id = user.getId();
        if (!users.containsKey(id)) {
            throw new NotFoundException("Пользователь с id=" + id + "не найден");
        }
        users.put(id, user);
        log.info("User updated: {}", user);
        return user;
    }

    @Override
    public User findById(int id) {
        return users.get(id); // null если не найден
    }

    @Override
    public Collection<User> findAll() {
        return users.values();
    }
}