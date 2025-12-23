package com.geonwoo.assignment.presentation

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
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

        // Wait for item to be added
        Thread.sleep(500)

        // Click on the item to navigate to detail
        onView(withId(R.id.recyclerView))
            .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))

        // Check detail screen is displayed
        Thread.sleep(300)
        onView(withId(R.id.editTitle))
            .check(matches(isDisplayed()))
        onView(withId(R.id.btnSave))
            .check(matches(isDisplayed()))

        // Navigate back
        pressBack()

        // Check list screen is displayed
        Thread.sleep(300)
        onView(withId(R.id.recyclerView))
            .check(matches(isDisplayed()))
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

        // Wait for item to be added
        Thread.sleep(500)

        // Click on the item to navigate to detail
        onView(withId(R.id.recyclerView))
            .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))

        // Check detail data is displayed correctly
        Thread.sleep(300)
        onView(withText("Detail Test"))
            .check(matches(isDisplayed()))
    }
}
