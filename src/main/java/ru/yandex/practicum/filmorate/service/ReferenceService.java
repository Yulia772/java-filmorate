package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDbStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReferenceService {

    private final GenreDbStorage genreDbStorage;
    private final MpaDbStorage mpaDbStorage;

    public List<Genre> getAllGenres() {
        return genreDbStorage.findAll();
    }

    public Genre getGenreById(int id) {
        return genreDbStorage.findById(id);
    }

    public List<Mpa> getAllMpa() {
        return mpaDbStorage.findAll();
    }

    public Mpa getMpaById(int id) {
        return mpaDbStorage.findById(id);
    }
}
