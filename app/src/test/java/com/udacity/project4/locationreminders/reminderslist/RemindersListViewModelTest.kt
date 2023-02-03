package com.udacity.project4.locationreminders.reminderslist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.getOrAwaitValue
import junit.framework.Assert.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class RemindersListViewModelTest {

    //TODO: provide testing to the RemindersListViewModel and its live data objects

    // subject under test.
    private lateinit var remindersListViewModel: RemindersListViewModel

    private lateinit var remindersFakeDataSource: FakeDataSource

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Before
    fun setupViewModel(){
        // Given
        remindersFakeDataSource = FakeDataSource()
        remindersListViewModel = RemindersListViewModel(ApplicationProvider.getApplicationContext(), remindersFakeDataSource)
    }

    //https://stackoverflow.com/questions/57038848/koinappalreadystartedexception-a-koin-application-has-already-been-started
    @After
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun loadReminders_loading(){
        // Given this reminder
        val reminder = ReminderDataItem("remainder", "this is a dummy reminder", "location", 2.32322, 3.323323)


        // When calling the (loadReminders) method.
        mainCoroutineRule.pauseDispatcher()
        remindersListViewModel.loadReminders()

        // Then showLoading is true at the beginning.
        // and false after saving the reminder.
        assertTrue(remindersListViewModel.showLoading.getOrAwaitValue())
        mainCoroutineRule.resumeDispatcher()
        assertFalse(remindersListViewModel.showLoading.getOrAwaitValue())
    }

    @Test
    fun loadReminders_checkLoading_showsSuccess() = mainCoroutineRule.runBlockingTest{
        // Given this reminder and saving it.
        val reminder = ReminderDTO("remainder", "this is a dummy reminder", "location", 2.32322, 3.323323)
        remindersFakeDataSource.saveReminder(reminder)

        // When calling the (loadReminders) method.
        remindersListViewModel.loadReminders()

        // Then getting all the reminders ready to be displayed on the UI.
        val remindersFromFakeDataSource = remindersFakeDataSource.reminders
        val remindersFromListViewModel = remindersListViewModel.remindersList.getOrAwaitValue()

        remindersFromFakeDataSource?.forEach { reminderFromFakeDataSource ->
            assertEquals(reminderFromFakeDataSource.id, remindersFromListViewModel.iterator().next().id)
        }
    }

    @Test
    fun loadReminders_checkLoading_showsError(){
        // Make error happens.
        remindersFakeDataSource.setReturnError(true)
        remindersListViewModel.loadReminders()

        // Then showSnackBar shows an error.
        assertEquals("Error while getting remainders!!", remindersListViewModel.showSnackBar.getOrAwaitValue())
    }

}