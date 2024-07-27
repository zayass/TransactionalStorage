package org.zayass.assessment.storage.core

typealias StringSuspendStorage = SuspendStorage<String, String>

interface SuspendStorage<K : Any, V : Any> {
    suspend fun get(key: K): V?
    suspend fun set(key: K, value: V)
    suspend fun delete(key: K)
    suspend fun count(value: V): Int

    suspend fun begin()
    suspend fun commit(): TransactionResult
    suspend fun rollback(): TransactionResult
}
