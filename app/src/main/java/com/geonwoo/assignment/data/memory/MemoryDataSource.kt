package com.geonwoo.assignment.data.memory

import com.geonwoo.assignment.domain.model.Todo
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MemoryDataSource @Inject constructor() {
    private val cache = mutableMapOf<Long, Todo>()

    fun getTodos(): List<Todo> {
        return cache.values.toList().sortedByDescending { it.createdAt }
    }

    fun getTodoById(id: Long): Todo? {
        return cache[id]
    }

    fun saveTodo(todo: Todo) {
        cache[todo.id] = todo
    }

    fun saveTodos(todos: List<Todo>) {
        todos.forEach { todo ->
            cache[todo.id] = todo
        }
    }

    fun deleteTodo(id: Long) {
        cache.remove(id)
    }

    fun clearCache() {
        cache.clear()
    }

    fun hasCache(): Boolean {
        return cache.isNotEmpty()
    }
}
