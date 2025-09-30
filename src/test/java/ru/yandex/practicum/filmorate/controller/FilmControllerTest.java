package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertThrows;

class FilmControllerTest {

    private final FilmStorage filmStorage = new InMemoryFilmStorage();
    private final UserStorage userStorage = new InMemoryUserStorage();
    private final UserService userService = new UserService(userStorage);
    private final FilmService filmService = new FilmService(filmStorage, userService);
    private final FilmController controller = new FilmController(filmStorage, filmService);

    private Film base() {
        Film f = new Film();
        f.setName("Name");
        f.setDescription("desc");
        f.setReleaseDate(LocalDate.of(2000, 1, 1));
        f.setDuration(100);
        return f;
    }

    @Test
    void emptyName_shouldFail() {
        Film f = base();
        f.setName("");
        assertThrows(ValidationException.class, () -> controller.addFilm(f));
    }

    @Test
    void longDescription_shouldFail() {
        Film f = base();
        f.setDescription("a".repeat(201));
        assertThrows(ValidationException.class, () -> controller.addFilm(f));
    }

    @Test
    void tooEarlyRelease_shouldFail() {
        Film f = base();
        f.setReleaseDate(LocalDate.of(1895, 12, 27));
        assertThrows(ValidationException.class, () -> controller.addFilm(f));
    }

    @Test
    void nonPositiveDuration_shouldFail() {
        Film f = base();
        f.setDuration(0);
        assertThrows(ValidationException.class, () -> controller.addFilm(f));
    }
}