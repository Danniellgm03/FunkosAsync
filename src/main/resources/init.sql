CREATE TABLE IF NOT EXISTS funkos(
    id INT AUTO_INCREMENT PRIMARY KEY,
    cod UUID not null,
    myId LONG,
    name VARCHAR(255),
    model VARCHAR(20) CHECK (model IN('MARVEL', 'DISNEY', 'ANIME', 'OTROS')),
    price REAL,
    release_date DATE,
    created_at TIMESTAMP DEFAULT  CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT  CURRENT_TIMESTAMP NOT NULL
);

INSERT INTO funkos (cod, myId, name, model, price, release_date) VALUES('999c6f58-79b9-434b-82ab-01a2d6e4434a', 2,  'Spiderman', 'MARVEL', 15.99, '2022-05-01');