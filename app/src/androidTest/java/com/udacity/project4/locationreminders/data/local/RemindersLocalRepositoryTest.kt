package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result.*
import com.udacity.project4.locationreminders.data.dto.succeeded
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Medium Test to test the repository
@MediumTest
class RemindersLocalRepositoryTest {

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var localDataSource: RemindersLocalRepository
    private lateinit var database: RemindersDatabase

    @Before
    fun setup() {
        // Using an in-memory database for testing, because it doesn't survive killing the process.
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()

        localDataSource =
            RemindersLocalRepository(
                database.reminderDao(),
                Dispatchers.Main
            )
    }

    @After
    fun cleanUp() {
        database.close()
    }

    @Test
    fun saveReminderAndGetReminder() = runBlocking{
        // GIVEN - save a remainder
        val reminder = ReminderDTO("remainder", "this is a dummy reminder", "location", 2.32322, 3.323323)
        localDataSource.saveReminder(reminder)

        // WHEN - Get the remainder by id from the database.
        val returnedReminder = localDataSource.getReminder(reminder.id)

        // THEN - The returned  remainder matches the saved one.
        assertThat(returnedReminder.succeeded, `is`(true))
        returnedReminder as Success
        assertThat(returnedReminder.data.id, `is`(reminder.id))
        assertThat(returnedReminder.data.title, `is`(reminder.title))
        assertThat(returnedReminder.data.description, `is`(reminder.description))
        assertThat(returnedReminder.data.location, `is`(reminder.location))
        assertThat(returnedReminder.data.latitude, `is`(reminder.latitude))
        assertThat(returnedReminder.data.longitude, `is`(reminder.longitude))
    }

    @Test
    fun getReminders() = runBlocking {
        // GIVEN - These remainders
        val reminder0 = ReminderDTO("remainder1", "this is a dummy reminder", "location", 2.32322, 3.323323)
        val reminder1 = ReminderDTO("remainder1", "this is a dummy reminder", "location", 2.32322, 3.323323)
        val reminder2 = ReminderDTO("remainder1", "this is a dummy reminder", "location", 2.32322, 3.323323)
        val reminder3= ReminderDTO("remainder1", "this is a dummy reminder", "location", 2.32322, 3.323323)
        localDataSource.saveReminder(reminder0)
        localDataSource.saveReminder(reminder1)
        localDataSource.saveReminder(reminder2)
        localDataSource.saveReminder(reminder3)

        // WHEN - Get all remainders from the database.
        val returnedReminders = localDataSource.getReminders()

        // THEN - The returned reminders matches the saved ones.
        assertThat(returnedReminders, notNullValue())
        assertThat(returnedReminders.succeeded, `is`(true))
        returnedReminders as Success
        assertThat(returnedReminders.data[0], `is`(reminder0))
        assertThat(returnedReminders.data[1], `is`(reminder1))
        assertThat(returnedReminders.data[2], `is`(reminder2))
        assertThat(returnedReminders.data[3], `is`(reminder3))
    }

    @Test
    fun deleteAllReminders() = runBlocking{
        // GIVEN - These remainders
        val reminder0 = ReminderDTO("remainder1", "this is a dummy reminder", "location", 2.32322, 3.323323)
        val reminder1 = ReminderDTO("remainder1", "this is a dummy reminder", "location", 2.32322, 3.323323)
        val reminder2 = ReminderDTO("remainder1", "this is a dummy reminder", "location", 2.32322, 3.323323)
        val reminder3= ReminderDTO("remainder1", "this is a dummy reminder", "location", 2.32322, 3.323323)
        localDataSource.saveReminder(reminder0)
        localDataSource.saveReminder(reminder1)
        localDataSource.saveReminder(reminder2)
        localDataSource.saveReminder(reminder3)

        // WHEN - Delete all remainders from the database and get the remainders after deleting.
        localDataSource.deleteAllReminders()
        val returnedReminders = localDataSource.getReminders()

        // THEN - Then there are no remainders in the database.
        assertThat(returnedReminders.succeeded, `is`(true))
        returnedReminders as Success
        assertThat(returnedReminders.data, `is`(emptyList()))

    }
}