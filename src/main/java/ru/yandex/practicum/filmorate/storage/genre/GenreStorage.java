package ru.yandex.practicum.filmorate.storage.genre;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface GenreStorage {
    Collection<Genre> getAll();

    Genre findById(Long id);

    Collection<Genre> findAllByIds(Collection<Long> ids);

    Map<Long, Set<Genre>> loadGenresForFilms(List<Long> filmIds);
}