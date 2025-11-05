package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@Transactional // каждый тест откатывается — БД «чистая»
class FilmServiceTest {

    @Autowired
    private FilmService filmService;
    @Autowired
    private UserService userService;

    private static final AtomicInteger SEQ = new AtomicInteger(1);

    private User newUser(String login) {
        int n = SEQ.getAndIncrement();
        User u = new User();
        u.setEmail("u" + n + "@a.ru"); // уникальный email на каждый вызов
        u.setLogin(login);
        u.setName(login);
        u.setBirthday(LocalDate.of(1990, 1, 1));
        return userService.create(u);
    }

    private Film newFilm(String name) {
        Film f = new Film();
        f.setName(name);
        f.setDescription("desc");
        f.setReleaseDate(LocalDate.of(2000, 1, 1));
        f.setDuration(100);
        Mpa m = new Mpa();
        m.setId(1);
        m.setName("G");
        f.setMpa(m);
        Genre g = new Genre();
        g.setId(1);
        g.setName("Комедия");
        f.setGenres(new LinkedHashSet<>(List.of(g)));
        return filmService.create(f);
    }

    @Test
    void addLike_andGetPopular_ordersByLikesDesc() {
        Film f1 = newFilm("A");
        Film f2 = newFilm("B");
        User u1 = newUser("a");
        User u2 = newUser("b");

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
        Film f = newFilm("C");
        User u = newUser("c");

        filmService.addLike(f.getId(), u.getId());
        filmService.addLike(f.getId(), u.getId()); // повторный лайк игнорируется MERGE’ом

        List<Film> top = filmService.getPopular(10);
        assertEquals(1, top.size());
        assertEquals(f.getId(), top.get(0).getId());
    }

    @Test
    void removeLike_decreasesCounter() {
        Film f = newFilm("D");
        User u = newUser("d");

        filmService.addLike(f.getId(), u.getId());
        filmService.removeLike(f.getId(), u.getId());

        List<Film> top = filmService.getPopular(10);
        assertEquals(1, top.size());
        assertEquals(f.getId(), top.get(0).getId());
    }

    @Test
    void getPopular_defaultCounts10() {
        for (int i = 0; i < 12; i++) newFilm("F" + i);
        List<Film> top = filmService.getPopular(10);
        assertEquals(10, top.size());
    }
}