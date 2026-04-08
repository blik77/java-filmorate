CREATE TABLE IF NOT EXISTS mpa_ratings (
    id SERIAL PRIMARY KEY,
    name VARCHAR(10) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS genres (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    login VARCHAR(100) NOT NULL UNIQUE,
    name VARCHAR(100),
    birthday DATE NOT NULL
);

CREATE TABLE IF NOT EXISTS films (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    release_date DATE NOT NULL,
    duration INT NOT NULL CHECK (duration > 0),
    mpa_id INT NOT NULL,
    CONSTRAINT fk_mpa FOREIGN KEY (mpa_id)
    REFERENCES mpa_ratings (id)
    ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS film_genres (
    film_id INT NOT NULL,
    genre_id INT NOT NULL,
    PRIMARY KEY (film_id, genre_id),
    CONSTRAINT fk_film FOREIGN KEY (film_id)
    REFERENCES films (id)
    ON DELETE CASCADE,
    CONSTRAINT fk_genre FOREIGN KEY (genre_id)
    REFERENCES genres (id)
    ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS likes (
    user_id INT NOT NULL,
    film_id INT NOT NULL,
    PRIMARY KEY (user_id, film_id),
    CONSTRAINT fk_like_user FOREIGN KEY (user_id)
    REFERENCES users (id)
    ON DELETE CASCADE,
    CONSTRAINT fk_like_film FOREIGN KEY (film_id)
    REFERENCES films (id)
    ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS friendships (
    user_id INT NOT NULL,
    friend_id INT NOT NULL,
    status VARCHAR(20) NOT NULL CHECK (status IN ('PENDING', 'CONFIRMED')),
    PRIMARY KEY (user_id, friend_id),
    CONSTRAINT fk_user FOREIGN KEY (user_id)
    REFERENCES users (id)
    ON DELETE CASCADE,
    CONSTRAINT fk_friend FOREIGN KEY (friend_id)
    REFERENCES users (id)
    ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_film_mpa ON films(mpa_id);
CREATE INDEX IF NOT EXISTS idx_film_genre_genre_id ON film_genres(genre_id);
CREATE INDEX IF NOT EXISTS idx_likes_film_id ON likes(film_id);
CREATE INDEX IF NOT EXISTS idx_friendships_friend_id ON friendships(friend_id);