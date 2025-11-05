package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureTestDatabase
class UserServiceTest {

    @Autowired
    private UserService userService;

    private User newUser(String email, String login) {
        User u = new User();
        u.setEmail(email);
        u.setLogin(login);
        u.setName(login);
        u.setBirthday(LocalDate.of(1990, 1, 1));
        return userService.create(u);
    }

    @Test
    void addFriend_isOneWay() {
        User a = newUser("a@a.ru", "a");
        User b = newUser("b@b.ru", "b");

        userService.addFriend(a.getId(), b.getId());

        List<User> aFriends = userService.getFriends(a.getId());
        List<User> bFriends = userService.getFriends(b.getId());

        assertEquals(1, aFriends.size());
        assertEquals(b.getId(), aFriends.get(0).getId());
        assertTrue(bFriends.isEmpty(), "Дружба должна быть односторонней до подтверждения");
    }

    @Test
    void addFriend_confirmedWhenBothSidesAdd() {
        User a = newUser("a@a.ru", "a");
        User b = newUser("b@b.ru", "b");

        userService.addFriend(a.getId(), b.getId()); // заявка a -> b
        userService.addFriend(b.getId(), a.getId()); // встречная — теперь подтверждена

        List<User> aFriends = userService.getFriends(a.getId());
        List<User> bFriends = userService.getFriends(b.getId());

        assertEquals(1, aFriends.size());
        assertEquals(1, bFriends.size());
        assertEquals(b.getId(), aFriends.get(0).getId());
        assertEquals(a.getId(), bFriends.get(0).getId());
    }

    @Test
    void removeFriend_isOneWay() {
        User a = newUser("a@a.ru", "a");
        User b = newUser("b@b.ru", "b");

        userService.addFriend(a.getId(), b.getId()); // a -> b
        userService.removeFriend(a.getId(), b.getId()); // убрали только это направление

        assertTrue(userService.getFriends(a.getId()).isEmpty());
        // у b по-прежнему пусто (он и не добавлял a)
        assertTrue(userService.getFriends(b.getId()).isEmpty());
    }

    @Test
    void commonFriends_returnsIntersection() {
        User a = newUser("a@a.ru", "a");
        User b = newUser("b@b.ru", "b");
        User c = newUser("c@c.ru", "c");

        userService.addFriend(a.getId(), c.getId());
        userService.addFriend(b.getId(), c.getId());

        List<User> common = userService.getCommonFriends(a.getId(), b.getId());
        assertEquals(1, common.size());
        assertEquals(c.getId(), common.get(0).getId());
    }
}