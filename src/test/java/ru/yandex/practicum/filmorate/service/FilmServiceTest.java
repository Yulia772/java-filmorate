package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FilmServiceTest {

    private FilmStorage filmStorage;
    private UserStorage userStorage;
    private UserService userService;
    private FilmService filmService;

    @BeforeEach
    void setup() {
        filmStorage = new InMemoryFilmStorage();
        userStorage = new InMemoryUserStorage();
        userService = new UserService(userStorage);
        filmService = new FilmService(filmStorage, userService);
    }

    private Film newFilm(int id, String name) {
        Film f = new Film();
        f.setId(id);
        f.setName(name);
        f.setDescription("desc");
        f.setReleaseDate(LocalDate.of(2000, 1, 1));
        f.setDuration(100);
        filmService.create(f);
        return f;
    }

    private User newUser(int id, String email, String login) {
        User u = new User();
        u.setId(id);
        u.setEmail(email);
        u.setLogin(login);
        u.setName(login);
        u.setBirthday(LocalDate.of(1990, 1, 1));
        userService.create(u);
        return u;
    }

    @Test
    void addLike_andGetPopular_ordersByLikesDesc() {
        Film f1 = newFilm(1, "A");
        Film f2 = newFilm(2, "B");
        User u1 = newUser(1, "a@a.ru", "a");
        User u2 = newUser(2, "b@b.ru", "b");

        filmService.addLike(f1.getId(), u1.getId());
        filmService.addLike(f1.getId(), u2.getId());
        filmService.addLike(f2.getId(), u1.getId());

        List<Film> top = filmService.getPopular(10);

        assertEquals(2, top.size());
        assertEquals(f1.getId(), top.get(0).getId());
        assertEquals(f2.getId(), top.get(1).getId());
    }

    @Test
    void likeTwice_doesNotDuplicate() {
        Film f = newFilm(1, "A");
        User u = newUser(1, "a@a.ru", "a");

        filmService.addLike(f.getId(), u.getId());
        filmService.addLike(f.getId(), u.getId()); // повторно

        List<Film> top = filmService.getPopular(10);
        assertEquals(1, top.size());
        assertEquals(f.getId(), top.get(0).getId());
    }

    @Test
    void removeLike_decreasesCounter() {
        Film f = newFilm(1, "A");
        User u = newUser(1, "a@a.ru", "a");

        filmService.addLike(f.getId(), u.getId());
        filmService.removeLike(f.getId(), u.getId());

        List<Film> top = filmService.getPopular(10);
        assertEquals(1, top.size());
        assertEquals(f.getId(), top.get(0).getId());
    }

    @Test
    void getPopular_defaultCountIs10() {
        for (int i = 1; i <= 12; i++) newFilm(i, "F" + i);
        List<Film> top = filmService.getPopular(10);
        assertEquals(10, top.size());
    }
}