package kz.dehaliboch.otp_test.concurrency

import java.util.concurrent.locks.ReentrantReadWriteLock

class ReentrantLockTest {

    var sharedResource = 0
        private set

    private val reentrantReadWriteLock = ReentrantReadWriteLock()

    fun changeState() {
        reentrantReadWriteLock.writeLock().lock()
        try {
            sharedResource++
        } finally {
            reentrantReadWriteLock.writeLock().unlock()
        }
    }

    fun readState(): Int {
        reentrantReadWriteLock.readLock().lock()
        try {
            return sharedResource
        } finally {
            reentrantReadWriteLock.readLock().unlock()
        }
    }
}