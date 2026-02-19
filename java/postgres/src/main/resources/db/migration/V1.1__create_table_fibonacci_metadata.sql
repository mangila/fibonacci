CREATE TABLE IF NOT EXISTS fibonacci_metadata
(
    id         INT PRIMARY KEY,
    computed   BOOLEAN NOT NULL,
    updated_at TIMESTAMP DEFAULT NOW(),
    created_at TIMESTAMP DEFAULT NOW()
);