package com.geonwoo.assignment.data.remote

import com.geonwoo.assignment.data.remote.api.TodoApi
import com.geonwoo.assignment.data.remote.dto.TodoDto
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteDataSource @Inject constructor(
    private val todoApi: TodoApi
) {
    suspend fun getTodos(): List<TodoDto> {
        return try {
            todoApi.getTodos()
        } catch (e: Exception) {
            // 서버 연결 실패 시 빈 리스트 반환
            emptyList()
        }
    }

    suspend fun getTodoById(id: Long): TodoDto? {
        return try {
            todoApi.getTodoById(id)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun createTodo(todo: TodoDto): TodoDto? {
        return try {
            todoApi.createTodo(todo)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun updateTodo(todo: TodoDto): TodoDto? {
        return try {
            todoApi.updateTodo(todo.id, todo)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun deleteTodo(id: Long): Boolean {
        return try {
            todoApi.deleteTodo(id)
            true
        } catch (e: Exception) {
            false
        }
    }
}
