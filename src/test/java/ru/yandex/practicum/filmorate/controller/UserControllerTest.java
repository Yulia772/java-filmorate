package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserControllerTest {

    private UserStorage userStorage;
    private UserService userService;
    private UserController controller;

    @BeforeEach
    void setup() {
        userStorage = new InMemoryUserStorage();
        userService = new UserService(userStorage);
        controller = new UserController(userService);
    }

    @Test
    void shouldRejectBadEmail() {
        User u = new User();
        u.setLogin("egor");
        u.setEmail("bad-email");
        u.setBirthday(LocalDate.of(2000, 1, 1));

        assertThrows(ValidationException.class, () -> controller.createUser(u));
    }

    @Test
    void shouldRejectLoginWithSpaces() {
        User u = new User();
        u.setLogin("bad login");
        u.setEmail("a@b.c");
        u.setBirthday(LocalDate.of(2000, 1, 1));

        assertThrows(ValidationException.class, () -> controller.createUser(u));
    }

    @Test
    void shouldRejectFutureBirthday() {
        User u = new User();
        u.setLogin("egor");
        u.setEmail("a@b.c");
        u.setBirthday(LocalDate.now().plusDays(1));

        assertThrows(ValidationException.class, () -> controller.createUser(u));
    }

    @Test
    void emptyNameReplacedWithLogin() {
        User u = new User();
        u.setLogin("egor");
        u.setEmail("a@b.c");
        u.setName(""); // пустое имя
        u.setBirthday(LocalDate.of(2000, 1, 1));

        User created = controller.createUser(u);
        assertEquals("egor", created.getName());
    }
}