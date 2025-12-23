package com.geonwoo.assignment.domain.usecase

import com.geonwoo.assignment.domain.model.Todo
import com.geonwoo.assignment.domain.repository.TodoRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class UpdateTodoUseCaseTest {

    private lateinit var repository: TodoRepository
    private lateinit var updateTodoUseCase: UpdateTodoUseCase

    @Before
    fun setup() {
        repository = mockk()
        updateTodoUseCase = UpdateTodoUseCase(repository)
    }

    @Test
    fun `invoke updates todo in repository`() = runTest {
        // Given
        val todo = Todo(id = 1, title = "Updated", description = "Updated Desc", isCompleted = true)
        coEvery { repository.updateTodo(todo) } just runs

        // When
        updateTodoUseCase(todo)

        // Then
        coVerify { repository.updateTodo(todo) }
    }

    @Test
    fun `invoke toggles completion status`() = runTest {
        // Given
        val todo = Todo(id = 1, title = "Test", description = "Desc", isCompleted = false)
        val updatedTodo = todo.copy(isCompleted = true)
        coEvery { repository.updateTodo(updatedTodo) } just runs

        // When
        updateTodoUseCase(updatedTodo)

        // Then
        coVerify { repository.updateTodo(updatedTodo) }
    }
}
