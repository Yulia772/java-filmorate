package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserServiceTest {

    private final UserStorage userStorage = new InMemoryUserStorage();
    private final UserService userService = new UserService(userStorage);

    private User newUser(int id, String email, String login) {
        User u = new User();
        u.setId(id);
        u.setEmail(email);
        u.setLogin(login);
        u.setName(login);
        u.setBirthday(LocalDate.of(1990, 1, 1));
        userStorage.create(u);
        return u;
    }

    @Test
    void addFriend_bidirectional() {
        User a = newUser(1, "a@a.ru", "a");
        User b = newUser(2, "b@b.ru", "b");

        userService.addFriend(a.getId(), b.getId());

        List<User> aFriends = userService.getFriends(a.getId());
        List<User> bFriends = userService.getFriends(b.getId());

        assertEquals(1, aFriends.size());
        assertEquals(b.getId(), aFriends.get(0).getId());
        assertEquals(1, bFriends.size());
        assertEquals(a.getId(), bFriends.get(0).getId());
    }

    @Test
    void commonFriends_returnsIntersection() {
        User a = newUser(1, "a@a.ru", "a");
        User b = newUser(2, "b@b.ru", "b");
        User c = newUser(3, "c@c.ru", "c");

        userService.addFriend(a.getId(), c.getId());
        userService.addFriend(b.getId(), c.getId());

        List<User> common = userService.getCommonFriends(a.getId(), b.getId());
        assertEquals(1, common.size());
        assertEquals(c.getId(), common.get(0).getId());
    }

    @Test
    void removeFriend() {
        User a = newUser(1, "a@a.ru", "a");
        User b = newUser(2, "b@b.ru", "b");

        userService.addFriend(a.getId(), b.getId());
        userService.removeFriend(a.getId(), b.getId());

        assertTrue(userService.getFriends(a.getId()).isEmpty());
        assertTrue(userService.getFriends(b.getId()).isEmpty());
    }
}