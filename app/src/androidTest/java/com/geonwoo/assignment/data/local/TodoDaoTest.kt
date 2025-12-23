package com.geonwoo.assignment.data.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.geonwoo.assignment.data.local.dao.TodoDao
import com.geonwoo.assignment.data.local.database.AppDatabase
import com.geonwoo.assignment.data.local.entity.TodoEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TodoDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var todoDao: TodoDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        todoDao = database.todoDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertTodo_andGetById() = runTest {
        // Given
        val todo = TodoEntity(
            title = "Test Todo",
            description = "Test Description"
        )

        // When
        val id = todoDao.insertTodo(todo)
        val result = todoDao.getTodoById(id)

        // Then
        assertEquals("Test Todo", result?.title)
        assertEquals("Test Description", result?.description)
    }

    @Test
    fun getTodos_returnsAllTodos() = runTest {
        // Given
        val todo1 = TodoEntity(title = "Todo 1", description = "Desc 1")
        val todo2 = TodoEntity(title = "Todo 2", description = "Desc 2")
        todoDao.insertTodo(todo1)
        todoDao.insertTodo(todo2)

        // When
        val result = todoDao.getTodos().first()

        // Then
        assertEquals(2, result.size)
    }

    @Test
    fun getTodos_returnsSortedByCreatedAtDescending() = runTest {
        // Given
        val todo1 = TodoEntity(title = "Old", description = "Desc", createdAt = 1000)
        val todo2 = TodoEntity(title = "New", description = "Desc", createdAt = 2000)
        todoDao.insertTodo(todo1)
        todoDao.insertTodo(todo2)

        // When
        val result = todoDao.getTodos().first()

        // Then
        assertEquals("New", result[0].title)
        assertEquals("Old", result[1].title)
    }

    @Test
    fun updateTodo_updatesCorrectly() = runTest {
        // Given
        val todo = TodoEntity(title = "Original", description = "Desc")
        val id = todoDao.insertTodo(todo)

        // When
        val updatedTodo = todo.copy(id = id, title = "Updated", isCompleted = true)
        todoDao.updateTodo(updatedTodo)

        // Then
        val result = todoDao.getTodoById(id)
        assertEquals("Updated", result?.title)
        assertTrue(result?.isCompleted == true)
    }

    @Test
    fun deleteTodo_removesTodo() = runTest {
        // Given
        val todo = TodoEntity(title = "To Delete", description = "Desc")
        val id = todoDao.insertTodo(todo)

        // When
        todoDao.deleteTodo(todo.copy(id = id))

        // Then
        val result = todoDao.getTodoById(id)
        assertNull(result)
    }

    @Test
    fun deleteAllTodos_clearsDatabase() = runTest {
        // Given
        todoDao.insertTodo(TodoEntity(title = "Todo 1", description = "Desc"))
        todoDao.insertTodo(TodoEntity(title = "Todo 2", description = "Desc"))

        // When
        todoDao.deleteAllTodos()

        // Then
        val result = todoDao.getTodos().first()
        assertTrue(result.isEmpty())
    }

    @Test
    fun insertTodos_insertsMultipleTodos() = runTest {
        // Given
        val todos = listOf(
            TodoEntity(title = "Todo 1", description = "Desc 1"),
            TodoEntity(title = "Todo 2", description = "Desc 2"),
            TodoEntity(title = "Todo 3", description = "Desc 3")
        )

        // When
        todoDao.insertTodos(todos)

        // Then
        val result = todoDao.getTodos().first()
        assertEquals(3, result.size)
    }

    @Test
    fun getTodoById_returnsNullForNonExistent() = runTest {
        // When
        val result = todoDao.getTodoById(999)

        // Then
        assertNull(result)
    }
}
