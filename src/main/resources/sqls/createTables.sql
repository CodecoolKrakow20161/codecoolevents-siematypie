CREATE TABLE IF NOT EXISTS events(
                     	id SERIAL PRIMARY KEY,
                        name TEXT,
                        date DATE,
                        description TEXT,
                        categoryId INTEGER
                        );

CREATE TABLE IF NOT EXISTS categories(
                     	id SERIAL PRIMARY KEY,
                        name TEXT UNIQUE
                        );
