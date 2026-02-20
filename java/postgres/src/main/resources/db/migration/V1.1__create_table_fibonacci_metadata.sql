CREATE TABLE IF NOT EXISTS fibonacci_metadata
(
    id         INT PRIMARY KEY,
    scheduled  BOOLEAN NOT NULL,
    computed   BOOLEAN NOT NULL,
    algorithm  VARCHAR(255) NOT NULL,
    updated_at TIMESTAMP DEFAULT NOW(),
    created_at TIMESTAMP DEFAULT NOW()
);