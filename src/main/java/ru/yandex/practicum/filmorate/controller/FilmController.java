package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
public class FilmController {
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);
    private static final LocalDate CINEMA_BIRTHDAY = LocalDate.of(1895, 12, 28);
    private final Map<Integer, Film> films = new HashMap<>();
    private int currentId = 1;

    @PostMapping
    public Film addFilm(@RequestBody Film film) {
        validate(film);
        film.setId(currentId++);
        films.put(film.getId(), film);
        log.info("Фильм добавлен: {}", film);
        return film;
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) {
        validate(film);
        if (!films.containsKey(film.getId())) {
            log.warn("Обновление фильма: id ={} не найден", film.getId());
            throw new ValidationException("Фильм с id=" + film.getId() + " не найден");
        }
        films.put(film.getId(), film);
        log.info("Фильм одновлен: {}", film);
        return film;
    }

    @GetMapping
    public Collection<Film> getAllFilms() {
        return films.values();
    }

    private void validate(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            log.warn("Валидация фильма не пройдена: пустое название");
            throw new ValidationException("Название фильма не может быть пустым");
        }
        if (film.getDescription() != null && film.getDescription().length() > 200) {
            log.warn("Валидация фильма не пройдена: описание > 200 символов");
            throw new ValidationException("Максимальная длина описания - 200 символов");
        }
        if (film.getReleaseDate() != null && film.getReleaseDate().isBefore(CINEMA_BIRTHDAY)) {
            log.warn("Валидация фильма не пройдена: релиз {} раньше {}", film.getReleaseDate(), CINEMA_BIRTHDAY);
            throw new ValidationException("Дата релиза не раньше 28 декабря 1895 года");
        }
        if (film.getDuration() <= 0) {
            log.warn("Валидация фильма не пройдена: неположительная длительность {}", film.getDuration());
            throw new ValidationException("Продолжительность фильма должна быть положительным числом");
        }
    }
}

