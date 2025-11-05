package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
class FilmServiceTest {

    @Autowired
    private FilmService filmService;

    @Autowired
    private UserService userService;

    private Film newFilm(String name) {
        Film f = new Film();
        f.setName(name);
        f.setDescription("desc");
        f.setReleaseDate(LocalDate.of(2000, 1, 1));
        f.setDuration(100);
        Mpa m = new Mpa();
        m.setId(1); // G из data.sql
        f.setMpa(m);
        return filmService.create(f);
    }

    private User newUser(String email, String login) {
        User u = new User();
        u.setEmail(email);
        u.setLogin(login);
        u.setName(login);
        u.setBirthday(LocalDate.of(1990, 1, 1));
        return userService.create(u);
    }

    @BeforeEach
    void clean() {
        // опционально: можно ничего не делать — @AutoConfigureTestDatabase поднимает чистую БД перед тестом
    }

    @Test
    void addLike_andGetPopular_ordersByLikesDesc() {
        Film f1 = newFilm("A");
        Film f2 = newFilm("B");
        User u1 = newUser("a@a.ru", "a");
        User u2 = newUser("b@b.ru", "b");

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
        Film f = newFilm("A");
        User u = newUser("a@a.ru", "a");

        filmService.addLike(f.getId(), u.getId());
        filmService.addLike(f.getId(), u.getId()); // повторно — не должно дублировать

        List<Film> top = filmService.getPopular(10);
        assertEquals(1, top.size());
        assertEquals(f.getId(), top.get(0).getId());
    }

    @Test
    void removeLike_decreasesCounter() {
        Film f = newFilm("A");
        User u = newUser("a@a.ru", "a");

        filmService.addLike(f.getId(), u.getId());
        filmService.removeLike(f.getId(), u.getId());

        List<Film> top = filmService.getPopular(10);
        assertEquals(1, top.size());
        assertEquals(f.getId(), top.get(0).getId());
    }

    @Test
    void getPopular_defaultCountIs10() {
        for (int i = 1; i <= 12; i++) newFilm("F" + i);
        List<Film> top = filmService.getPopular(10);
        assertEquals(10, top.size());
    }
}