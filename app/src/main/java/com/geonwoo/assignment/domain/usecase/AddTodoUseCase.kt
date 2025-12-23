package com.geonwoo.assignment.domain.usecase

import com.geonwoo.assignment.domain.model.Todo
import com.geonwoo.assignment.domain.repository.TodoRepository
import javax.inject.Inject

class AddTodoUseCase @Inject constructor(
    private val repository: TodoRepository
) {
    suspend operator fun invoke(todo: Todo): Long {
        return repository.addTodo(todo)
    }
}
