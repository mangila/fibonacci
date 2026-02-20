CREATE OR REPLACE FUNCTION notify_new_fibonacci_result_fn() RETURNS trigger AS
$$
DECLARE
    channel        TEXT := 'fibonacci';
    notify_payload TEXT;
BEGIN
    -- Only send the id, sequence and the length(precision) of the numeric value in the notification
    notify_payload := json_build_object(
            'id', NEW.id,
            'sequence', NEW.sequence,
            'precision', NEW.precision
                      )::json;
    PERFORM pg_notify(channel, notify_payload);
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;