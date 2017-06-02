
CREATE TABLE IF NOT EXISTS categories(
                     	id SERIAL PRIMARY KEY,
                        name TEXT UNIQUE
                        );


CREATE TABLE IF NOT EXISTS events(
                     	id SERIAL PRIMARY KEY,
                        name TEXT,
                        date DATE,
                        description TEXT,
                        categoryId INTEGER DEFAULT 1 REFERENCES categories (id) on delete set default
                        );

INSERT into categories(name) values('general') ON CONFLICT DO NOTHING;