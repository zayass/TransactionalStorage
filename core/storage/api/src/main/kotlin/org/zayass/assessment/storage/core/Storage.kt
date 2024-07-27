package org.zayass.assessment.storage.core

interface Storage<K : Any, V : Any> {
    operator fun get(key: K): V?
    operator fun set(key: K, value: V)
    fun delete(key: K)
    fun count(value: V): Int

    fun begin()
    fun commit(): TransactionResult
    fun rollback(): TransactionResult
}
