package com.geonwoo.assignment.domain.usecase

import com.geonwoo.assignment.domain.model.Todo
import com.geonwoo.assignment.domain.repository.TodoRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class GetTodoByIdUseCaseTest {

    private lateinit var repository: TodoRepository
    private lateinit var getTodoByIdUseCase: GetTodoByIdUseCase

    @Before
    fun setup() {
        repository = mockk()
        getTodoByIdUseCase = GetTodoByIdUseCase(repository)
    }

    @Test
    fun `invoke returns todo when found`() = runTest {
        // Given
        val expectedTodo = Todo(id = 1, title = "Test", description = "Description")
        coEvery { repository.getTodoById(1) } returns expectedTodo

        // When
        val result = getTodoByIdUseCase(1)

        // Then
        assertEquals(expectedTodo, result)
        coVerify { repository.getTodoById(1) }
    }

    @Test
    fun `invoke returns null when not found`() = runTest {
        // Given
        coEvery { repository.getTodoById(999) } returns null

        // When
        val result = getTodoByIdUseCase(999)

        // Then
        assertNull(result)
    }
}
