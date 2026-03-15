package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDbStorage;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import(MpaDbStorage.class)
@Sql({"classpath:schema.sql", "classpath:data.sql"})
class MpaDbStorageTest {

    @Autowired
    private MpaDbStorage mpaStorage;

    @Test
    void shouldReturnAllMpa() {
        assertThat(mpaStorage.findAll()).hasSize(5);
    }

    @Test
    void shouldFindMpaById() {
        assertThat(mpaStorage.findById(1)).isNotNull();
        assertThat(mpaStorage.findById(1).getName()).isNotBlank();
    }
}
