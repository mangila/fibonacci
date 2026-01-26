CREATE TABLE IF NOT EXISTS fibonacci_results
(
    id        SERIAL PRIMARY KEY,
    sequence  INT UNIQUE NOT NULL,
    -- If doing huge fibonacci sequences, this could overflow, but we're not doing that here.
    -- but should be a BYTEA then when fib sequences are over 500k ish'
    result    NUMERIC    NOT NULL,
    precision INT        NOT NULL
);