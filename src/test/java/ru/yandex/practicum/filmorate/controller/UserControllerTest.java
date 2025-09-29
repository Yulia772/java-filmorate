package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertThrows;

class UserControllerTest {
    @Test
    void shouldRejectBadEmail() {
        UserController c = new UserController();
        User u = new User();
        u.setEmail("bad-email");
        u.setLogin("egor");
        u.setBirthday(LocalDate.of(2000, 1, 1));
        assertThrows(ValidationException.class, () -> c.createUser(u));
    }

    @Test
    void shouldRejectLoginWithSpaces() {
        UserController c = new UserController();
        User u = new User();
        u.setEmail("a@b.c");
        u.setLogin("bad login");
        u.setBirthday(LocalDate.of(2000, 1, 1));
        assertThrows(ValidationException.class, () -> c.createUser(u));
    }

    @Test
    void shouldRejectFutureBirthday() {
        UserController c = new UserController();
        User u = new User();
        u.setEmail("a@b.c");
        u.setLogin("egor");
        u.setBirthday(LocalDate.now().plusDays(1));
        assertThrows(ValidationException.class, () -> c.createUser(u));
    }
}
