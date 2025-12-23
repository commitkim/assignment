package com.geonwoo.assignment.domain.usecase

import com.geonwoo.assignment.domain.model.Todo
import com.geonwoo.assignment.domain.repository.TodoRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class AddTodoUseCaseTest {

    private lateinit var repository: TodoRepository
    private lateinit var addTodoUseCase: AddTodoUseCase

    @Before
    fun setup() {
        repository = mockk()
        addTodoUseCase = AddTodoUseCase(repository)
    }

    @Test
    fun `invoke adds todo and returns id`() = runTest {
        // Given
        val todo = Todo(title = "New Todo", description = "New Description")
        val expectedId = 1L
        coEvery { repository.addTodo(todo) } returns expectedId

        // When
        val result = addTodoUseCase(todo)

        // Then
        assertEquals(expectedId, result)
        coVerify { repository.addTodo(todo) }
    }

    @Test
    fun `invoke with empty title still calls repository`() = runTest {
        // Given
        val todo = Todo(title = "", description = "Description")
        coEvery { repository.addTodo(todo) } returns 2L

        // When
        val result = addTodoUseCase(todo)

        // Then
        assertEquals(2L, result)
    }
}
