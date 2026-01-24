CREATE TABLE IF NOT EXISTS fibonacci_results
(
    id        SERIAL PRIMARY KEY,
    sequence  INT UNIQUE NOT NULL,
    result    NUMERIC    NOT NULL,
    precision INT        NOT NULL
);