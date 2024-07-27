package org.zayass.assessment.storage.core

import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.withLock

@Suppress("ktlint:standard:function-naming")
fun <K : Any, V : Any> SynchronizedStorage(): Storage<K, V> = SynchronizedStorageImpl()

internal class SynchronizedStorageImpl<K : Any, V : Any> : Storage<K, V> {
    private val wrapped = Storage<K, V>()

    private val rwLock = ReentrantReadWriteLock()
    private val readLock = rwLock.readLock()
    private val writeLock = rwLock.writeLock()

    override fun get(key: K) = readLock.withLock {
        wrapped[key]
    }

    override fun count(value: V) = readLock.withLock {
        wrapped.count(value)
    }

    override fun set(key: K, value: V) = writeLock.withLock {
        wrapped[key] = value
    }

    override fun delete(key: K) = writeLock.withLock {
        wrapped.delete(key)
    }

    override fun begin() = writeLock.withLock(wrapped::begin)
    override fun commit() = writeLock.withLock(wrapped::commit)
    override fun rollback() = writeLock.withLock(wrapped::rollback)
}
