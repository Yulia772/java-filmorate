package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
class FilmControllerTest {

    @Autowired
    private FilmController controller;

    private Film baseFilm() {
        Film f = new Film();
        f.setName("Test film");
        f.setDescription("desc");
        f.setReleaseDate(LocalDate.of(2000, 1, 1));
        f.setDuration(100);

        Mpa m = new Mpa();
        m.setId(1);
        f.setMpa(m);

        Genre g1 = new Genre();
        g1.setId(1);
        Genre g2 = new Genre();
        g2.setId(2);
        Set<Genre> genres = new LinkedHashSet<>();
        genres.add(g1);
        genres.add(g2);
        f.setGenres(genres);

        return f;
    }

    @Test
    void shouldRejectEmptyName() {
        Film f = baseFilm();
        f.setName("");
        assertThrows(ValidationException.class, () -> controller.addFilm(f));
    }

    @Test
    void shouldRejectTooLongDescription() {
        Film f = baseFilm();
        f.setDescription("x".repeat(201)); // > 200 символов
        assertThrows(ValidationException.class, () -> controller.addFilm(f));
    }

    @Test
    void shouldRejectReleaseDateBeforeCinemaBirthday() {
        Film f = baseFilm();
        f.setReleaseDate(LocalDate.of(1895, 12, 27)); // день до 28.12.1895
        assertThrows(ValidationException.class, () -> controller.addFilm(f));
    }

    @Test
    void shouldRejectNonPositiveDuration() {
        Film f = baseFilm();
        f.setDuration(0);
        assertThrows(ValidationException.class, () -> controller.addFilm(f));
    }

    @Test
    void createAndGetById_returnsFilmWithIdAndMpaAndGenres() {
        Film created = controller.addFilm(baseFilm());
        assertTrue(created.getId() > 0);
        assertNotNull(created.getMpa());
        assertEquals(1, created.getMpa().getId()); // из data.sql
        assertNotNull(created.getGenres());
        assertEquals(2, created.getGenres().size());

        Film fromGet = controller.getById(created.getId());
        assertEquals(created.getId(), fromGet.getId());
        assertEquals("Test film", fromGet.getName());
        assertEquals(2, fromGet.getGenres().size());
    }

    @Test
    void updateFilm_updatesFields() {
        Film created = controller.addFilm(baseFilm());
        created.setName("Updated");
        created.setDuration(123);

        Film updated = controller.updateFilm(created);
        assertEquals("Updated", updated.getName());
        assertEquals(123, updated.getDuration());

        Film fromGet = controller.getById(created.getId());
        assertEquals("Updated", fromGet.getName());
        assertEquals(123, fromGet.getDuration());
    }
}