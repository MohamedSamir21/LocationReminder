package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest;
import com.udacity.project4.locationreminders.data.dto.ReminderDTO

import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;

import kotlinx.coroutines.ExperimentalCoroutinesApi;
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Test

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Unit test the DAO
@SmallTest
class RemindersDaoTest {

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: RemindersDatabase

    @Before
    fun initDb() {
        // Using an in-memory database so that the information stored here disappears when the
        // process is killed.
        database = Room.inMemoryDatabaseBuilder(
            getApplicationContext(),
            RemindersDatabase::class.java
        ).build()
    }

    @After
    fun closeDb() = database.close()

    @Test
    fun saveReminderAndGetReminderById() = runBlockingTest{
        // GIVEN - save a remainder
        val reminder = ReminderDTO("remainder", "this is a dummy reminder", "location", 2.32322, 3.323323)
        database.reminderDao().saveReminder(reminder)

        // WHEN - Get the remainder by id from the database.
        val returnedRemainder= database.reminderDao().getReminderById(reminder.id)

        // THEN - The returned  remainder matches the saved one.
        assertThat<ReminderDTO>(returnedRemainder as ReminderDTO, notNullValue())
        assertThat(returnedRemainder.id, `is`(reminder.id))
        assertThat(returnedRemainder.title, `is`(reminder.title))
        assertThat(returnedRemainder.description, `is`(reminder.description))
        assertThat(returnedRemainder.location, `is`(reminder.location))
        assertThat(returnedRemainder.latitude, `is`(reminder.latitude))
        assertThat(returnedRemainder.longitude, `is`(reminder.longitude))
    }

    @Test
    fun getReminders() = runBlockingTest {
        // GIVEN - These remainders
        val reminder0 = ReminderDTO("remainder1", "this is a dummy reminder", "location", 2.32322, 3.323323)
        val reminder1 = ReminderDTO("remainder1", "this is a dummy reminder", "location", 2.32322, 3.323323)
        val reminder2 = ReminderDTO("remainder1", "this is a dummy reminder", "location", 2.32322, 3.323323)
        val reminder3= ReminderDTO("remainder1", "this is a dummy reminder", "location", 2.32322, 3.323323)
        database.reminderDao().saveReminder(reminder0)
        database.reminderDao().saveReminder(reminder1)
        database.reminderDao().saveReminder(reminder2)
        database.reminderDao().saveReminder(reminder3)

        // WHEN - Get all remainders from the database.
        val returnedReminders = database.reminderDao().getReminders()

        // THEN - The returned reminders matches the saved ones.
        assertThat(returnedReminders, notNullValue())
        assertThat(returnedReminders[0], `is`(reminder0))
        assertThat(returnedReminders[1], `is`(reminder1))
        assertThat(returnedReminders[2], `is`(reminder2))
        assertThat(returnedReminders[3], `is`(reminder3))
    }

    @Test
    fun deleteAllReminders() = runBlockingTest{
        // GIVEN - These remainders
        val reminder0 = ReminderDTO("remainder1", "this is a dummy reminder", "location", 2.32322, 3.323323)
        val reminder1 = ReminderDTO("remainder1", "this is a dummy reminder", "location", 2.32322, 3.323323)
        val reminder2 = ReminderDTO("remainder1", "this is a dummy reminder", "location", 2.32322, 3.323323)
        val reminder3= ReminderDTO("remainder1", "this is a dummy reminder", "location", 2.32322, 3.323323)
        database.reminderDao().saveReminder(reminder0)
        database.reminderDao().saveReminder(reminder1)
        database.reminderDao().saveReminder(reminder2)
        database.reminderDao().saveReminder(reminder3)

        // WHEN - Delete all remainders from the database and get the remainders after deleting.
         database.reminderDao().deleteAllReminders()
        val returnedReminders = database.reminderDao().getReminders()

        // THEN - Then there are no remainders in the database.
        assertThat(returnedReminders, `is`(emptyList()))
    }
}