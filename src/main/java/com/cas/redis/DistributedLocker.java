package com.cas.redis;

import org.redisson.api.RLock;

import java.util.concurrent.TimeUnit;

public interface DistributedLocker {

    RLock lock(String lockKey);

    RLock lock(String lockKey, int timeout);

    RLock lock(String lockKey, TimeUnit unit, int timeout);

    boolean tryLock(String lockKey, TimeUnit unit, int waitTime, int leaseTime);

    boolean tryLock(String lockKey, TimeUnit unit, int waitTime);

    void unlock(String lockKey);

    void unlock(RLock lock);
}
