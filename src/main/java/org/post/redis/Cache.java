package org.post.redis;

import org.post.config.Settings;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class Cache {
    JedisPool pool;
    int expire;

    public Cache(Settings settings) {
        this.pool = new JedisPool(settings.getRedisHost(), settings.getRedisPort());
        this.expire = settings.getRedisExpireMinutes();
    }

    public void SetKey(String email,String code) {
        try (Jedis jedis = this.pool.getResource()) {

            jedis.setex(String.format("email:%s", email), this.expire * 60L, code);
        }
    }

    public String GetKey(String email){
        try (Jedis jedis = pool.getResource()) {

            return jedis.get(String.format("email:%s", email));
        }
    }
}
