package com.geonwoo.assignment.data.memory

import com.geonwoo.assignment.domain.model.Todo
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class MemoryDataSourceTest {

    private lateinit var memoryDataSource: MemoryDataSource

    @Before
    fun setup() {
        memoryDataSource = MemoryDataSource()
    }

    @Test
    fun `getTodos returns empty list initially`() {
        // When
        val result = memoryDataSource.getTodos()

        // Then
        assertTrue(result.isEmpty())
    }

    @Test
    fun `saveTodo stores todo in cache`() {
        // Given
        val todo = Todo(id = 1, title = "Test", description = "Desc")

        // When
        memoryDataSource.saveTodo(todo)
        val result = memoryDataSource.getTodoById(1)

        // Then
        assertEquals(todo, result)
    }

    @Test
    fun `saveTodos stores multiple todos`() {
        // Given
        val todos = listOf(
            Todo(id = 1, title = "Test 1", description = "Desc 1"),
            Todo(id = 2, title = "Test 2", description = "Desc 2")
        )

        // When
        memoryDataSource.saveTodos(todos)
        val result = memoryDataSource.getTodos()

        // Then
        assertEquals(2, result.size)
    }

    @Test
    fun `getTodoById returns null when not found`() {
        // When
        val result = memoryDataSource.getTodoById(999)

        // Then
        assertNull(result)
    }

    @Test
    fun `deleteTodo removes todo from cache`() {
        // Given
        val todo = Todo(id = 1, title = "Test", description = "Desc")
        memoryDataSource.saveTodo(todo)

        // When
        memoryDataSource.deleteTodo(1)

        // Then
        assertNull(memoryDataSource.getTodoById(1))
    }

    @Test
    fun `clearCache removes all todos`() {
        // Given
        val todos = listOf(
            Todo(id = 1, title = "Test 1", description = "Desc 1"),
            Todo(id = 2, title = "Test 2", description = "Desc 2")
        )
        memoryDataSource.saveTodos(todos)

        // When
        memoryDataSource.clearCache()

        // Then
        assertTrue(memoryDataSource.getTodos().isEmpty())
    }

    @Test
    fun `hasCache returns true when cache is not empty`() {
        // Given
        val todo = Todo(id = 1, title = "Test", description = "Desc")
        memoryDataSource.saveTodo(todo)

        // When & Then
        assertTrue(memoryDataSource.hasCache())
    }

    @Test
    fun `hasCache returns false when cache is empty`() {
        // When & Then
        assertFalse(memoryDataSource.hasCache())
    }

    @Test
    fun `getTodos returns sorted by createdAt descending`() {
        // Given
        val todo1 = Todo(id = 1, title = "Old", description = "Desc", createdAt = 1000)
        val todo2 = Todo(id = 2, title = "New", description = "Desc", createdAt = 2000)
        memoryDataSource.saveTodo(todo1)
        memoryDataSource.saveTodo(todo2)

        // When
        val result = memoryDataSource.getTodos()

        // Then
        assertEquals("New", result[0].title)
        assertEquals("Old", result[1].title)
    }
}
