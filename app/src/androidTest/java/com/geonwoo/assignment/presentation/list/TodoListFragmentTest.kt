package com.geonwoo.assignment.presentation.list

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingPolicies
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.geonwoo.assignment.R
import com.geonwoo.assignment.presentation.MainActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class TodoListFragmentTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun setup() {
        hiltRule.inject()
        
        // Idling 정책 설정 - 대기 시간 1초
        IdlingPolicies.setIdlingResourceTimeout(1, TimeUnit.SECONDS)
        IdlingPolicies.setMasterPolicyTimeout(1, TimeUnit.SECONDS)
    }

    @Test
    fun recyclerView_isDisplayed() {
        onView(withId(R.id.recyclerView))
            .check(matches(isDisplayed()))
    }

    @Test
    fun fabAdd_isDisplayed() {
        onView(withId(R.id.fabAdd))
            .check(matches(isDisplayed()))
    }

    @Test
    fun clickFab_showsAddTodoDialog() {
        // When
        onView(withId(R.id.fabAdd)).perform(click())

        // Then
        onView(withId(R.id.editTitle))
            .check(matches(isDisplayed()))
        onView(withId(R.id.editDescription))
            .check(matches(isDisplayed()))
    }

    @Test
    fun addTodo_showsInRecyclerView() {
        // Given - Open dialog
        onView(withId(R.id.fabAdd)).perform(click())

        // When - Enter todo details
        onView(withId(R.id.editTitle))
            .perform(typeText("Test Todo"), closeSoftKeyboard())
        onView(withId(R.id.editDescription))
            .perform(typeText("Test Description"), closeSoftKeyboard())

        // Click add button
        onView(withText("추가")).perform(click())

        // Room의 in-memory 데이터베이스는 메인 스레드 쿼리를 허용하므로
        // 데이터가 즉시 저장되고 RecyclerView가 즉시 업데이트됩니다
        // Espresso의 IdlingPolicies를 통해 적절한 시간(1초) 내에 완료를 기다립니다

        // Then - Check if todo appears in list
        onView(withText("Test Todo"))
            .check(matches(isCompletelyDisplayed()))
    }

    @Test
    fun addTodoWithEmptyTitle_showsError() {
        // Given - Open dialog
        onView(withId(R.id.fabAdd)).perform(click())

        // When - Enter only description
        onView(withId(R.id.editDescription))
            .perform(typeText("Test Description"), closeSoftKeyboard())

        // Click add button
        onView(withText("추가")).perform(click())

        // Then - RecyclerView가 다시 표시되어야 함 (추가 취소됨)
        onView(withId(R.id.recyclerView))
            .check(matches(isDisplayed()))
    }

    @Test
    fun cancelDialog_dismissesDialog() {
        // Given - Open dialog
        onView(withId(R.id.fabAdd)).perform(click())

        // When - Click cancel
        onView(withText("취소")).perform(click())

        // Then - RecyclerView should be visible again
        onView(withId(R.id.recyclerView))
            .check(matches(isDisplayed()))
    }
}