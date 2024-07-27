package org.zayass.assessment.storage.core

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

@Suppress("ktlint:standard:function-naming")
fun <K : Any, V : Any> SuspendStorage(): SuspendStorage<K, V> = SuspendStorageImpl()

internal class SuspendStorageImpl<K : Any, V : Any> : SuspendStorage<K, V> {
    private val wrapped = Storage<K, V>()
    private val mutex = Mutex()

    override suspend fun get(key: K) = mutex.withLock {
        wrapped[key]
    }

    override suspend fun set(key: K, value: V) = mutex.withLock {
        wrapped[key] = value
    }

    override suspend fun delete(key: K) = mutex.withLock {
        wrapped.delete(key)
    }

    override suspend fun count(value: V) = mutex.withLock {
        wrapped.count(value)
    }

    override suspend fun begin() = mutex.withLock {
        wrapped.begin()
    }

    override suspend fun commit() = mutex.withLock {
        wrapped.commit()
    }

    override suspend fun rollback() = mutex.withLock {
        wrapped.rollback()
    }
}
