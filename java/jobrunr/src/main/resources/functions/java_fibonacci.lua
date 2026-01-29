#!lua name=java_fibonacci_lib

-- keys[1]: zset_key
-- keys[2]: bloom_filter_key
-- args[1]: sequence

-- Adds to zset and updates the bloom filter
local function add_zset(keys, args)
    return "OK"
end

-- keys[1]: zset_key
-- keys[2]: stream_key
-- keys[3]: sequence_key

-- Checks the current "pointer" to drain from and add to Redis stream
local function drain_zset(keys, args)
    local zset_key = keys[1]
    local stream_key = keys[2]
    local sequence_key = keys[3]

    local current_sequence = redis.call('GET', sequence_key)

    if not current_sequence then
         return { err = "ERR" }
    end

    -- 1. Get the current head
    local entries = redis.call('ZRANGE', zset_key, 0, 0, 'WITHSCORES')

    -- 2. If ZSET is empty, stop
    if #entries == 0 then
        return { err = "ZSET_EMPTY" }
    end

    local current_member = entries[1]
    local current_score = entries[2]

    -- 3. The "Sequence Check"
    if tostring(current_score) ~= current_sequence then
        return { err = "SEQUENCE_MISMATCH", actual = current_member }
    end

    -- 4. Move to Stream
    redis.call('XADD', stream_key, '*', 'member', current_member)

    -- 5. Remove from ZSET
    redis.call('ZREM', zset_key, tostring(current_member))

    -- 6. Increment for new function call
    redis.call('INCR', sequence_key)

    return "OK"
end

-- Register the functions
redis.register_function('add_zset', add_zset)
redis.register_function('drain_zset', drain_zset)
