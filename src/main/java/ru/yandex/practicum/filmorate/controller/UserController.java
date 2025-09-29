package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final Map<Integer, User> users = new HashMap<>();
    private int currentId = 1;

    @PostMapping
    public User createUser(@RequestBody User user) {
        log.info("DEBAG USER: {}", user);
        validate(user);
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        user.setId(currentId++);
        users.put(user.getId(), user);
        log.info("Пользователь создан: {}", user);
        return user;
    }

    @PutMapping
    public User updateUser(@RequestBody User user) {
        validate(user);
        if (!users.containsKey(user.getId())) {
            log.warn("Обновление пользователя: id={} не найден", user.getId());
            throw new ValidationException("Пользователь с id=" + user.getId() + " не найден");
        }
        users.put(user.getId(), user);
        log.info("Пользователь обновлен: {}", user);
        return user;
    }

    @GetMapping
    public Collection<User> getAllUsers() {
        return users.values();
    }

    private void validate(User user) {
        String email = user.getEmail();
        if (email == null || email.isBlank() || !email.contains("@")) {
            log.warn("Валидация пользователя не пройдена: email='{}'", email);
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать @");
        }
        String login = user.getLogin();
        if (login == null || login.isBlank() || login.contains(" ")) {
            log.warn("Валидация пользователя не пройдена: login='{}'", login);
            throw new ValidationException("Логин не может быть пустым или содержать пробелы");
        }
        if (user.getBirthday() != null && user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("Валидация пользователя не пройдена: день рождения в будущем {}", user.getBirthday());
            throw new ValidationException("День рождения не может быть в будущем");
        }
    }
}
