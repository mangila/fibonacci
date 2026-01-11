DROP TABLE IF EXISTS fibonacci_results;
CREATE TABLE IF NOT EXISTS fibonacci_results
(
    id        SERIAL PRIMARY KEY,
    -- NUMERIC or BYTEA is the question for large numbers
    result    NUMERIC NOT NULL,
    precision INT     NOT NULL
);
^^
CREATE OR REPLACE FUNCTION notify_new_fibonacci_result_fn() RETURNS trigger AS
$$
DECLARE
    channel        TEXT := 'livestream';
    notify_payload TEXT;
BEGIN
    -- Only send the id and the length of the numeric value in the notification
    notify_payload := json_build_object(
            'id', NEW.id,
            'precision', length(NEW.precision::TEXT)
                      )::json;
    PERFORM pg_notify(channel, notify_payload);
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;
^^

DROP TRIGGER IF EXISTS fibonacci_result_notify_trgr ON fibonacci_results;

CREATE TRIGGER fibonacci_result_notify_trgr
    AFTER INSERT
    ON fibonacci_results
    FOR EACH ROW
EXECUTE FUNCTION notify_new_fibonacci_result_fn();
^^