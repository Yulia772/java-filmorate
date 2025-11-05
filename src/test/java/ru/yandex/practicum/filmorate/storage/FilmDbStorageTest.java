package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import(FilmDbStorage.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmDbStorageTest {

    private final FilmDbStorage filmStorage;

    @Test
    void testCreateAndFindFilmById() {
        Film film = new Film();
        film.setName("Interstellar");
        film.setDescription("Epic space movie");
        film.setReleaseDate(LocalDate.of(2014, 11, 7));
        film.setDuration(169);

        Mpa mpa = new Mpa();
        mpa.setId(1);
        film.setMpa(mpa);

        film = filmStorage.create(film);

        Film fromDb = filmStorage.findById(film.getId());

        assertThat(fromDb)
                .isNotNull()
                .hasFieldOrPropertyWithValue("name", "Interstellar")
                .hasFieldOrPropertyWithValue("duration", 169);
    }
}