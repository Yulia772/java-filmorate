package ru.yandex.practicum.filmorate.storage.mpa;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class MpaDbStorage {

    private final JdbcTemplate jdbc;

    public List<Mpa> findAll() {
        String sql = "SELECT id, name FROM mpa ORDER BY id";
        return jdbc.query(sql, this::mapRowToMpa);
    }

    public Mpa findById(int id) {
        String sql = "SELECT id, name FROM mpa WHERE id = ?";
        List<Mpa> list = jdbc.query(sql, this::mapRowToMpa, id);
        if (list.isEmpty()) {
            throw new NotFoundException("Жанр с id=" + id + " не найден");
        }
        return list.get(0);
    }

    private Mpa mapRowToMpa(ResultSet rs, int rowNum) throws SQLException {
        Mpa m = new Mpa();
        m.setId(rs.getInt("id"));
        m.setName(rs.getString("name"));
        return m;
    }
}