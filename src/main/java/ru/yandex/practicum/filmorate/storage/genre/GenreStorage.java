package ru.yandex.practicum.filmorate.storage.genre;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.List;

public interface GenreStorage {
    Collection<Genre> getAll();

    Genre findById(Long id);

    Collection<Genre> findAllByIds(Collection<Long> ids);

    void loadGenresForFilms(List<Film> films);
}