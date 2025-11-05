package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.List;

@Service
public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserStorage users;
    private final UserDbStorage userDb;

    @Autowired
    public UserService(@Qualifier("dbUserStorage") UserStorage users, UserDbStorage userDb) {
        this.users = users;
        this.userDb = userDb;
    }

    public User create(User user) {
        return users.create(user);
    }

    public User update(User user) {
        getRequired(user.getId());
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
        getRequired(userId);
        getRequired(friendId);
        userDb.addFriend(userId, friendId);
        log.info("Users {} sent friend request to {}", userId, friendId);
    }

    public void removeFriend(int userId, int friendId) {
        getRequired(userId);
        getRequired(friendId);
        userDb.removeFriend(userId, friendId);
        log.info("Users {} and {} are no longer friends", userId, friendId);
    }

    public List<User> getFriends(int userId) {
        getRequired(userId);
        return userDb.getFriends(userId);
    }

    public List<User> getCommonFriends(int userId, int otherId) {
        getRequired(userId);
        getRequired(otherId);
        return userDb.getCommonFriends(userId, otherId);
    }

    public boolean isFriendshipConfirmed(int a, int b) {
        return userDb.friendshipConfirmed(a, b);
    }
}
