local key = 'flash:request:' .. KEYS[1]  -- 每个 IP 地址对应的 Redis 键
local limit = tonumber(ARGV[1]) or 5  -- 每秒请求限制次数，默认为 5
local expire_time = tonumber(ARGV[2]) or 1  -- Redis 键的过期时间，默认为 1 秒

local current = redis.call('incr', key)
if current == 1 then
    redis.call('expire', key, expire_time)
end

if current > limit then
    return 0  -- 超过限制，返回 0 表示请求被拒绝
else
    return 1  -- 未超过限制，返回 1 表示请求有效
end
