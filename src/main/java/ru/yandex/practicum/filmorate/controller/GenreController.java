package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.util.Collection;

@RestController
@RequestMapping("/genres")
@Slf4j
public class GenreController {

    private final GenreStorage genreStorage;

    public GenreController(GenreStorage genreStorage) {
        this.genreStorage = genreStorage;
    }

    @GetMapping
    public Collection<Genre> getAll() {
        return genreStorage.getAll();
    }

    @GetMapping("/{id}")
    public Genre getById(@PathVariable Long id) {
        return genreStorage.findById(id);
    }
}