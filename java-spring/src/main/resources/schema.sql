DROP TABLE IF EXISTS fibonacci_results;
CREATE TABLE IF NOT EXISTS fibonacci_results
(
    id     BIGINT PRIMARY KEY,
    length INT,
    result BYTEA
);