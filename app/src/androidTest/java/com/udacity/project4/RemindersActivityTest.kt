package com.udacity.project4

import android.app.Activity
import android.app.Application
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.withDecorView
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import com.google.android.material.internal.ContextUtils.getActivity
import com.udacity.project4.locationreminders.RemindersActivity
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.local.LocalDB
import com.udacity.project4.locationreminders.data.local.RemindersLocalRepository
import com.udacity.project4.locationreminders.reminderslist.RemindersListViewModel
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import com.udacity.project4.util.DataBindingIdlingResource
import com.udacity.project4.util.monitorActivity
import com.udacity.project4.utils.EspressoIdlingResource
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.not
import org.hamcrest.Matchers
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.AutoCloseKoinTest
import org.koin.test.KoinTest
import org.koin.test.get

@RunWith(AndroidJUnit4::class)
@LargeTest
//END TO END test to black box test the app
class RemindersActivityTest :
    KoinTest {// Extended Koin Test - embed autoclose @after method to close Koin after every test

    private lateinit var repository: ReminderDataSource
    private lateinit var appContext: Application
    private val dataBindingIdlingResource = DataBindingIdlingResource()

    // Use activityRule to start an activity before each test method, and to shut it down after each test method.
    // We can use it to call activity method to get a reference to the current instance of the activity under test (RemindersActivity).
    // https://developer.android.com/reference/androidx/test/rule/ActivityTestRule
    @get:Rule
    val activityRule = ActivityTestRule(RemindersActivity::class.java)

    @Before
    fun init() {
        stopKoin()//stop the original app koin
        appContext = getApplicationContext()
        val myModule = module {
            viewModel {
                RemindersListViewModel(
                    appContext,
                    get() as ReminderDataSource
                )
            }
            single {
                SaveReminderViewModel(
                    appContext,
                    get() as ReminderDataSource
                )
            }
            single { RemindersLocalRepository(get()) as ReminderDataSource }
            single { LocalDB.createRemindersDao(appContext) }
        }
        //declare a new koin module
        startKoin {
            modules(listOf(myModule))
        }
        //Get our real repository
        repository = get()

        //clear the data to start fresh
        runBlocking {
            repository.deleteAllReminders()
        }
    }


//    TODO: add End to End testing to the app

    /**
     * Idling resources tell Espresso that the app is idle or busy. This is needed when operations
     * are not scheduled in the main Looper (for example when executed on a different thread).
     */
    @Before
    fun registerIdlingResource() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
        IdlingRegistry.getInstance().register(dataBindingIdlingResource)
    }

    /**
     * Unregister your Idling Resource so it can be garbage collected and does not leak any memory.
     */
    @After
    fun unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
        IdlingRegistry.getInstance().unregister(dataBindingIdlingResource)
    }

    @Test
    fun saveAndCheckDisplayingTask(){
        // GIVEN - Start up Remainders screen.
        val activityScenario = ActivityScenario.launch(RemindersActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)


        // WHEN - Click on the add FAB button , add a remainder, fill the fields with appropriate data and Save.
        onView(withId(R.id.addReminderFAB)).perform(click())
        onView(withId(R.id.remainderTitle))
            .perform(typeText("remainder"), closeSoftKeyboard())
        onView(withId(R.id.remainderDescription))
            .perform(typeText("this is a dummy reminder"), closeSoftKeyboard())
        onView(withId(R.id.selectLocation)).perform(click())
        onView(withId(R.id.saveLocation)).perform(click())
        onView(withId(R.id.saveReminder)).perform(click())

        // THEN - Verify that the remainder is displayed.
        onView(withText("remainder")).check(matches(isDisplayed()))
        onView(withText("this is a dummy reminder")).check(matches(isDisplayed()))
        onView(withId(R.id.selectedLocation)).check(matches(not(isDisplayed())))

        // Make sure the activity is closed before resetting the db:
        activityScenario.close()
    }

    @Test
    fun detectTitleSnackBarMessageError(){
        // GIVEN - Start up Remainders screen.
        val activityScenario = ActivityScenario.launch(RemindersActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        // WHEN - Click on the add FAB button, leave the title field empty and click Save button.
        onView(withId(R.id.addReminderFAB)).perform(click())
        onView(withId(R.id.saveReminder)).perform(click())

        // THEN - Verify that title error message appears when the user leaves the title field empty.
        onView(withId(R.id.snackbar_text))
            .check(matches(withText(R.string.select_title)))

        // Make sure the activity is closed before resetting the db:
        activityScenario.close()
    }

    @Test
    fun detectLocationSnackBarMessageError(){
        // GIVEN - Start up Remainders screen.
        val activityScenario = ActivityScenario.launch(RemindersActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        // WHEN - Click on the add FAB button , fill the title field, don't select  the location and click Save button.
        onView(withId(R.id.addReminderFAB)).perform(click())
        onView(withId(R.id.remainderTitle))
            .perform(typeText("remainder"), closeSoftKeyboard())
        onView(withId(R.id.saveReminder)).perform(click())

        // THEN - Verify that location error message appears when the user doesn't select the location .
        onView(withId(com.google.android.material.R.id.snackbar_text))
            .check(matches(withText(R.string.err_select_location)))

        // Make sure the activity is closed before resetting the db:
        activityScenario.close()
    }

    @Test
    fun detectToastSavingSuccess(){
        // GIVEN - Start up Remainders screen.
        val activityScenario = ActivityScenario.launch(RemindersActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        // WHEN - Click on the add FAB button , fill the input fields and click Save button.
        onView(withId(R.id.addReminderFAB)).perform(click())
        onView(withId(R.id.remainderTitle))
            .perform(typeText("remainder"), closeSoftKeyboard())
        onView(withId(R.id.selectLocation)).perform(click())
        onView(withId(R.id.fragment_map)).perform(longClick())
        onView(withId(R.id.saveLocation)).perform(click())
        onView(withId(R.id.saveReminder)).perform(click())

        // THEN - Verify that the toast with saving message success appears.
        // https://developer.android.com/reference/androidx/test/rule/ActivityTestRule
        // https://stackoverflow.com/a/28606603/16109025
        onView(withText(R.string.reminder_saved)).inRoot(withDecorView(not(`is`(activityRule.activity.window.decorView))))
            .check(matches(isDisplayed()))

        // Make sure the activity is closed before resetting the db:
        activityScenario.close()

    }

}
