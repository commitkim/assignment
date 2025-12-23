package com.geonwoo.assignment.domain.usecase

import com.geonwoo.assignment.domain.model.Todo
import com.geonwoo.assignment.domain.repository.TodoRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetTodosUseCaseTest {

    private lateinit var repository: TodoRepository
    private lateinit var getTodosUseCase: GetTodosUseCase

    @Before
    fun setup() {
        repository = mockk()
        getTodosUseCase = GetTodosUseCase(repository)
    }

    @Test
    fun `invoke returns flow of todos from repository`() = runTest {
        // Given
        val expectedTodos = listOf(
            Todo(id = 1, title = "Test 1", description = "Description 1"),
            Todo(id = 2, title = "Test 2", description = "Description 2")
        )
        every { repository.getTodos() } returns flowOf(expectedTodos)

        // When
        val result = getTodosUseCase().first()

        // Then
        assertEquals(expectedTodos, result)
        verify { repository.getTodos() }
    }

    @Test
    fun `invoke returns empty list when no todos exist`() = runTest {
        // Given
        every { repository.getTodos() } returns flowOf(emptyList())

        // When
        val result = getTodosUseCase().first()

        // Then
        assertEquals(emptyList<Todo>(), result)
    }
}
