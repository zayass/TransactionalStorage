package org.zayass.assessment.storage.core

import org.zayass.assessment.storage.core.StorageImpl.Optional.None
import org.zayass.assessment.storage.core.StorageImpl.Optional.Some

@Suppress("ktlint:standard:function-naming")
fun <K : Any, V : Any> Storage(): Storage<K, V> = StorageImpl()

internal class StorageImpl<K : Any, V : Any> : Storage<K, V> {
    private val rootStore = HashMap<K, V>()
    private val rootCounter = HashMap<V, Int>()
    private val transactions = ArrayList<Transaction<K, V>>()

    /**
     * Time complexity: O(d) where d is depth of nested transactions
     */
    override fun get(key: K): V? {
        for (transaction in transactions.asReversed()) {
            val value = transaction.store[key]
            if (value != null) {
                return value.toNullable()
            }
        }

        return rootStore[key]
    }

    /**
     * Time complexity: O(1)
     */
    override fun set(key: K, value: V) {
        decrementCounterForKey(key)

        val transaction = transactions.lastOrNull()

        if (transaction != null) {
            transaction[key] = value
        } else {
            rootStore[key] = value
        }

        incrementCounterForKey(key)
    }

    /**
     * Time complexity: O(1)
     */
    override fun delete(key: K) {
        decrementCounterForKey(key)

        val transaction = transactions.lastOrNull()

        if (transaction != null) {
            transaction.remove(key)
        } else {
            rootStore.remove(key)
        }
    }

    /**
     * Time complexity: O(d) where d is depth of nested transactions
     */
    override fun count(value: V): Int {
        val transactionsDiff = transactions.fold(0) { acc, transaction ->
            val count = transaction.counter[value] ?: 0
            acc + count
        }

        val count = rootCounter[value] ?: 0
        return count + transactionsDiff
    }

    /**
     * Time complexity: O(1)
     */
    override fun begin() {
        transactions += Transaction()
    }

    /**
     * Time complexity: O(k) where k is count of keys changed in last transaction
     */
    override fun commit(): TransactionResult {
        val transaction = transactions.removeLastOrNull()
            ?: return TransactionResult.NOT_IN_TRANSACTION

        val parent = transactions.lastOrNull()

        if (parent == null) {
            transaction.mergeToRoot()
        } else {
            transaction.mergeToParent(parent)
        }

        return TransactionResult.SUCCESS
    }

    /**
     * Time complexity: O(1)
     */
    override fun rollback(): TransactionResult {
        return if (transactions.isNotEmpty()) {
            transactions.removeLast()
            TransactionResult.SUCCESS
        } else {
            TransactionResult.NOT_IN_TRANSACTION
        }
    }

    private fun Transaction<K, V>.mergeToRoot() {
        for ((key, value) in store) {
            when (value) {
                is Some -> {
                    rootStore[key] = value.value
                }
                None -> {
                    rootStore.remove(key)
                }
            }
        }

        for ((value, diff) in counter) {
            rootCounter.update(value, diff)
        }
    }

    private fun Transaction<K, V>.mergeToParent(parent: Transaction<K, V>) {
        for ((key, value) in store) {
            parent.store[key] = value
        }

        for ((value, diff) in counter) {
            parent.counter.update(value, diff)
        }
    }

    private fun incrementCounterForKey(key: K) = updateCounter(key, +1)
    private fun decrementCounterForKey(key: K) = updateCounter(key, -1)

    private fun updateCounter(key: K, diff: Int) {
        val value = this[key] ?: return

        val transaction = transactions.lastOrNull()
        val counter = transaction?.counter ?: rootCounter

        counter.update(value, diff)
    }

    private fun HashMap<V, Int>.update(key: V, diff: Int) {
        val old = this[key] ?: 0
        val new = old + diff

        if (new != 0) {
            this[key] = new
        } else {
            remove(key)
        }
    }

    // A little bit overkill but more flexible and easier to implement removal in nested transactions
    private sealed interface Optional<out T : Any> {
        fun toNullable(): T?

        data class Some<out T : Any>(val value: T) : Optional<T> {
            override fun toNullable() = value
        }

        data object None : Optional<Nothing> {
            override fun toNullable() = null
        }
    }

    private class Transaction<K : Any, V : Any> {
        // None used to mark as removed
        val store = HashMap<K, Optional<V>>()
        val counter = HashMap<V, Int>()

        operator fun set(key: K, value: V) {
            store[key] = Some(value)
        }

        fun remove(key: K) {
            store[key] = None
        }
    }
}
