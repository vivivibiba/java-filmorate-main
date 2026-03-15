package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import(UserDbStorage.class)
@Sql({"classpath:schema.sql", "classpath:data.sql"})
class UserDbStorageTest {

    @Autowired
    private UserDbStorage userStorage;

    @Test
    void shouldCreateAndFindUserById() {
        User user = new User();
        user.setEmail("test@test.ru");
        user.setLogin("login");
        user.setName("name");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        User created = userStorage.create(user);

        assertThat(created.getId()).isNotNull();

        User found = userStorage.findById(created.getId());
        assertThat(found).isNotNull();
        assertThat(found.getEmail()).isEqualTo("test@test.ru");
    }

    @Test
    void shouldUpdateUser() {
        User user = new User();
        user.setEmail("test@test.ru");
        user.setLogin("login");
        user.setName("name");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        User created = userStorage.create(user);

        created.setName("new name");
        userStorage.update(created);

        User found = userStorage.findById(created.getId());
        assertThat(found).isNotNull();
        assertThat(found.getName()).isEqualTo("new name");
    }

    @Test
    void shouldFindAllUsers() {
        User user1 = new User();
        user1.setEmail("u1@test.ru");
        user1.setLogin("u1");
        user1.setName("u1");
        user1.setBirthday(LocalDate.of(2000, 1, 1));
        userStorage.create(user1);

        User user2 = new User();
        user2.setEmail("u2@test.ru");
        user2.setLogin("u2");
        user2.setName("u2");
        user2.setBirthday(LocalDate.of(2000, 1, 1));
        userStorage.create(user2);

        List<User> users = userStorage.findAll();
        assertThat(users).hasSize(2);
    }

    @Test
    void shouldDeleteUser() {
        User user = new User();
        user.setEmail("del@test.ru");
        user.setLogin("del");
        user.setName("del");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        User created = userStorage.create(user);
        userStorage.delete(created.getId());

        assertThat(userStorage.findById(created.getId())).isNull();
    }
}
