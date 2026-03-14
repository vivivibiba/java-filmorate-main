package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.like.LikeStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final LikeStorage likeStorage;
    private final GenreStorage genreStorage;
    private final MpaStorage mpaStorage;

    public FilmService(
            @Qualifier("filmDbStorage") FilmStorage filmStorage,
            @Qualifier("userDbStorage") UserStorage userStorage,
            LikeStorage likeStorage,
            GenreStorage genreStorage,
            MpaStorage mpaStorage
    ) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.likeStorage = likeStorage;
        this.genreStorage = genreStorage;
        this.mpaStorage = mpaStorage;
    }

    public Film create(Film film) {
        prepareFilm(film);
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        getFilmOrThrow(film.getId());
        prepareFilm(film);
        return filmStorage.update(film);
    }

    public void delete(int id) {
        filmStorage.delete(id);
    }

    public List<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film findById(int id) {
        return getFilmOrThrow(id);
    }

    public void addLike(int filmId, int userId) {
        getFilmOrThrow(filmId);
        getUserOrThrow(userId);
        likeStorage.addLike(filmId, userId);
    }

    public void removeLike(int filmId, int userId) {
        getFilmOrThrow(filmId);
        getUserOrThrow(userId);
        likeStorage.removeLike(filmId, userId);
    }

    public List<Film> getPopular(int count) {
        if (count <= 0) {
            throw new ValidationException("Параметр count должен быть положительным");
        }

        List<Integer> filmIds = likeStorage.getPopularFilmIds(count);
        List<Film> films = new ArrayList<>();
        for (Integer filmId : filmIds) {
            Film film = filmStorage.findById(filmId);
            if (film != null) {
                films.add(film);
            }
        }
        return films;
    }

    private Film getFilmOrThrow(int id) {
        Film film = filmStorage.findById(id);
        if (film == null) {
            throw new NotFoundException("Фильм с id " + id + " не найден");
        }
        return film;
    }

    private User getUserOrThrow(int id) {
        User user = userStorage.findById(id);
        if (user == null) {
            throw new NotFoundException("Пользователь с id " + id + " не найден");
        }
        return user;
    }

    private void prepareFilm(Film film) {
        if (film.getMpa() == null || film.getMpa().getId() == null) {
            throw new ValidationException("Рейтинг MPA должен быть указан");
        }

        Mpa mpa = mpaStorage.findById(film.getMpa().getId());
        if (mpa == null) {
            throw new NotFoundException("Рейтинг MPA с id " + film.getMpa().getId() + " не найден");
        }
        film.setMpa(mpa);

        if (film.getGenres() == null) {
            film.setGenres(new ArrayList<>());
            return;
        }

        Set<Integer> genreIds = new HashSet<>();
        for (Genre genre : film.getGenres()) {
            if (genre == null || genre.getId() == null) {
                throw new ValidationException("Жанр должен содержать id");
            }
            genreIds.add(genre.getId());
        }

        List<Genre> genres = new ArrayList<>();
        for (Integer genreId : genreIds) {
            Genre dbGenre = genreStorage.findById(genreId);
            if (dbGenre == null) {
                throw new NotFoundException("Жанр с id " + genreId + " не найден");
            }
            genres.add(dbGenre);
        }

        genres.sort(Comparator.comparingInt(Genre::getId));
        film.setGenres(genres);
    }
}
