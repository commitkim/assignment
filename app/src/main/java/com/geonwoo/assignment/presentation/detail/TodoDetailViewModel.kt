package com.geonwoo.assignment.presentation.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.geonwoo.assignment.domain.model.Todo
import com.geonwoo.assignment.domain.usecase.DeleteTodoUseCase
import com.geonwoo.assignment.domain.usecase.GetTodoByIdUseCase
import com.geonwoo.assignment.domain.usecase.UpdateTodoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TodoDetailViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val getTodoByIdUseCase: GetTodoByIdUseCase,
    private val updateTodoUseCase: UpdateTodoUseCase,
    private val deleteTodoUseCase: DeleteTodoUseCase
) : ViewModel() {

    private val todoId: Long = savedStateHandle.get<Long>("todoId") ?: -1L

    private val _todo = MutableLiveData<Todo?>()
    val todo: LiveData<Todo?> = _todo

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _navigateBack = MutableLiveData(false)
    val navigateBack: LiveData<Boolean> = _navigateBack

    init {
        loadTodo()
    }

    private fun loadTodo() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _todo.value = getTodoByIdUseCase(todoId)
            } catch (e: Exception) {
                _error.value = "로딩 실패: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateTodo(title: String, description: String) {
        val currentTodo = _todo.value ?: return

        if (title.isBlank()) {
            _error.value = "제목을 입력해주세요"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val updatedTodo = currentTodo.copy(
                    title = title,
                    description = description
                )
                updateTodoUseCase(updatedTodo)
                _todo.value = updatedTodo
                _navigateBack.value = true
            } catch (e: Exception) {
                _error.value = "업데이트 실패: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun toggleComplete() {
        val currentTodo = _todo.value ?: return

        viewModelScope.launch {
            try {
                val updatedTodo = currentTodo.copy(isCompleted = !currentTodo.isCompleted)
                updateTodoUseCase(updatedTodo)
                _todo.value = updatedTodo
            } catch (e: Exception) {
                _error.value = "업데이트 실패: ${e.message}"
            }
        }
    }

    fun deleteTodo() {
        val currentTodo = _todo.value ?: return

        viewModelScope.launch {
            _isLoading.value = true
            try {
                deleteTodoUseCase(currentTodo)
                _navigateBack.value = true
            } catch (e: Exception) {
                _error.value = "삭제 실패: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }

    fun onNavigateBackHandled() {
        _navigateBack.value = false
    }
}
