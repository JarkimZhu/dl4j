/*
 * Copyright (c) 2014-2017. JarkimZhu
 * This software can not be used privately without permission
 */

package me.jarkimzhu.dl4j.redis;

import me.jarkimzhu.dl4j.common.StringLockValue;
import me.jarkimzhu.libs.cache.ICache;
import me.jarkimzhu.libs.utils.MathUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

/**
 * Created on 2017/7/12.
 *
 * @author JarkimZhu
 * @since JDK1.8
 */
public abstract class AbstractRedisLock implements Lock {

    private static final Logger logger = LoggerFactory.getLogger(AbstractRedisLock.class);

    static final String LOCK_KEY = "dl4j:DEFAULT_LOCK";

    private ICache<String, String> cache;
    private String lockName;

    private ThreadLocal<StringLockValue> lockValue = new ThreadLocal<>();

    AbstractRedisLock(String lockName, ICache<String, String> cache, long expiredTime) {
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
        waitLock(Long.MAX_VALUE);
    }

    @Override
    public boolean tryLock() {
        String token = MathUtils.randomInt(6);
        long sysTime = System.nanoTime();
        StringLockValue value = new StringLockValue(token, sysTime);
        return cache.putIfNotExists(lockName, value.getValue());
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return waitLock(unit.toNanos(time));
    }

    private boolean waitLock(long waitTime) throws InterruptedException {
        StringLockValue value = new StringLockValue();
        long startTime = System.nanoTime();
        while (lockValue.get() == null && (System.nanoTime() - startTime) < waitTime) {
            String token = MathUtils.randomInt(6);
            long sysTime = System.nanoTime();
            value.setValue(token, sysTime);
            if(cache.putIfNotExists(lockName, value.getValue())) {
                lockValue.set(value);
                break;
            } else {
                TimeUnit.MILLISECONDS.sleep(50);
            }
        }
        return lockValue.get() != null;
    }

    @Override
    public void unlock() {
        StringLockValue lockValue = this.lockValue.get();
        if(lockValue != null) {
            String value = cache.get(lockName);
            if(value != null) {
                StringLockValue v = new StringLockValue(value);
                if(v.getToken().equals(lockValue.getToken())) {
                    cache.remove(lockName);
                    this.lockValue.remove();
                }
            }
        }
    }
}
