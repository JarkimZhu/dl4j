/*
 * Copyright (c) 2014-2017. JarkimZhu
 * This software can not be used privately without permission
 */

package me.jarkimzhu.dl4j.redis;

import me.jarkimzhu.libs.cache.redis.pool.PoolRedisCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * Created on 2017/7/10.
 *
 * @author JarkimZhu
 * @since JDK1.8
 */
public class PoolRedisLock extends AbstractRedisLock implements Lock {

    private static final Logger logger = LoggerFactory.getLogger(PoolRedisCache.class);

    public PoolRedisLock(PoolRedisCache<String, String> cache, long expiredTime) {
        this(LOCK_KEY, cache, expiredTime);
    }

    public PoolRedisLock(String lockName, PoolRedisCache<String, String> cache, long expiredTime) {
        super(lockName, cache, expiredTime);
    }

    @Override
    public Condition newCondition() {
        return null; // TODO
    }
}
