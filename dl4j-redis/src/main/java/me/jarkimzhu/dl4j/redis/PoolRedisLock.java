/*
 * Copyright (c) 2014-2017. JarkimZhu
 * This software can not be used privately without permission
 */

package me.jarkimzhu.dl4j.redis;

import me.jarkimzhu.dl4j.common.StringLockValue;
import me.jarkimzhu.libs.cache.redis.pool.PoolRedisCache;
import me.jarkimzhu.libs.utils.MathUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * Created on 2017/7/10.
 *
 * @author JarkimZhu
 * @since JDK1.8
 */
public class PoolRedisLock implements Lock {

    private static final Logger logger = LoggerFactory.getLogger(PoolRedisCache.class);
    private static final String LOCK_KEY = "dl4j:DEFAULT_LOCK";

    private PoolRedisCache<String, String> cache;

    private String lockName;

    private volatile StringLockValue lockValue;

    public PoolRedisLock(PoolRedisCache<String, String> cache, long expiredTime) {
        this(LOCK_KEY, cache, expiredTime);
    }

    public PoolRedisLock(String lockName, PoolRedisCache<String, String> cache, long expiredTime) {
        this.cache = cache;
        this.lockName = lockName;
        cache.setTimeout(expiredTime);
    }

    @Override
    public void lock() {
        try {
            lockInterruptibly();
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
        }
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        StringLockValue value = new StringLockValue();
        while (lockValue == null) {
            String token = MathUtils.randomInt(6);
            long sysTime = System.currentTimeMillis();
            value.setValue(token, sysTime);
            if(cache.putIfNotExists(lockName, value.getValue())) {
                lockValue = value;
                break;
            } else {
                TimeUnit.MILLISECONDS.sleep(50);
            }
        }
    }

    @Override
    public boolean tryLock() {
        String token = MathUtils.randomInt(6);
        long sysTime = System.currentTimeMillis();
        StringLockValue value = new StringLockValue(token, sysTime);
        return cache.putIfNotExists(lockName, value.getValue());
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return false;
    }

    @Override
    public void unlock() {
        if(lockValue != null) {
            String value = cache.get(lockName);
            if(value != null) {
                StringLockValue v = new StringLockValue(value);
                if(v.getToken().equals(lockValue.getToken())) {
                    cache.remove(lockName);
                    lockValue = null;
                }
            }
        }
    }

    @Override
    public Condition newCondition() {
        return null;
    }
}
