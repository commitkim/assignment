package com.geonwoo.assignment.presentation.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.geonwoo.assignment.domain.model.Todo
import com.geonwoo.assignment.domain.usecase.AddTodoUseCase
import com.geonwoo.assignment.domain.usecase.DeleteTodoUseCase
import com.geonwoo.assignment.domain.usecase.GetTodosUseCase
import com.geonwoo.assignment.domain.usecase.UpdateTodoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TodoListViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val getTodosUseCase: GetTodosUseCase,
    private val addTodoUseCase: AddTodoUseCase,
    private val updateTodoUseCase: UpdateTodoUseCase,
    private val deleteTodoUseCase: DeleteTodoUseCase
) : ViewModel() {

    val todos: LiveData<List<Todo>> = getTodosUseCase().asLiveData()

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun addTodo(title: String, description: String) {
        if (title.isBlank()) {
            _error.value = "제목을 입력해주세요"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val todo = Todo(
                    title = title,
                    description = description
                )
                addTodoUseCase(todo)
            } catch (e: Exception) {
                _error.value = "추가 실패: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun toggleTodoComplete(todo: Todo) {
        viewModelScope.launch {
            try {
                updateTodoUseCase(todo.copy(isCompleted = !todo.isCompleted))
            } catch (e: Exception) {
                _error.value = "업데이트 실패: ${e.message}"
            }
        }
    }

    fun deleteTodo(todo: Todo) {
        viewModelScope.launch {
            try {
                deleteTodoUseCase(todo)
            } catch (e: Exception) {
                _error.value = "삭제 실패: ${e.message}"
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}
