package com.geonwoo.assignment.data.repository

import com.geonwoo.assignment.data.local.LocalDataSource
import com.geonwoo.assignment.data.mapper.TodoMapper.toDomain
import com.geonwoo.assignment.data.mapper.TodoMapper.toDomainList
import com.geonwoo.assignment.data.mapper.TodoMapper.toDomainListFromDto
import com.geonwoo.assignment.data.mapper.TodoMapper.toEntity
import com.geonwoo.assignment.data.mapper.TodoMapper.toEntityList
import com.geonwoo.assignment.data.memory.MemoryDataSource
import com.geonwoo.assignment.data.remote.RemoteDataSource
import com.geonwoo.assignment.domain.model.Todo
import com.geonwoo.assignment.domain.repository.TodoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TodoRepositoryImpl @Inject constructor(
    private val localDataSource: LocalDataSource,
    private val remoteDataSource: RemoteDataSource,
    private val memoryDataSource: MemoryDataSource
) : TodoRepository {

    override fun getTodos(): Flow<List<Todo>> {
        return localDataSource.getTodos().map { entities ->
            val todos = entities.toDomainList()
            // 메모리 캐시 업데이트
            memoryDataSource.saveTodos(todos)
            todos
        }
    }

    override suspend fun getTodoById(id: Long): Todo? {
        // 1. 먼저 메모리 캐시 확인
        memoryDataSource.getTodoById(id)?.let { return it }

        // 2. 로컬 DB 확인
        return localDataSource.getTodoById(id)?.toDomain()?.also {
            memoryDataSource.saveTodo(it)
        }
    }

    override suspend fun addTodo(todo: Todo): Long {
        // 로컬 DB에 저장
        val id = localDataSource.insertTodo(todo.toEntity())
        val savedTodo = todo.copy(id = id)
        
        // 메모리 캐시 업데이트
        memoryDataSource.saveTodo(savedTodo)
        
        return id
    }

    override suspend fun updateTodo(todo: Todo) {
        // 로컬 DB 업데이트
        localDataSource.updateTodo(todo.toEntity())
        
        // 메모리 캐시 업데이트
        memoryDataSource.saveTodo(todo)
    }

    override suspend fun deleteTodo(todo: Todo) {
        // 로컬 DB에서 삭제
        localDataSource.deleteTodo(todo.toEntity())
        
        // 메모리 캐시에서 삭제
        memoryDataSource.deleteTodo(todo.id)
    }

    override suspend fun refreshTodos() {
        // Remote에서 데이터 가져오기
        val remoteTodos = remoteDataSource.getTodos()
        
        if (remoteTodos.isNotEmpty()) {
            // 로컬 DB 초기화 후 저장
            localDataSource.deleteAllTodos()
            localDataSource.insertTodos(remoteTodos.toEntityList())
            
            // 메모리 캐시 업데이트
            memoryDataSource.clearCache()
            memoryDataSource.saveTodos(remoteTodos.toDomainListFromDto())
        }
    }
}
