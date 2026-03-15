package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import(GenreDbStorage.class)
@Sql({"classpath:schema.sql", "classpath:data.sql"})
class GenreDbStorageTest {

    @Autowired
    private GenreDbStorage genreStorage;

    @Test
    void shouldReturnAllGenres() {
        assertThat(genreStorage.findAll()).hasSize(6);
    }

    @Test
    void shouldFindGenreById() {
        assertThat(genreStorage.findById(1)).isNotNull();
        assertThat(genreStorage.findById(1).getName()).isNotBlank();
    }
}
