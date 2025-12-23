package com.geonwoo.assignment.data.local

import com.geonwoo.assignment.data.local.dao.TodoDao
import com.geonwoo.assignment.data.local.entity.TodoEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalDataSource @Inject constructor(
    private val todoDao: TodoDao
) {
    fun getTodos(): Flow<List<TodoEntity>> {
        return todoDao.getTodos()
    }

    suspend fun getTodoById(id: Long): TodoEntity? {
        return todoDao.getTodoById(id)
    }

    suspend fun insertTodo(todo: TodoEntity): Long {
        return todoDao.insertTodo(todo)
    }

    suspend fun insertTodos(todos: List<TodoEntity>) {
        todoDao.insertTodos(todos)
    }

    suspend fun updateTodo(todo: TodoEntity) {
        todoDao.updateTodo(todo)
    }

    suspend fun deleteTodo(todo: TodoEntity) {
        todoDao.deleteTodo(todo)
    }

    suspend fun deleteAllTodos() {
        todoDao.deleteAllTodos()
    }
}
