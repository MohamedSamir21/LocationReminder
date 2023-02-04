package com.udacity.project4.locationreminders.data

import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import com.udacity.project4.locationreminders.data.dto.Result.Success
import com.udacity.project4.locationreminders.data.dto.Result.Error


// Use FakeDataSource that acts as a test double to the LocalDataSource
class FakeDataSource(var reminders: MutableList<ReminderDTO>? = mutableListOf()) : ReminderDataSource {
    private var shouldReturnError = false

    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        if (shouldReturnError) { // In case of error.
            return Error("Error while getting remainders!!")
        }
        // Otherwise, return the list.
        else reminders.let { return Success(ArrayList(it)) }
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        // Save the reminder.
        reminders?.add(reminder)
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        // In case of error.
        if (shouldReturnError) return Error("Reminder not found!")

        // Find the reminder with the passed id.
        val reminder = reminders!!.find {it.id == id }

        // Return the reminder with the id.
        return if (reminder != null) return Success(reminder)
                else Error("Reminder not found!")
    }

    override suspend fun deleteAllReminders() {
        // Delete all the reminders.
        reminders?.clear()
    }

    fun setReturnError(value: Boolean) {
        shouldReturnError = value
    }

}