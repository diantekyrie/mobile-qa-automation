package com.qapractice.app

import androidx.test.espresso.Espresso.closeSoftKeyboard
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(LoginActivity::class.java)

    private fun logIn(username: String, password: String) {
        if (username.isNotEmpty()) onView(withId(R.id.username)).perform(typeText(username))
        if (password.isNotEmpty()) onView(withId(R.id.password)).perform(typeText(password))
        closeSoftKeyboard()
        onView(withId(R.id.login_button)).perform(click())
    }

    @Test
    fun validLoginOpensTaskList() {
        logIn(Credentials.VALID_USERNAME, Credentials.VALID_PASSWORD)

        onView(withId(R.id.welcome_text)).check(matches(withText("Welcome, tester")))
    }

    @Test
    fun emptyFieldsShowRequiredError() {
        logIn("", "")

        onView(withId(R.id.login_error))
            .check(matches(isDisplayed()))
            .check(matches(withText(R.string.error_required)))
    }

    @Test
    fun wrongPasswordShowsInvalidError() {
        logIn(Credentials.VALID_USERNAME, "wrong-password")

        onView(withId(R.id.login_error)).check(matches(withText(R.string.error_invalid)))
    }

    @Test
    fun unknownUserShowsInvalidError() {
        logIn("nobody", Credentials.VALID_PASSWORD)

        onView(withId(R.id.login_error)).check(matches(withText(R.string.error_invalid)))
    }
}
