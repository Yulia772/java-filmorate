package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import(UserDbStorage.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmorateApplicationTests {

    private final UserDbStorage userStorage;

    @Test
    void testFindUserById() {
        // arrange
        User u = new User();
        u.setEmail("a@b.c");
        u.setLogin("alice");
        u.setName("Alice");
        u.setBirthday(LocalDate.of(1990, 1, 1));
        u = userStorage.create(u);

        // act
        User found = userStorage.findById(u.getId());

        // assert
        assertThat(found)
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", u.getId());
    }
}