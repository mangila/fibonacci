#!lua name=produce_sequence_lib

-- keys[1]: queue_key
-- keys[2]: bloomfilter_key
-- args[1]: sequence
-- args[2]: payload

-- Produces a new payload to the queue or discard if sequence already in bloom filter
local function produce_sequence(keys, args)
    local queue_key = keys[1]
    local bloomfilter_key = keys[2]
    local sequence = args[1]
    local payload = args[2]

    -- 1. Check redis bloom filter
    local exists = redis.call('BF.EXISTS', bloomfilter_key, sequence)

    -- 2. check if it exists in the bloom filter
    -- else then push to queue
    if exists == 1 then
        return "EXISTS: " .. sequence
    else
        redis.call('RPUSH', queue_key, payload)
        return "OK: " .. sequence
     end
end

-- Register the functions
redis.register_function('produce_sequence', produce_sequence)