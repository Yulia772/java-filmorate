package ru.yandex.practicum.filmorate.storage.film;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component("inMemoryFilmStorage")
public class InMemoryFilmStorage implements FilmStorage {
    private static final Logger log = LoggerFactory.getLogger(InMemoryFilmStorage.class);

    private final Map<Integer, Film> films = new HashMap<>();
    private int currentId = 1;

    @Override
    public Film create(Film film) {
        film.setId(currentId++);
        films.put(film.getId(), film);
        log.info("Film created: {}", film);
        return film;
    }

    @Override
    public Film update(Film film) {
        int id = film.getId();
        if (!films.containsKey(id)) {
            throw new NotFoundException("Фильм с id=" + id + "не найден");
        }
        films.put(id, film);
        log.info("Film updated: {}", film);
        return film;
    }

    @Override
    public Film findById(int id) {
        return films.get(id); // null если не найден
    }

    @Override
    public Collection<Film> findAll() {
        return films.values();
    }
}