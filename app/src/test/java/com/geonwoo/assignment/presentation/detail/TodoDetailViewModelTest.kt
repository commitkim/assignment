package com.geonwoo.assignment.presentation.detail

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import com.geonwoo.assignment.domain.model.Todo
import com.geonwoo.assignment.domain.usecase.DeleteTodoUseCase
import com.geonwoo.assignment.domain.usecase.GetTodoByIdUseCase
import com.geonwoo.assignment.domain.usecase.UpdateTodoUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TodoDetailViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var savedStateHandle: SavedStateHandle
    private lateinit var getTodoByIdUseCase: GetTodoByIdUseCase
    private lateinit var updateTodoUseCase: UpdateTodoUseCase
    private lateinit var deleteTodoUseCase: DeleteTodoUseCase

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        getTodoByIdUseCase = mockk()
        updateTodoUseCase = mockk()
        deleteTodoUseCase = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(todoId: Long = 1L): TodoDetailViewModel {
        savedStateHandle = SavedStateHandle(mapOf("todoId" to todoId))
        return TodoDetailViewModel(
            savedStateHandle,
            getTodoByIdUseCase,
            updateTodoUseCase,
            deleteTodoUseCase
        )
    }

    @Test
    fun `init loads todo from use case`() = runTest {
        // Given
        val todo = Todo(id = 1, title = "Test", description = "Desc")
        coEvery { getTodoByIdUseCase(1) } returns todo

        // When
        val viewModel = createViewModel(1)
        advanceUntilIdle()

        // Then
        assertEquals(todo, viewModel.todo.value)
    }

    @Test
    fun `init sets todo to null when not found`() = runTest {
        // Given
        coEvery { getTodoByIdUseCase(999) } returns null

        // When
        val viewModel = createViewModel(999)
        advanceUntilIdle()

        // Then
        assertNull(viewModel.todo.value)
    }

    @Test
    fun `updateTodo with valid title updates and navigates back`() = runTest {
        // Given
        val todo = Todo(id = 1, title = "Original", description = "Desc")
        coEvery { getTodoByIdUseCase(1) } returns todo
        coEvery { updateTodoUseCase(any()) } just runs

        val viewModel = createViewModel(1)
        advanceUntilIdle()

        // When
        viewModel.updateTodo("Updated", "New Desc")
        advanceUntilIdle()

        // Then
        coVerify { updateTodoUseCase(match { it.title == "Updated" }) }
        assertTrue(viewModel.navigateBack.value == true)
    }

    @Test
    fun `updateTodo with blank title sets error`() = runTest {
        // Given
        val todo = Todo(id = 1, title = "Original", description = "Desc")
        coEvery { getTodoByIdUseCase(1) } returns todo

        val viewModel = createViewModel(1)
        advanceUntilIdle()

        // When
        viewModel.updateTodo("", "Desc")
        advanceUntilIdle()

        // Then
        assertEquals("제목을 입력해주세요", viewModel.error.value)
    }

    @Test
    fun `toggleComplete updates todo completion status`() = runTest {
        // Given
        val todo = Todo(id = 1, title = "Test", description = "Desc", isCompleted = false)
        coEvery { getTodoByIdUseCase(1) } returns todo
        coEvery { updateTodoUseCase(any()) } just runs

        val viewModel = createViewModel(1)
        advanceUntilIdle()

        // When
        viewModel.toggleComplete()
        advanceUntilIdle()

        // Then
        coVerify { updateTodoUseCase(match { it.isCompleted == true }) }
    }

    @Test
    fun `deleteTodo removes and navigates back`() = runTest {
        // Given
        val todo = Todo(id = 1, title = "Test", description = "Desc")
        coEvery { getTodoByIdUseCase(1) } returns todo
        coEvery { deleteTodoUseCase(todo) } just runs

        val viewModel = createViewModel(1)
        advanceUntilIdle()

        // When
        viewModel.deleteTodo()
        advanceUntilIdle()

        // Then
        coVerify { deleteTodoUseCase(todo) }
        assertTrue(viewModel.navigateBack.value == true)
    }
}
