/*
 * Copyright (c) 2014-2017. JarkimZhu
 * This software can not be used privately without permission
 */

package me.jarkimzhu.dl4j.redis;

import me.jarkimzhu.libs.cache.redis.cluster.ClusterRedisCache;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * Created on 2017/7/12.
 *
 * @author JarkimZhu
 * @since JDK1.8
 */
public class ClusterRedisLock extends AbstractRedisLock implements Lock {

    public ClusterRedisLock(ClusterRedisCache<String, String> cache, long expiredTime) {
        this(LOCK_KEY, cache, expiredTime);
    }

    public ClusterRedisLock(String lockName, ClusterRedisCache<String, String> cache, long expiredTime) {
        super(lockName, cache, expiredTime);
    }

    @Override
    public Condition newCondition() {
        return null;
    }
}
