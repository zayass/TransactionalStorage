package org.zayass.assessment.storage.core

import org.zayass.assessment.storage.core.StorageImpl.Optional.None
import org.zayass.assessment.storage.core.StorageImpl.Optional.Some

@Suppress("ktlint:standard:function-naming")
fun <K : Any, V : Any> Storage(): Storage<K, V> = StorageImpl()

/**
 * # Assumptions
 *
 * There is:
 * - n - total number of keys in root storage
 * - t - total number of keys change within average transaction
 * - d - average level of transaction depth
 *
 * Here we assume that n >>> t >> d
 *
 *
 * # Approach
 *
 * Root storage implemented as two hash maps and one stack:
 * - key -> value hashmap for key based operations ([get], [set], [delete])
 * - value -> count hashmap for [count]
 * - transactions stack - for transactions support
 *
 * Each transaction in stack handle their own hashmaps with overrides:
 * - key -> value hashmap handle overrides and use None to mark key as deleted
 * - value -> count hashmap handle diffs to parent counter
 *
 * Usage of transaction stack:
 * - [get] iterates over stack until reach first key appearance
 * - [set], [delete] operates only on top of stack
 * - [count] - iterates over whole stack in any case
 *
 * # Performance
 *
 * - [set], [delete], [begin], [rollback] - runs in O(1)
 * - [get], [count] - runs in O(d) where d is depth of transactions
 * - [commit] - runs in O(t) where t is keys changed within transaction
 * - So overall complexity of running n crud operations in one transaction will be O(n).
 *   Therefor amortized time for one operation within one transaction is O(1)
 *
 *
 * # Alternative approaches
 *
 * - Snapshots based functional collections like [HAMT](https://en.wikipedia.org/wiki/Hash_array_mapped_trie)
 *      - See: [Gist](https://gist.github.com/zayass/103bd81fab253fb5de63114d0c911ea2)
 *      - Discarded because require external library
 *
 * - Copy values from parent to child transaction:
 *      - [begin] - will be O(t)
 *      - [commit] - will be O(1)
 *      - [count] - will be O(1)
 *      - Memory usage will be more
 *      - Discarded because begin/rollback will be slower, memory usage increased,
 *      and possible depth of transactions assumed as low
 *
 * - MVCC - Discarded because implementation time not fit in constraints
 *
 *
 * # Thread safety
 * This particular class not thread-safe at all but two thread-safe wrappers are provided:
 * - [SynchronizedStorage] - For thread environment based [java.util.concurrent.locks.ReentrantReadWriteLock]
 * - [SuspendStorageImpl] - For coroutine environment based on spin-lock [kotlinx.coroutines.sync.Mutex]
 *
 * Both wrappers not allow to run multiple transactions simultaneously.
 * In real life applications there should be one transactions-stack per thread coroutine.
 * But it seems to hard for given time constraints and also don't fit well with required interface.
 * Good example of such approach is Room which allocates one thread from thread pool for every top-level transaction.
 * And each child transaction share same thread.
 *
 * See [RoomDatabase.startTransactionCoroutine](https://github.com/androidx/androidx/blob/ea96776b1409b84a0b3ed568435263347e1589e0/room/room-runtime/src/androidMain/kotlin/androidx/room/RoomDatabase.android.kt#L1982)
 *
 *
 * Another possible approach to isolate simultaneous transactions is
 * [MVCC](https://en.wikipedia.org/wiki/Multiversion_concurrency_control) and
 * [Append-only log](https://en.wikipedia.org/wiki/Append-only)
 *
 * See [Sophia](http://sophia.systems/)
 */
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
