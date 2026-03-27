local tokens_key = KEYS[1]
local ts_key = KEYS[2]

local capacity = tonumber(ARGV[1])
local refill_rate = tonumber(ARGV[2])
local now = tonumber(ARGV[3])

local last_ts = tonumber(redis.call("GET", ts_key))
if last_ts == nil then
  last_ts = now
end

local current = tonumber(redis.call("GET", tokens_key))
if current == nil then
  current = capacity
end

local elapsed = now - last_ts
local refill = math.floor(elapsed / 60000 * refill_rate)

current = math.min(capacity, current + refill)

if current <= 0 then
  return 0
end

redis.call("SET", tokens_key, current - 1)
redis.call("SET", ts_key, now)

return 1