package com.udacity.project4.authentication

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.udacity.project4.R
import com.udacity.project4.locationreminders.RemindersActivity
import kotlinx.android.synthetic.main.activity_authentication.*

/**
 * This class should be the starting point of the app, It asks the users to sign in / register, and redirects the
 * signed in users to the RemindersActivity.
 */



class AuthenticationActivity : AppCompatActivity() {

    companion object{
        const val TAG = "MainFragment"
        const val SIGN_IN_REQUEST_CODE = 21
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authentication)
        determineUserDestination()
        // add (setOnClickListener) to the (loginButton) to handle when the users clicks.
        loginButton.setOnClickListener {
            signInWithGoogleOrEmail()
        }

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SIGN_IN_REQUEST_CODE) {
            val response = IdpResponse.fromResultIntent(data)
            // If the Sign In succeed, then go to the (RemindersActivity)
            if (resultCode == Activity.RESULT_OK) {
                // User successfully signed in
                Log.i(TAG, "Successfully signed in user ${FirebaseAuth.getInstance().currentUser?.displayName}!")
                // If the user was authenticated, send him to RemindersActivity.
                val intent = Intent(applicationContext, RemindersActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                // Sign in failed.
                Log.i(TAG, "Sign in unsuccessful ${response?.error?.errorCode}")
            }
        }
    }

    // This method to enable users sign in with google or email and password.
    private fun signInWithGoogleOrEmail() {
        // We choose the two options we want our users to be able to sing in (using google or email and password).
        val providers = arrayListOf(AuthUI.IdpConfig.GoogleBuilder().build()
            , AuthUI.IdpConfig.EmailBuilder().build())

        // Give the (providers), start the intent and then wait the response of this activity.
        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder().setAvailableProviders(providers).build(), AuthenticationActivity.SIGN_IN_REQUEST_CODE
        )
    }

    // This method to check the user's status and determine the user's destination.
    private fun determineUserDestination(){
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        if (currentUser != null) {
            val intent = Intent(this, RemindersActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            // Stay in this login Activity.
        }
    }
}
