package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertThrows;

class FilmControllerTest {
    @Test
    void shouldRejectEmptyName() {
        FilmController c = new FilmController();
        Film f = new Film();
        f.setName(" ");
        f.setDuration(100);
        f.setReleaseDate(LocalDate.of(2000, 1, 1));
        assertThrows(ValidationException.class, () -> c.addFilm(f));
    }

    @Test
    void shouldRejectTooLongDescription() {
        FilmController c = new FilmController();
        Film f = new Film();
        f.setName("Ok");
        f.setDescription("x".repeat(201));
        f.setDuration(90);
        f.setReleaseDate(LocalDate.of(2000, 1, 1));
        assertThrows(ValidationException.class, () -> c.addFilm(f));
    }

    @Test
    void shouldRejectEarlyReleaseDate() {
        FilmController c = new FilmController();
        Film f = new Film();
        f.setName("Ok");
        f.setDuration(90);
        f.setReleaseDate(LocalDate.of(1895, 12, 27)); // раньше порога
        assertThrows(ValidationException.class, () -> c.addFilm(f));
    }

    @Test
    void shouldRejectNonPositiveDuration() {
        FilmController c = new FilmController();
        Film f = new Film();
        f.setName("Ok");
        f.setDuration(0);
        f.setReleaseDate(LocalDate.of(2000, 1, 1));
        assertThrows(ValidationException.class, () -> c.addFilm(f));
    }
}