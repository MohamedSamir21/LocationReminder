package com.udacity.project4.locationreminders.data

import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import com.udacity.project4.locationreminders.data.dto.Result.Success
import com.udacity.project4.locationreminders.data.dto.Result.Error


// Use FakeDataSource that acts as a test double to the LocalDataSource
class FakeDataSource(var reminders: MutableList<ReminderDTO>? = mutableListOf()) : ReminderDataSource {


    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        // Return the reminders.
        reminders?.let { return Success(ArrayList(it)) }
        return Error("No reminders are found")
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        // Save the reminder.
        reminders?.add(reminder)
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        // Return the reminder with the id
        return if (reminders!!.isNotEmpty()) {
            // Find the reminder with the passed id.
            val reminder = reminders?.find {it.id == id }
            // Check if it exists or not.
            if (reminder != null) return Success(reminder) else Error("This reminder doesn't exist")
        }else{
            Error("The list is empty. This reminder doesn't exist")
        }
    }

    override suspend fun deleteAllReminders() {
        // Delete all the reminders.
        reminders?.clear()
    }


}