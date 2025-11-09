package ru.yandex.practicum.filmorate.storage.genre;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class GenreDbStorage {

    private final JdbcTemplate jdbc;

    public List<Genre> findAll() {
        String sql = "SELECT id, name FROM genres ORDER BY id";
        return jdbc.query(sql, this::mapRowToGenre);
    }

    public Genre findById(int id) {
        String sql = "SELECT id, name FROM genres WHERE id = ?";
        List<Genre> list = jdbc.query(sql, this::mapRowToGenre, id);
        if (list.isEmpty()) {
            throw new NotFoundException("Жанр с id=" + id + " не найден");
        }
        return list.get(0);
    }

    private Genre mapRowToGenre(ResultSet rs, int rowNum) throws SQLException {
        Genre g = new Genre();
        g.setId(rs.getInt("id"));
        g.setName(rs.getString("name"));
        return g;
    }
}
