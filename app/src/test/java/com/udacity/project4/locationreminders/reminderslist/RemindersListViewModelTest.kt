package com.udacity.project4.locationreminders.reminderslist

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.data.FakeDataSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
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

    private lateinit var reminders: FakeDataSource

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setupViewModel(){
        // Given
        reminders = FakeDataSource()
        remindersListViewModel = RemindersListViewModel(ApplicationProvider.getApplicationContext(), reminders)
    }

    //https://stackoverflow.com/questions/57038848/koinappalreadystartedexception-a-koin-application-has-already-been-started
    @After
    fun tearDown() {
        stopKoin()
    }


}