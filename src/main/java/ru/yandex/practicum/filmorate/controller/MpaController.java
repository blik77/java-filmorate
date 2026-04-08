package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.util.Collection;

@RestController
@RequestMapping("/mpa")
@Slf4j
public class MpaController {

    private final MpaStorage mpaStorage;

    public MpaController(MpaStorage mpaStorage) {
        this.mpaStorage = mpaStorage;
    }

    @GetMapping
    public Collection<MpaRating> getAll() {
        return mpaStorage.getAll();
    }

    @GetMapping("/{id}")
    public MpaRating getById(@PathVariable Long id) {
        return mpaStorage.findById(id);
    }
}