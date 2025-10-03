package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private static final Logger log = LoggerFactory.getLogger(FilmService.class);

    private final FilmStorage films;
    private final UserService users;

    @Autowired
    public FilmService(FilmStorage films, UserService users) {
        this.films = films;
        this.users = users;
    }

    public Film create(Film film) {
        return films.create(film);
    }

    public Film update(Film film) {
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
        Film film = getRequired(filmId);
        users.getRequired(userId); // проверим, что пользователь существует
        film.getLikes().add(userId); // Set гарантирует уникальность лайка
        log.info("User {} liked film {}", userId, filmId);
    }

    public void removeLike(int filmId, int userId) {
        Film film = getRequired(filmId);
        users.getRequired(userId);
        film.getLikes().remove(userId);
        log.info("User {} remove like from film {}", userId, filmId);
    }

    public List<Film> getPopular(int count) {
        return films.findAll().stream()
                .sorted(Comparator.comparingInt((Film f) -> f.getLikes().size()).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }
}