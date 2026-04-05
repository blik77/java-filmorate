--delete from mpa_ratings;
--delete from genres;


MERGE INTO mpa_ratings (id, name) KEY(id)
VALUES
    (0, 'G'), (1, 'PG'), (2, 'PG-13'), (3, 'R'), (4, 'NC-17');

MERGE INTO genres (id, name) KEY(id)
VALUES
    (0, 'Комедия'), (1, 'Драма'), (2, 'Мультфильм'),
    (3, 'Триллер'), (4, 'Документальный'), (5, 'Боевик');