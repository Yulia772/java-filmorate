package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserStorage users;

    @Autowired
    public UserService(UserStorage users) {
        this.users = users;
    }

    public User create(User user) {
        return users.create(user);
    }

    public User update(User user) {
        return users.update(user);
    }

    public Collection<User> getAll() {
        return users.findAll();
    }

    public User getRequired(int id) {
        User u = users.findById(id);
        if (u == null) {
            throw new NotFoundException("Пользователь с id=" + id + " не найден");
        }
        return u;
    }

    public void addFriend(int userId, int friendId) {
        User u = getRequired(userId);
        User f = getRequired(friendId);
        u.getFriends().add(friendId);
        f.getFriends().add(userId);
        log.info("Users {} and {} are now friends", userId, friendId);
    }

    public void removeFriend(int userId, int friendId) {
        User u = getRequired(userId);
        User f = getRequired(friendId);
        u.getFriends().remove(friendId);
        f.getFriends().remove(userId);
        log.info("Users {} and {} are no longer friends", userId, friendId);
    }

    public List<User> getFriends(int userId) {
        return getRequired(userId).getFriends().stream()
                .map(this::getRequired)
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(int userId, int otherId) {
        Set<Integer> a = getRequired(userId).getFriends();
        Set<Integer> b = getRequired(otherId).getFriends();
        return a.stream().filter(b::contains)
                .map(this::getRequired)
                .collect(Collectors.toList());
    }
}
