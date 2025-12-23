package com.geonwoo.assignment.presentation.list

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import com.geonwoo.assignment.domain.model.Todo
import com.geonwoo.assignment.domain.usecase.AddTodoUseCase
import com.geonwoo.assignment.domain.usecase.DeleteTodoUseCase
import com.geonwoo.assignment.domain.usecase.GetTodosUseCase
import com.geonwoo.assignment.domain.usecase.UpdateTodoUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TodoListViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var savedStateHandle: SavedStateHandle
    private lateinit var getTodosUseCase: GetTodosUseCase
    private lateinit var addTodoUseCase: AddTodoUseCase
    private lateinit var updateTodoUseCase: UpdateTodoUseCase
    private lateinit var deleteTodoUseCase: DeleteTodoUseCase
    private lateinit var viewModel: TodoListViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        savedStateHandle = SavedStateHandle()
        getTodosUseCase = mockk()
        addTodoUseCase = mockk()
        updateTodoUseCase = mockk()
        deleteTodoUseCase = mockk()

        every { getTodosUseCase() } returns flowOf(emptyList())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(): TodoListViewModel {
        return TodoListViewModel(
            savedStateHandle,
            getTodosUseCase,
            addTodoUseCase,
            updateTodoUseCase,
            deleteTodoUseCase
        )
    }

    @Test
    fun `addTodo with valid title adds todo`() = runTest {
        // Given
        coEvery { addTodoUseCase(any()) } returns 1L
        viewModel = createViewModel()

        // When
        viewModel.addTodo("New Todo", "Description")
        advanceUntilIdle()

        // Then
        coVerify { addTodoUseCase(match { it.title == "New Todo" }) }
    }

    @Test
    fun `addTodo with blank title sets error`() = runTest {
        // Given
        viewModel = createViewModel()

        // When
        viewModel.addTodo("", "Description")
        advanceUntilIdle()

        // Then
        assertEquals("제목을 입력해주세요", viewModel.error.value)
    }

    @Test
    fun `toggleTodoComplete updates todo`() = runTest {
        // Given
        val todo = Todo(id = 1, title = "Test", description = "Desc", isCompleted = false)
        coEvery { updateTodoUseCase(any()) } just runs
        viewModel = createViewModel()

        // When
        viewModel.toggleTodoComplete(todo)
        advanceUntilIdle()

        // Then
        coVerify { updateTodoUseCase(match { it.isCompleted == true }) }
    }

    @Test
    fun `deleteTodo removes todo`() = runTest {
        // Given
        val todo = Todo(id = 1, title = "Test", description = "Desc")
        coEvery { deleteTodoUseCase(todo) } just runs
        viewModel = createViewModel()

        // When
        viewModel.deleteTodo(todo)
        advanceUntilIdle()

        // Then
        coVerify { deleteTodoUseCase(todo) }
    }

    @Test
    fun `clearError sets error to null`() = runTest {
        // Given
        viewModel = createViewModel()
        viewModel.addTodo("", "")  // This sets an error
        advanceUntilIdle()

        // When
        viewModel.clearError()

        // Then
        assertEquals(null, viewModel.error.value)
    }
}
