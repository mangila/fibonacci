#!lua name=add_zset_lib

-- keys[1]: zset_key
-- keys[2]: bloom_filter_key
-- args[1]: score - the fibonacci sequence, that also will be added to the bloom filter
-- args[2]: member - json string contains fibonacci projection

-- Adds to zset and updates the bloom filter
local function add_zset(keys, args)
    local zset_key = keys[1]
    local bloom_filter_key = keys[2]
    local score = args[1]
    local member = args[2]

    -- 1. Add to zset with the NX flag (only accept unique members)
    redis.call('ZADD', zset_key, 'NX', score, member)
    -- 2. Update Bloom filter
    redis.call('BF.ADD', bloom_filter_key, score)

    return "OK"
end

-- Register the functions
redis.register_function('add_zset', add_zset)