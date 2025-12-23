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

class DeleteTodoUseCaseTest {

    private lateinit var repository: TodoRepository
    private lateinit var deleteTodoUseCase: DeleteTodoUseCase

    @Before
    fun setup() {
        repository = mockk()
        deleteTodoUseCase = DeleteTodoUseCase(repository)
    }

    @Test
    fun `invoke deletes todo from repository`() = runTest {
        // Given
        val todo = Todo(id = 1, title = "To Delete", description = "Description")
        coEvery { repository.deleteTodo(todo) } just runs

        // When
        deleteTodoUseCase(todo)

        // Then
        coVerify { repository.deleteTodo(todo) }
    }
}
