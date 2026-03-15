package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.friend.FriendDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import({UserDbStorage.class, FriendDbStorage.class})
@Sql({"classpath:schema.sql", "classpath:data.sql"})
class FriendDbStorageTest {

    @Autowired
    private UserDbStorage userStorage;

    @Autowired
    private FriendDbStorage friendStorage;

    @Test
    void shouldAddAndRemoveFriend() {
        int userId = userStorage.create(makeUser("u1@test.ru", "u1")).getId();
        int friendId = userStorage.create(makeUser("u2@test.ru", "u2")).getId();

        friendStorage.addFriend(userId, friendId);

        assertThat(friendStorage.getFriendIds(userId)).containsExactly(friendId);

        friendStorage.removeFriend(userId, friendId);

        assertThat(friendStorage.getFriendIds(userId)).isEmpty();
    }

    @Test
    void shouldReturnCommonFriends() {
        int userId1 = userStorage.create(makeUser("u1@test.ru", "u1")).getId();
        int userId2 = userStorage.create(makeUser("u2@test.ru", "u2")).getId();
        int commonFriendId = userStorage.create(makeUser("u3@test.ru", "u3")).getId();

        friendStorage.addFriend(userId1, commonFriendId);
        friendStorage.addFriend(userId2, commonFriendId);

        assertThat(friendStorage.getCommonFriendIds(userId1, userId2)).containsExactly(commonFriendId);
    }

    private User makeUser(String email, String login) {
        User user = new User();
        user.setEmail(email);
        user.setLogin(login);
        user.setName(login);
        user.setBirthday(LocalDate.of(2000, 1, 1));
        return user;
    }
}
