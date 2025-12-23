package com.geonwoo.assignment.data.repository

import app.cash.turbine.test
import com.geonwoo.assignment.data.local.LocalDataSource
import com.geonwoo.assignment.data.local.entity.TodoEntity
import com.geonwoo.assignment.data.memory.MemoryDataSource
import com.geonwoo.assignment.data.remote.RemoteDataSource
import com.geonwoo.assignment.domain.model.Todo
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class TodoRepositoryImplTest {

    private lateinit var localDataSource: LocalDataSource
    private lateinit var remoteDataSource: RemoteDataSource
    private lateinit var memoryDataSource: MemoryDataSource
    private lateinit var repository: TodoRepositoryImpl

    @Before
    fun setup() {
        localDataSource = mockk()
        remoteDataSource = mockk()
        memoryDataSource = mockk(relaxed = true)
        repository = TodoRepositoryImpl(localDataSource, remoteDataSource, memoryDataSource)
    }

    @Test
    fun `getTodos returns flow of todos from local data source`() = runTest {
        // Given
        val entities = listOf(
            TodoEntity(id = 1, title = "Test 1", description = "Desc 1"),
            TodoEntity(id = 2, title = "Test 2", description = "Desc 2")
        )
        every { localDataSource.getTodos() } returns flowOf(entities)

        // When & Then
        repository.getTodos().test {
            val result = awaitItem()
            assertEquals(2, result.size)
            assertEquals("Test 1", result[0].title)
            assertEquals("Test 2", result[1].title)
            awaitComplete()
        }
        
        verify { memoryDataSource.saveTodos(any()) }
    }

    @Test
    fun `getTodoById returns from memory cache if available`() = runTest {
        // Given
        val cachedTodo = Todo(id = 1, title = "Cached", description = "From Memory")
        every { memoryDataSource.getTodoById(1) } returns cachedTodo

        // When
        val result = repository.getTodoById(1)

        // Then
        assertEquals(cachedTodo, result)
        verify { memoryDataSource.getTodoById(1) }
    }

    @Test
    fun `getTodoById returns from local if not in cache`() = runTest {
        // Given
        val entity = TodoEntity(id = 1, title = "Local", description = "From DB")
        every { memoryDataSource.getTodoById(1) } returns null
        coEvery { localDataSource.getTodoById(1) } returns entity

        // When
        val result = repository.getTodoById(1)

        // Then
        assertEquals("Local", result?.title)
        verify { memoryDataSource.saveTodo(any()) }
    }

    @Test
    fun `getTodoById returns null when not found`() = runTest {
        // Given
        every { memoryDataSource.getTodoById(999) } returns null
        coEvery { localDataSource.getTodoById(999) } returns null

        // When
        val result = repository.getTodoById(999)

        // Then
        assertNull(result)
    }

    @Test
    fun `addTodo saves to local and memory`() = runTest {
        // Given
        val todo = Todo(title = "New", description = "Todo")
        coEvery { localDataSource.insertTodo(any()) } returns 1L

        // When
        val result = repository.addTodo(todo)

        // Then
        assertEquals(1L, result)
        coVerify { localDataSource.insertTodo(any()) }
        verify { memoryDataSource.saveTodo(any()) }
    }

    @Test
    fun `updateTodo updates local and memory`() = runTest {
        // Given
        val todo = Todo(id = 1, title = "Updated", description = "Desc")
        coEvery { localDataSource.updateTodo(any()) } just runs

        // When
        repository.updateTodo(todo)

        // Then
        coVerify { localDataSource.updateTodo(any()) }
        verify { memoryDataSource.saveTodo(todo) }
    }

    @Test
    fun `deleteTodo removes from local and memory`() = runTest {
        // Given
        val todo = Todo(id = 1, title = "To Delete", description = "Desc")
        coEvery { localDataSource.deleteTodo(any()) } just runs

        // When
        repository.deleteTodo(todo)

        // Then
        coVerify { localDataSource.deleteTodo(any()) }
        verify { memoryDataSource.deleteTodo(1) }
    }
}
