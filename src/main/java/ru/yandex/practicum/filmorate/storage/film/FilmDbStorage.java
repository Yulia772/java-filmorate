package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;

@Repository("dbFilmStorage")
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbc;

    @Override
    public Film create(Film film) {
        if (film.getMpa() == null) {
            throw new ValidationException("MPA обязателен");
        }
        checkMpaExistsOrThrow(film.getMpa().getId());
        checkGenresExistOrThrow(film.getGenres());

        String sql = "INSERT INTO films(name, description, release_date, duration, mpa_id) VALUES (?,?,?,?,?)";
        KeyHolder kh = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, film.getReleaseDate() == null ? null : Date.valueOf(film.getReleaseDate()));
            ps.setInt(4, film.getDuration());
            ps.setInt(5, film.getMpa().getId());
            return ps;
        }, kh);
        film.setId(kh.getKey().intValue());

        saveGenres(film.getId(), film.getGenres());

        return findById(film.getId());
    }

    @Override
    public Film update(Film film) {
        Film current = findById(film.getId());
        if (current == null) {
            throw new NotFoundException("Фильм с id=" + film.getId() + " не найден");
        }

        if (film.getMpa() == null) {
            throw new ValidationException("MPA обязателен");
        }
        checkMpaExistsOrThrow(film.getMpa().getId());
        checkGenresExistOrThrow(film.getGenres());

        String sql = "UPDATE films SET name=?, description=?, release_date=?, duration=?, mpa_id=? WHERE id=?";
        jdbc.update(sql,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate() == null ? null : Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId()
        );

        saveGenres(film.getId(), film.getGenres());

        return findById(film.getId());
    }

    @Override
    public Film findById(int id) {
        String sql = """
                SELECT f.id, f.name, f.description, f.release_date, f.duration,
                       m.id AS mpa_id, m.name AS mpa_name
                FROM films f
                JOIN mpa m ON m.id = f.mpa_id
                WHERE f.id = ?
                """;
        List<Film> list = jdbc.query(sql, (rs, rn) -> {
            Film f = new Film();
            f.setId(rs.getInt("id"));
            f.setName(rs.getString("name"));
            f.setDescription(rs.getString("description"));
            Date rd = rs.getDate("release_date");
            f.setReleaseDate(rd == null ? null : rd.toLocalDate());
            f.setDuration(rs.getInt("duration"));

            Mpa mpa = new Mpa();
            mpa.setId(rs.getInt("mpa_id"));
            mpa.setName(rs.getString("mpa_name"));
            f.setMpa(mpa);
            return f;
        }, id);

        if (list.isEmpty()) {
            return null;
        }
        Film film = list.get(0);
        film.setGenres(loadGenres(film.getId()));
        return film;
    }

    @Override
    public Collection<Film> findAll() {
        String sql = """
                SELECT f.id, f.name, f.description, f.release_date, f.duration,
                       m.id AS mpa_id, m.name AS mpa_name
                FROM films f
                JOIN mpa m ON m.id = f.mpa_id
                ORDER BY f.id
                """;
        List<Film> films = jdbc.query(sql, (rs, rn) -> {
            Film f = new Film();
            f.setId(rs.getInt("id"));
            f.setName(rs.getString("name"));
            f.setDescription(rs.getString("description"));
            Date rd = rs.getDate("release_date");
            f.setReleaseDate(rd == null ? null : rd.toLocalDate());
            f.setDuration(rs.getInt("duration"));

            Mpa mpa = new Mpa();
            mpa.setId(rs.getInt("mpa_id"));
            mpa.setName(rs.getString("mpa_name"));
            f.setMpa(mpa);
            return f;
        });

        for (Film f : films) {
            f.setGenres(loadGenres(f.getId()));
        }
        return films;
    }

    private void saveGenres(int filmId, Collection<Genre> genres) {
        jdbc.update("DELETE FROM film_genres WHERE film_id=?", filmId);
        if (genres == null || genres.isEmpty()) return;

        String ins = "INSERT INTO film_genres(film_id, genre_id) VALUES(?, ?)";
        for (Genre g : new LinkedHashSet<>(genres)) {
            jdbc.update(ins, filmId, g.getId());
        }
    }

    private LinkedHashSet<Genre> loadGenres(int filmId) {
        String sql = """
                SELECT g.id, g.name
                FROM film_genres fg
                JOIN genres g ON g.id = fg.genre_id
                WHERE fg.film_id = ?
                ORDER BY g.id
                """;
        List<Genre> list = jdbc.query(sql, (rs, rn) -> {
            Genre g = new Genre();
            g.setId(rs.getInt("id"));
            g.setName(rs.getString("name"));
            return g;
        }, filmId);
        return new LinkedHashSet<>(list);
    }

    public void addLike(int filmId, int userId) {
        jdbc.update("MERGE INTO likes (film_id, user_id) KEY(film_id, user_id) VALUES (?, ?)", filmId, userId);
    }

    public void removeLike(int filmId, int userId) {
        jdbc.update("DELETE FROM likes WHERE film_id=? AND user_id=?", filmId, userId);
    }

    public List<Film> findPopular(int count) {
        String sql = """
                SELECT f.id, f.name, f.description, f.release_date, f.duration,
                       m.id AS mpa_id, m.name AS mpa_name,
                       COUNT(l.user_id) AS like_cnt
                FROM films f
                JOIN mpa m ON m.id = f.mpa_id
                LEFT JOIN likes l ON l.film_id = f.id
                GROUP BY f.id, f.name, f.description, f.release_date, f.duration, m.id, m.name
                ORDER BY like_cnt DESC, f.id
                LIMIT ?
                """;
        List<Film> list = jdbc.query(sql, (rs, rn) -> {
            Film f = new Film();
            f.setId(rs.getInt("id"));
            f.setName(rs.getString("name"));
            f.setDescription(rs.getString("description"));
            var d = rs.getDate("release_date");
            f.setReleaseDate(d == null ? null : d.toLocalDate());
            f.setDuration(rs.getInt("duration"));
            Mpa m = new Mpa();
            m.setId(rs.getInt("mpa_id"));
            m.setName(rs.getString("mpa_name"));
            f.setMpa(m);
            return f;
        }, count);
        for (Film f : list) {
            f.setGenres(loadGenres(f.getId()));
        }
        return list;
    }

    private void checkMpaExistsOrThrow(int mpaId) {
        Integer cnt = jdbc.queryForObject(
                "SELECT COUNT(*) FROM mpa WHERE id = ?",
                Integer.class, mpaId
        );
        if (cnt == null || cnt == 0) {
            throw new NotFoundException("Рейтинг MPA id=" + mpaId + " не найден");
        }
    }

    private void checkGenresExistOrThrow(Collection<Genre> genres) {
        if (genres == null || genres.isEmpty()) return;
        for (Genre g : genres) {
            Integer cnt = jdbc.queryForObject(
                    "SELECT COUNT(*) FROM genres WHERE id = ?",
                    Integer.class, g.getId()
            );
            if (cnt == null || cnt == 0) {
                throw new NotFoundException("Жанр id=" + g.getId() + " не найден");
            }
        }
    }
}