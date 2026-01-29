CREATE TABLE IF NOT EXISTS fibonacci_metadata
(
    id             INT PRIMARY KEY,
    sent_to_zset   BOOLEAN NOT NULL,
    sent_to_stream BOOLEAN NOT NULL,
    updated_at     TIMESTAMP DEFAULT NOW(),
    created_at     TIMESTAMP DEFAULT NOW()
);