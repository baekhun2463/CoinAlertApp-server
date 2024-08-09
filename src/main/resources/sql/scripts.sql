CREATE DATABASE coinalertapp;

\c coinalertapp  -- Use the created database

CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(45) NOT NULL,
    password VARCHAR(45) NOT NULL,
    enabled INT NOT NULL
);

CREATE TABLE authorities (
    id SERIAL PRIMARY KEY,
    username VARCHAR(45) NOT NULL,
    authority VARCHAR(45) NOT NULL
);

INSERT INTO users (username, password, enabled) VALUES ('test', '1234', 1);
INSERT INTO authorities (username, authority) VALUES ('test', 'write');
