package com.qapractice.app

import androidx.test.espresso.idling.CountingIdlingResource

/**
 * Counts in-flight sync jobs so Espresso can wait for them instead of sleeping.
 * Tests register [resource] with IdlingRegistry; the app calls increment/decrement.
 */
object SyncIdlingResource {
    val resource = CountingIdlingResource("sync")

    fun increment() = resource.increment()

    fun decrement() {
        if (!resource.isIdleNow) resource.decrement()
    }
}
