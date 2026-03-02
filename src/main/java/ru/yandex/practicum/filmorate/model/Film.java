package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Film {

    private static final LocalDate FIRST_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    private Integer id;

    @NotBlank(message = "Название фильма не может быть пустым")
    private String name;

    @Size(max = 200, message = "Описание фильма не может быть длиннее 200 символов")
    private String description;

    @NotNull(message = "Дата релиза не указана")
    private LocalDate releaseDate;

    @NotNull(message = "Продолжительность фильма должна быть указана")
    @Positive(message = "Продолжительность фильма должна быть положительным числом")
    private Integer duration;

    @AssertTrue(message = "Дата релиза не может быть раньше 28 декабря 1895 года")
    @JsonIgnore
    public boolean isReleaseDateValid() {
        if (releaseDate == null) {
            return true;
        }
        return !releaseDate.isBefore(FIRST_RELEASE_DATE);
    }
}
