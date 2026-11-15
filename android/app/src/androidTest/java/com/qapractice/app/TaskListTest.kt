package com.qapractice.app

import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.hasErrorText
import androidx.test.espresso.matcher.ViewMatchers.isChecked
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.instanceOf
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TaskListTest {

    private val launchIntent =
        Intent(ApplicationProvider.getApplicationContext(), TaskListActivity::class.java)
            .putExtra(TaskListActivity.EXTRA_USERNAME, "tester")

    @get:Rule
    val activityRule = ActivityScenarioRule<TaskListActivity>(launchIntent)

    @Before
    fun registerIdlingResource() {
        IdlingRegistry.getInstance().register(SyncIdlingResource.resource)
    }

    @After
    fun unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(SyncIdlingResource.resource)
    }

    private fun addTask(name: String) {
        onView(withId(R.id.new_task_input)).perform(typeText(name), closeSoftKeyboard())
        onView(withId(R.id.add_task_button)).perform(click())
    }

    /** ListView rows are found by their data with onData, not by view id. */
    private fun taskRow(name: String) =
        onData(allOf(instanceOf(String::class.java), equalTo(name)))
            .inAdapterView(withId(R.id.task_list))

    @Test
    fun startsWithNoTasks() {
        onView(withId(R.id.task_summary)).check(matches(withText("Tasks: 0 | Done: 0")))
    }

    @Test
    fun addedTaskAppearsInList() {
        addTask("Write test plan")

        taskRow("Write test plan").check(matches(isDisplayed()))
        onView(withId(R.id.task_summary)).check(matches(withText("Tasks: 1 | Done: 0")))
    }

    @Test
    fun emptyTaskIsRejected() {
        onView(withId(R.id.add_task_button)).perform(click())

        onView(withId(R.id.new_task_input)).check(matches(hasErrorText("Task can't be empty")))
        onView(withId(R.id.task_summary)).check(matches(withText("Tasks: 0 | Done: 0")))
    }

    @Test
    fun tappingTaskMarksItDone() {
        addTask("File bug report")

        taskRow("File bug report").perform(click())

        taskRow("File bug report").check(matches(isChecked()))
        onView(withId(R.id.task_summary)).check(matches(withText("Tasks: 1 | Done: 1")))
    }

    /**
     * Sync takes 2 seconds. Without the registered idling resource this check would run
     * immediately and fail on "Syncing…". With it, Espresso waits until sync is done.
     */
    @Test
    fun syncWaitsForBackgroundWork() {
        addTask("Run regression")
        addTask("Triage crashes")

        onView(withId(R.id.sync_button)).perform(click())

        onView(withId(R.id.sync_status)).check(matches(withText("Synced 2 tasks")))
    }
}
