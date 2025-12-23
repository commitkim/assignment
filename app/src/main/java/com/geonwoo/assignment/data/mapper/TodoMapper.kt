package com.geonwoo.assignment.data.mapper

import com.geonwoo.assignment.data.local.entity.TodoEntity
import com.geonwoo.assignment.data.remote.dto.TodoDto
import com.geonwoo.assignment.domain.model.Todo

object TodoMapper {
    
    // Entity -> Domain
    fun TodoEntity.toDomain(): Todo {
        return Todo(
            id = id,
            title = title,
            description = description,
            isCompleted = isCompleted,
            createdAt = createdAt
        )
    }

    // Domain -> Entity
    fun Todo.toEntity(): TodoEntity {
        return TodoEntity(
            id = id,
            title = title,
            description = description,
            isCompleted = isCompleted,
            createdAt = createdAt
        )
    }

    // DTO -> Domain
    fun TodoDto.toDomain(): Todo {
        return Todo(
            id = id,
            title = title,
            description = description,
            isCompleted = isCompleted,
            createdAt = createdAt
        )
    }

    // Domain -> DTO
    fun Todo.toDto(): TodoDto {
        return TodoDto(
            id = id,
            title = title,
            description = description,
            isCompleted = isCompleted,
            createdAt = createdAt
        )
    }

    // DTO -> Entity
    fun TodoDto.toEntity(): TodoEntity {
        return TodoEntity(
            id = id,
            title = title,
            description = description,
            isCompleted = isCompleted,
            createdAt = createdAt
        )
    }

    // List extensions
    fun List<TodoEntity>.toDomainList(): List<Todo> = map { it.toDomain() }
    fun List<TodoDto>.toDomainListFromDto(): List<Todo> = map { it.toDomain() }
    fun List<TodoDto>.toEntityList(): List<TodoEntity> = map { it.toEntity() }
}
