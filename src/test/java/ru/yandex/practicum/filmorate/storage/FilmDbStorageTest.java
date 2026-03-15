package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import(FilmDbStorage.class)
@Sql({"classpath:schema.sql", "classpath:data.sql"})
class FilmDbStorageTest {

    @Autowired
    private FilmDbStorage filmStorage;

    @Test
    void shouldCreateAndFindFilmById() {
        Film film = makeFilm();
        Film created = filmStorage.create(film);

        assertThat(created.getId()).isNotNull();
        assertThat(created.getMpa()).isNotNull();
        assertThat(created.getMpa().getName()).isNotBlank();
        assertThat(created.getGenres()).hasSize(1);
        assertThat(created.getGenres().get(0).getName()).isNotBlank();

        Film found = filmStorage.findById(created.getId());
        assertThat(found).isNotNull();
        assertThat(found.getName()).isEqualTo("Film");
    }

    @Test
    void shouldUpdateFilm() {
        Film film = makeFilm();
        Film created = filmStorage.create(film);

        created.setName("Updated");
        filmStorage.update(created);

        Film found = filmStorage.findById(created.getId());
        assertThat(found).isNotNull();
        assertThat(found.getName()).isEqualTo("Updated");
    }

    @Test
    void shouldFindAllFilms() {
        filmStorage.create(makeFilm());
        filmStorage.create(makeFilm());

        List<Film> films = filmStorage.findAll();
        assertThat(films).hasSize(2);
    }

    @Test
    void shouldDeleteFilm() {
        Film created = filmStorage.create(makeFilm());
        filmStorage.delete(created.getId());

        assertThat(filmStorage.findById(created.getId())).isNull();
    }

    private Film makeFilm() {
        Film film = new Film();
        film.setName("Film");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        Mpa mpa = new Mpa();
        mpa.setId(1);
        film.setMpa(mpa);

        Genre genre = new Genre();
        genre.setId(1);
        film.getGenres().add(genre);

        return film;
    }
}
