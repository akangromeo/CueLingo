package com.example.cuelingo.ui.detaildictionary

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.example.cuelingo.R
import org.junit.Before
import org.junit.Test

class DetailDictionaryActivityTest{

    @Before
    fun setup() {
        ActivityScenario.launch(DetailDictionaryActivity::class.java)
    }

    @Test
    fun clickFindAnotherWordButton_opensDictionaryActivity() {
        (Espresso.onView(withId(R.id.ib_find_another))).perform(ViewActions.click())

        Espresso.onView(withId(R.id.dictionary_layout))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }
}