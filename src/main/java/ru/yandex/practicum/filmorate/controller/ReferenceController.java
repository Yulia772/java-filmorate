package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ReferenceController {

    private final JdbcTemplate jdbc;

    @GetMapping("/genres")
    public List<Genre> getGenres() {
        return jdbc.query("SELECT * FROM genres ORDER BY id",
                (rs, i) -> {
                    Genre g = new Genre();
                    g.setId(rs.getInt("id"));
                    g.setName(rs.getString("name"));
                    return g;
                });
    }

    @GetMapping("/genres/{id}")
    public Genre getGenre(@PathVariable int id) {
        List<Genre> list = jdbc.query("SELECT * FROM genres WHERE id=?",
                (rs, i) -> {
                    Genre g = new Genre();
                    g.setId(rs.getInt("id"));
                    g.setName(rs.getString("name"));
                    return g;
                }, id);
        if (list.isEmpty()) throw new NotFoundException("Жанр не найден: id=" + id);
        return list.get(0);
    }

    @GetMapping("/mpa")
    public List<Mpa> getMpaAll() {
        return jdbc.query("SELECT * FROM mpa ORDER BY id",
                (rs, i) -> {
                    Mpa m = new Mpa();
                    m.setId(rs.getInt("id"));
                    m.setName(rs.getString("name"));
                    return m;
                });
    }

    @GetMapping("/mpa/{id}")
    public Mpa getMpa(@PathVariable int id) {
        List<Mpa> list = jdbc.query("SELECT * FROM mpa WHERE id=?",
                (rs, i) -> {
                    Mpa m = new Mpa();
                    m.setId(rs.getInt("id"));
                    m.setName(rs.getString("name"));
                    return m;
                }, id);
        if (list.isEmpty()) throw new NotFoundException("Рейтинг не найден: id=" + id);
        return list.get(0);
    }
}