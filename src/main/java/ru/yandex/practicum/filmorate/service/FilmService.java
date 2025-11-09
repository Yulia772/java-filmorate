package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Service
public class FilmService {
    private static final Logger log = LoggerFactory.getLogger(FilmService.class);

    private static final LocalDate CINEMA_BIRTHDAY = LocalDate.of(1895, 12, 28);

    private final FilmStorage films;
    private final UserService users;
    private final FilmDbStorage filmDb;

    @Autowired
    public FilmService(@Qualifier("dbFilmStorage") FilmStorage films, UserService users, FilmDbStorage filmDb) {
        this.films = films;
        this.users = users;
        this.filmDb = filmDb;
    }

    public Film create(Film film) {
        validate(film);
        return films.create(film);
    }

    public Film update(Film film) {
        validate(film);
        return films.update(film);
    }

    public Collection<Film> getAll() {
        return films.findAll();
    }

    public Film getRequired(int id) {
        Film f = films.findById(id);
        if (f == null) {
            throw new NotFoundException("Фильм с id=" + id + " не найден");
        }
        return f;
    }

    public void addLike(int filmId, int userId) {
        getRequired(filmId);
        users.getRequired(userId);
        filmDb.addLike(filmId, userId);
        log.info("User {} liked film {}", userId, filmId);
    }

    public void removeLike(int filmId, int userId) {
        getRequired(filmId);
        users.getRequired(userId);
        filmDb.removeLike(filmId, userId);
        log.info("User {} remove like from film {}", userId, filmId);
    }

    public List<Film> getPopular(int count) {
        return filmDb.findPopular(count);
    }

    private void validate(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            throw new ValidationException("Название фильма не может быть пустым");
        }
        if (film.getDescription() != null && film.getDescription().length() > 200) {
            throw new ValidationException("Максимальная длина описания - 200 символов");
        }
        if (film.getReleaseDate() != null && film.getReleaseDate().isBefore(CINEMA_BIRTHDAY)) {
            throw new ValidationException("Дата релиза не раньше 28 декабря 1895 года");
        }
        if (film.getDuration() <= 0) {
            throw new ValidationException("Продолжительность фильма должна быть положительным числом");
        }
        log.debug("Film is valid: {}", film.getName());
    }
}