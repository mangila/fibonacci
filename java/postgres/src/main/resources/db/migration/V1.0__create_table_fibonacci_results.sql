CREATE TABLE IF NOT EXISTS fibonacci_results
(
    id        SERIAL PRIMARY KEY,
    sequence  INT UNIQUE NOT NULL,
    -- If doing huge fibonacci sequences, NUMERIC could overflow, but we're not doing that here.
    -- for fibonacci sequences around 500k ish, we should use BYTEA
    result    NUMERIC    NOT NULL,
    precision INT        NOT NULL
);