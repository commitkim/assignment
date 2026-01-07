package com.geonwoo.assignment.presentation

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.IdlingPolicies
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.recyclerview.widget.RecyclerView
import com.geonwoo.assignment.R
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class NavigationTest {

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
    fun navigateToDetail_andBack() {
        // First, add a todo item
        onView(withId(R.id.fabAdd)).perform(click())
        onView(withId(R.id.editTitle))
            .perform(typeText("Navigation Test"), closeSoftKeyboard())
        onView(withId(R.id.editDescription))
            .perform(typeText("Test Description"), closeSoftKeyboard())
        onView(withText("추가")).perform(click())

        // Espresso는 IdlingPolicies에 설정된 시간(1초) 내에
        // 비동기 작업 완료를 자동 대기

        // Click on the item to navigate to detail
        onView(withId(R.id.recyclerView))
            .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(0))
        onView(withId(R.id.recyclerView))
            .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))

        // Check detail screen is displayed
        onView(withId(R.id.editTitle))
            .check(matches(isCompletelyDisplayed()))
        onView(withId(R.id.btnSave))
            .check(matches(isDisplayed()))

        // Navigate back
        pressBack()

        // Check list screen is displayed
        onView(withId(R.id.recyclerView))
            .check(matches(isCompletelyDisplayed()))
    }

    @Test
    fun detailScreen_showsTodoData() {
        // First, add a todo item
        onView(withId(R.id.fabAdd)).perform(click())
        onView(withId(R.id.editTitle))
            .perform(typeText("Detail Test"), closeSoftKeyboard())
        onView(withId(R.id.editDescription))
            .perform(typeText("Detail Description"), closeSoftKeyboard())
        onView(withText("추가")).perform(click())

        // Espresso가 데이터베이스 작업 완료를 대기

        // Click on the item to navigate to detail
        onView(withId(R.id.recyclerView))
            .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(0))
        onView(withId(R.id.recyclerView))
            .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))

        // Check detail data is displayed correctly
        onView(withText("Detail Test"))
            .check(matches(isCompletelyDisplayed()))
    }
}