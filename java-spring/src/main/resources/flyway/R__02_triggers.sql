DROP TRIGGER IF EXISTS fibonacci_result_notify_trgr ON fibonacci_results;

CREATE TRIGGER fibonacci_result_notify_trgr
    AFTER INSERT
    ON fibonacci_results
    FOR EACH ROW
EXECUTE FUNCTION notify_new_fibonacci_result_fn();