package com.geonwoo.assignment.data.remote.api

import com.geonwoo.assignment.data.remote.dto.TodoDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface TodoApi {
    @GET("todos")
    suspend fun getTodos(): List<TodoDto>

    @GET("todos/{id}")
    suspend fun getTodoById(@Path("id") id: Long): TodoDto

    @POST("todos")
    suspend fun createTodo(@Body todo: TodoDto): TodoDto

    @PUT("todos/{id}")
    suspend fun updateTodo(@Path("id") id: Long, @Body todo: TodoDto): TodoDto

    @DELETE("todos/{id}")
    suspend fun deleteTodo(@Path("id") id: Long)
}
