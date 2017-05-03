CREATE TABLE IF NOT EXISTS events(
                     	id SERIAL PRIMARY KEY,
                        name TEXT,
                        date DATE,
                        description TEXT,
                        category TEXT
                        );

CREATE TABLE IF NOT EXISTS categories(
                     	id SERIAL PRIMARY KEY,
                        name TEXT UNIQUE
                        );
