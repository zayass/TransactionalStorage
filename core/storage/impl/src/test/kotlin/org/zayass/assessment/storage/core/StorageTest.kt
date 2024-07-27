package org.zayass.assessment.storage.core

import org.junit.Assert.assertEquals
import org.junit.Test

class StorageTest {

    //region Example test cases
    @Test
    fun `Example test - Set and get a value`() {
        val storage = Storage<String, Int>()

        storage["foo"] = 123
        assertEquals(123, storage["foo"])
    }

    @Test
    fun `Example test - Delete a value`() {
        val storage = Storage<String, Int>()

        storage["foo"] = 123
        assertEquals(123, storage["foo"])

        storage.delete("foo")
        assertEquals(null, storage["foo"])
    }

    @Test
    fun `Example test - Count the number of occurrences of a value`() {
        val storage = Storage<String, Int>()
        storage["foo"] = 123
        storage["bar"] = 456
        storage["baz"] = 123

        assertEquals(2, storage.count(123))
        assertEquals(1, storage.count(456))
    }

    @Test
    fun `Example test - Commit a transaction`() {
        val storage = Storage<String, Int>()

        storage["bar"] = 123
        assertEquals(123, storage["bar"])

        storage.begin()
        storage["foo"] = 456
        assertEquals(123, storage["bar"])

        storage.delete("bar")
        storage.commit()
        assertEquals(null, storage["bar"])

        assertEquals(TransactionResult.NOT_IN_TRANSACTION, storage.rollback())
        assertEquals(456, storage["foo"])
    }

    @Test
    fun `Example test - Rollback a transaction`() {
        val storage = Storage<String, String>()

        storage["foo"] = "123"
        storage["bar"] = "abc"

        storage.begin()
        storage["foo"] = "456"
        assertEquals("456", storage["foo"])

        storage["bar"] = "def"
        assertEquals("def", storage["bar"])

        storage.rollback()

        assertEquals("123", storage["foo"])
        assertEquals("abc", storage["bar"])
        assertEquals(TransactionResult.NOT_IN_TRANSACTION, storage.commit())
    }

    @Test
    fun `Example test - Nested transactions`() {
        val storage = Storage<String, Int>()
        storage["foo"] = 123
        storage["bar"] = 456

        storage.begin()
        storage["foo"] = 456

        storage.begin()

        assertEquals(2, storage.count(456))
        assertEquals(456, storage["foo"])

        storage["foo"] = 789
        assertEquals(789, storage["foo"])

        storage.rollback()
        assertEquals(456, storage["foo"])

        storage.delete("foo")
        assertEquals(null, storage["foo"])

        storage.rollback()
        assertEquals(123, storage["foo"])
    }
    //endregion

    //region Extra test cases
    @Test
    fun `Test rollback nested`() {
        val storage = Storage<String, Int>()

        storage["x"] = 1

        // begin outer transaction
        storage.begin()
        storage["x"] = 2

        // begin inner transaction
        storage.begin()
        storage.delete("x")

        assertEquals(null, storage["x"])

        // discard inner transaction
        storage.rollback()
        assertEquals(2, storage["x"])

        // discard outer transaction
        storage.rollback()
        assertEquals(1, storage["x"])
    }

    @Test
    fun `Test commit nested`() {
        val storage = Storage<String, Int>()

        storage["x"] = 1
        assertEquals(1, storage["x"])
        assertEquals(null, storage["y"])

        // begin outer transaction
        storage.begin()
        assertEquals(1, storage["x"])
        assertEquals(null, storage["y"])

        storage["x"] = 2
        storage["y"] = 1

        assertEquals(2, storage["x"])
        assertEquals(1, storage["y"])

        // begin inner transaction
        storage.begin()
        storage.delete("x")
        storage["y"] = 2

        assertEquals(null, storage["x"])
        assertEquals(2, storage["y"])

        // commit inner transaction
        storage.commit()
        assertEquals(null, storage["x"])
        assertEquals(2, storage["y"])

        // commit outer transaction
        storage.commit()
        assertEquals(null, storage["x"])
        assertEquals(2, storage["y"])
    }

    @Test
    fun `Test commit-rollback nested`() {
        val storage = Storage<String, Int>()

        storage["x"] = 1
        assertEquals(1, storage["x"])
        assertEquals(null, storage["y"])

        // begin outer transaction
        storage.begin()
        assertEquals(1, storage["x"])
        assertEquals(null, storage["y"])

        storage["x"] = 2
        storage["y"] = 1

        assertEquals(2, storage["x"])
        assertEquals(1, storage["y"])

        // begin inner transaction
        storage.begin()
        storage.delete("x")
        storage["y"] = 2

        assertEquals(null, storage["x"])
        assertEquals(2, storage["y"])

        // commit inner transaction
        storage.commit()
        assertEquals(null, storage["x"])
        assertEquals(2, storage["y"])

        // discard outer transaction
        storage.rollback()
        assertEquals(1, storage["x"])
        assertEquals(null, storage["y"])
    }

    @Test
    fun `Test count in nested transactions`() {
        val storage = Storage<String, Int>()

        assertEquals(0, storage.count(42))

        storage["x"] = 42
        storage["y"] = 43
        assertEquals(1, storage.count(42))

        // begin outer transaction
        storage.begin()
        storage["y"] = 42
        storage["z"] = 43
        assertEquals(2, storage.count(42))

        // begin inner transaction
        storage.begin()
        storage["z"] = 42
        assertEquals(3, storage.count(42))

        // commit inner transaction
        storage.commit()
        assertEquals(3, storage.count(42))

        // discard outer transaction
        storage.rollback()
        assertEquals(1, storage.count(42))

        storage.begin()
        storage["w"] = 42
        storage.commit()
        assertEquals(2, storage.count(42))
    }
    //endregion
}
