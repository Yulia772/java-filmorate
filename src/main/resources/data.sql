-- Рейтинги MPAA
MERGE INTO mpa (id, code, name) KEY(id) VALUES
(1, 'G', 'G'),
(2, 'PG', 'PG'),
(3, 'PG-13', 'PG-13'),
(4, 'R', 'R'),
(5, 'NC-17', 'NC-17');

-- Жанры
MERGE INTO genres (id, name) KEY(id) VALUES
(1, 'Комедия'),
(2, 'Драма'),
(3, 'Мультфильм'),
(4, 'Триллер'),
(5, 'Документальный'),
(6, 'Боевик');