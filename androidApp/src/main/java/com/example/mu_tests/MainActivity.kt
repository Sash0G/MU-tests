package com.example.mu_tests

import android.content.Context
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest

class MainActivity : AppCompatActivity() {
    lateinit var firebaseRemoteConfig: FirebaseRemoteConfig
    lateinit var button: Button
    lateinit var button2: Button
    lateinit var sginInButton: Button
    lateinit var signUpButton: Button
    lateinit var userLayout: androidx.constraintlayout.widget.ConstraintLayout
    lateinit var emailLayout: androidx.constraintlayout.widget.ConstraintLayout
    lateinit var keyLayout: androidx.constraintlayout.widget.ConstraintLayout
    lateinit var textOr: TextView
    lateinit var emailInput: EditText
    lateinit var passwordInput: EditText
    lateinit var usernameInput: EditText

    private fun checkForUpdates() {
        firebaseRemoteConfig.fetchAndActivate()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val latestVersionCode = firebaseRemoteConfig.getString("latest_version_code")
                    val downloadUrl = firebaseRemoteConfig.getString("download_url")

                    println("--------------------")
                    println(latestVersionCode)
                    println(getCurrentVersionCode())
                    println("--------------------")
                    if (latestVersionCode > getCurrentVersionCode()) {
                        showUpdateDialog(downloadUrl)
                    }
                }
            }
    }

    private fun getCurrentVersionCode(): String {
        return try {
            val pInfo: PackageInfo = packageManager.getPackageInfo(packageName, 0)
            pInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            ""
        }
    }

    private fun showUpdateDialog(downloadUrl: String) {
        AlertDialog.Builder(this)
            .setTitle("Update Available")
            .setMessage("A new version of the app is available. Please update to the latest version.")
            .setPositiveButton("Update") { _, _ ->
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(downloadUrl))
                startActivity(intent)
            }
            .setNegativeButton("Later", null)
            .show()
    }

    private fun initialize() {
        button = findViewById<Button>(R.id.button1)
        button2 = findViewById<Button>(R.id.button2)
        signUpButton = findViewById<Button>(R.id.signUp)
        sginInButton = findViewById<Button>(R.id.signIn)
        userLayout =
            findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.userLayout)
        emailLayout =
            findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.emailLayout)
        keyLayout = findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.keyLayout)
        textOr = findViewById<TextView>(R.id.or)
        emailInput = findViewById<EditText>(R.id.emailInput)
        passwordInput = findViewById<EditText>(R.id.passwordInput)
        usernameInput = findViewById<EditText>(R.id.usernameInput)
    }

    private fun signIn() {

        val layout =
            findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.signInMenu)
        layout.visibility = android.view.View.VISIBLE
        layout.translationY = 0.8f * resources.displayMetrics.heightPixels
        layout.animate()
            .translationY(0f)
            .alpha(1f)
            .setDuration(1000)
        var signUpToggle = false
        signUpButton.setOnClickListener {

            if (!signUpToggle) {
                userLayout.visibility = android.view.View.VISIBLE

                sginInButton.visibility = android.view.View.INVISIBLE
                textOr.visibility = android.view.View.INVISIBLE
                emailLayout.translationY = 0f
                emailLayout.animate()
                    .translationY(0.1f * resources.displayMetrics.heightPixels)
                    .alpha(1f)
                    .setDuration(300)
                keyLayout.translationY = 0f
                keyLayout.animate()
                    .translationY(0.1f * resources.displayMetrics.heightPixels)
                    .alpha(1f)
                    .setDuration(300)
                signUpToggle = true
            } else {
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(
                    emailInput.text.trim().toString(),
                    passwordInput.text.trim().toString()
                )
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            println("User signed up successfully!")

                            signUpToggle = false
                            userLayout.visibility = android.view.View.INVISIBLE
                            sginInButton.visibility = android.view.View.VISIBLE
                            textOr.visibility = android.view.View.VISIBLE

                            emailLayout.translationY = 0.1f * resources.displayMetrics.heightPixels
                            emailLayout.animate()
                                .translationY(0f)
                                .alpha(1f)
                                .setDuration(300)
                            keyLayout.translationY = 0.1f * resources.displayMetrics.heightPixels
                            keyLayout.animate()
                                .translationY(0f)
                                .alpha(1f)
                                .setDuration(300)
                        } else {
                            println("Error: ${task.exception?.message}")

                        }
                    }
                emailInput.text.clear()
                passwordInput.text.clear()
                usernameInput.text.clear()

            }
            sginInButton.setOnClickListener {
                FirebaseAuth.getInstance().signInWithEmailAndPassword(
                    emailInput.text.trim().toString(),
                    passwordInput.text.trim().toString()
                )
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        println("User signed in successfully!")
                        val user = task.result?.user
                        val profileUpdates = UserProfileChangeRequest.Builder()
                            .setDisplayName(usernameInput.text.toString()) // Set the desired username
                            .build()
                        user?.updateProfile(profileUpdates)
                        layout.translationY = 0f
                        layout.animate()
                            .translationY(0.8f * resources.displayMetrics.heightPixels)
                            .alpha(0f)
                            .setDuration(800)
                    } else {
                        println("Error: ${task.exception?.message}")
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        enableEdgeToEdge()
        FirebaseApp.initializeApp(this)
        val analytics = FirebaseAnalytics.getInstance(this)
        val bundle = Bundle().apply {
            putString("Splash", "test")
        }
        analytics.logEvent("Splash", bundle)
        initialize()

        firebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
        val configSettings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(3600)
            .build()
        firebaseRemoteConfig.setConfigSettingsAsync(configSettings)
        checkForUpdates()
        // Kotlin

        var layoutParams = button.layoutParams
        layoutParams.height =
            (0.4 * resources.displayMetrics.widthPixels).toInt() // Set the width in pixels
        layoutParams.width =
            (0.4 * resources.displayMetrics.widthPixels).toInt() // Set the width in pixels
        button.layoutParams = layoutParams
        layoutParams = button2.layoutParams
        layoutParams.height =
            (0.4 * resources.displayMetrics.widthPixels).toInt() // Set the width in pixels
        layoutParams.width =
            (0.4 * resources.displayMetrics.widthPixels).toInt() // Set the width in pixels
        button2.layoutParams = layoutParams

        button2.setOnClickListener {
            val intent = Intent(this, ThirdActivity::class.java)
            startActivity(intent)
        }
        button.setOnClickListener {
            val intent = Intent(this, SecondActivity::class.java)
            startActivity(intent)
        }
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            signIn()
        }
        analytics.setUserProperty("displayName", user?.displayName)
    }
}