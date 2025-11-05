package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.Collection;
import java.util.List;

@Repository("dbUserStorage")
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbc;

    private User mapRow(java.sql.ResultSet rs, int rowNum) throws java.sql.SQLException {
        User u = new User();
        u.setId(rs.getInt("id"));
        u.setEmail(rs.getString("email"));
        u.setLogin(rs.getString("login"));
        u.setName(rs.getString("name"));
        Date bd = rs.getDate("birthday");
        if (bd != null) u.setBirthday(bd.toLocalDate());
        return u;
    }

    @Override
    public User create(User user) {
        String sql = "INSERT INTO users(email, login, name, birthday) VALUES (?, ?, ?, ?)";
        KeyHolder kh = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getLogin());
            ps.setString(3, user.getName());
            ps.setDate(4, user.getBirthday() == null ? null : Date.valueOf(user.getBirthday()));
            return ps;
        }, kh);
        user.setId(kh.getKey().intValue());
        return user;
    }

    @Override
    public User update(User user) {
        String sql = "UPDATE users SET email=?, login=?, name=?, birthday=? WHERE id=?";
        jdbc.update(sql,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday() == null ? null : Date.valueOf(user.getBirthday()),
                user.getId());
        return findById(user.getId());
    }

    @Override
    public User findById(int id) {
        List<User> list = jdbc.query("SELECT * FROM users WHERE id=?", this::mapRow, id);
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public Collection<User> findAll() {
        return jdbc.query("SELECT * FROM users ORDER BY id", this::mapRow);
    }

    /* ===== ДРУЖБА (односторонняя) ===== */

    public void addFriend(int userId, int friendId) {
        jdbc.update("MERGE INTO friendships(user_id, friend_id) KEY(user_id, friend_id) VALUES (?,?)",
                userId, friendId);
    }

    public void removeFriend(int userId, int friendId) {
        jdbc.update("DELETE FROM friendships WHERE user_id=? AND friend_id=?", userId, friendId);
    }

    public List<User> getFriends(int userId) {
        String sql = """
                    SELECT u.* FROM friendships f
                    JOIN users u ON u.id = f.friend_id
                    WHERE f.user_id = ?
                    ORDER BY u.id
                """;
        return jdbc.query(sql, this::mapRow, userId);
    }

    public List<User> getCommonFriends(int a, int b) {
        String sql = """
                    SELECT u.* FROM friendships f1
                    JOIN friendships f2 ON f1.friend_id = f2.friend_id
                    JOIN users u ON u.id = f1.friend_id
                    WHERE f1.user_id = ? AND f2.user_id = ?
                    ORDER BY u.id
                """;
        return jdbc.query(sql, this::mapRow, a, b);
    }

    public boolean friendshipConfirmed(int userId, int friendId) {
        Integer cnt = jdbc.queryForObject(
                "SELECT COUNT(*) FROM friendships WHERE user_id=? AND friend_id=?",
                Integer.class, friendId, userId);
        return cnt != null && cnt > 0;
    }
}
