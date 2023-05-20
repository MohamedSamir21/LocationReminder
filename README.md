# LocationReminder <br>

## Overview
This is a TODO list app with location reminders that remind the user to do something when the user is at a specific
location. The app will require the user to create an account and log in to set and access reminders (for more details, see the description section below). 

### Important Notes:
<ul>
  <li>This project was created based on educational purposes according to 
    <a href="https://www.udacity.com/course/android-kotlin-developer-nanodegree--nd940">Advanced Android Kotlin Development Nanodgree</a>.
  </li>
  <li>In order not to break the terms of service, the API key is not provided in this repo. 
    You can put your API key in the (google_maps_api.xml) file from this path ("app/src/debug/res/values/google_maps_api.xml") or directly press this 
    <a href="https://github.com/MohamedSamir21/LocationReminder/blob/master/app/src/debug/res/values/google_maps_api.xml">link</a>.
  </li>
</ul>
<hr>

## Skills Acquired from this Project
<ul>
  <li>User Authentication: Signup, Login & Logout</li>
  <li>Firebase Authentication</li>
  <li>Map View & Styling</li>
  <li>Geofencing</li>
  <li>MVVM</li>
  <li>Testing</li>
  <li>Compatibility</li>
</ul>

## Description

### User Authentication 
<ul>
  <li>
    <b>Login</b>: There is a Login screen to ask users to log in using an email address or a Google account. Upon successful login,
    navigate the user to the Reminders screen. If there is no account, the app should navigate to a Registration (Signup) screen.
  </li>
  <br>
  <li>
    <b>Signup</b>: There is a Registration screen to allow a user to register using an email address or a Google account.
  </li>
  <br>
  <li>
    <b>Logout</b>: The users can log out of the app and when the app starts again they are required to log in first.
  </li>
  <br>
</ul>
<strong>Note:</strong> The authentication is done using the Firebase console and includes Firebase UI.

### Map View
<ul>
  <li>
    <b>Map view</b>: There is a Map view that shows the user's current location.
    <ul>
      <li>It first asks the user's location access permission to show his/her current location on the map.</li>
      <li>The location access is handled in cases of user denial that the user is shown the right error messages.</li>
    </ul>
  </li>
  <br>
  <li>
    <b>POI</b>: The app asks the user to select a point of interest (POI) on the map to create a reminder.
    <ul>
      <li>The app asks the user to select a location or POI on the map and add a new marker at that location.</li>
      <li>The selected POI can be just a simple POI or an area or specific latitude and longitude. The POI has a location name.</li>
      <li>Upon saving, the selected location is returned to the Save Reminder page and the user is asked to input the title and description for the reminder.</li>
      <li>When the reminder is saved, a geofencing request is created. Allowing the user to take Circular Radius for the geofence is a bonus.</li>
    </ul>
  </li>
  <br>
  <li>
    <b>Compatibility</b>: The app works on all the different Android versions including Android Q.
  </li>
  <br>
  <li>
    <b>Map Styling</b>: The Map Styling is done using the map styling wizard to have a nice-looking map. Users have the option 
    to change the map type from the toolbar items.
  </li>
  <br>
  <li>
    <b>Geofencing</b>: When the user enters a geofence, a reminder is retrieved from the local storage
    and a notification showing the reminder title will appear, even if the app is not open.
  </li>
</ul>

### Reminders
<b>Reminders</b>: There is a screen to add a new reminder when a user reaches the selected location. Each new reminder includes:
<ul>
  <li>title</li>
  <li>description</li>
  <li>selected location</li>
  <strong>Note</strong>:
  <ul> 
   <li>The user-entered data is captured using live data and data binding.</li>
   <li>(RemindersLocalRepository) is used to save the reminder to the local DB, and the geofencing request is created after confirmation.</li>
  </ul>
</ul>

### Reminders list view

There is a screen that displays the reminders retrieved from local storage. 
<ul>
  <li>
    If there are no reminders, it displays a "No Data" indicator. If there are any errors, it displays an error message.
  </li>
  <li>
    The user can navigate from the Reminders list view screen to another screen to create a new reminder.
  </li>
</ul>

### Reminder notification
For each reminder, a geofencing request, in the background that fires up a notification when the user enters the geofencing area, is created.
<ul>
  <li>Details are displayed about a reminder when a selected POI is reached and the user clicked on the notification.</li>
  <li>When the user clicks a notification, a new screen appears to display the reminder details.</li>
</ul>

### Testing
  #### Tests include:
  <ul>
    <li>The app follows the MVVM design pattern and uses ViewModels to hold the live data objects,
      do the validation, and interact with the data sources.
    </li>
    <li>
      Automation Testing using ViewMatchers and ViewInteractions to simulate user interactions with the app.
    </li>
    <li>
      Testing for Snackbar and Toast messages.
    </li>
      Testing the fragments’ navigation.
    </li>
    <li>
      Inserting and retrieving data using DAO.
    </li>
    <li>
      Predictable errors like data not found.
    </li>
  </ul>
  
## Preview
<img src="https://github.com/MohamedSamir21/LocationReminder/assets/75276673/0d7915ce-78d4-489f-a840-7ca690a92987">
