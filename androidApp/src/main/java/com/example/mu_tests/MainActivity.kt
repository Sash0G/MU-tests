package com.example.mu_tests

import PartButton
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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import eightbitlab.com.blurview.BlurView
import eightbitlab.com.blurview.RenderScriptBlur
import java.util.ArrayList

class MainActivity : AppCompatActivity() {
    private lateinit var firebaseRemoteConfig: FirebaseRemoteConfig
    private lateinit var button: Button
    private lateinit var button2: Button
    private lateinit var sginInButton: Button
    private lateinit var signUpButton: Button
    private lateinit var userLayout: androidx.constraintlayout.widget.ConstraintLayout
    private lateinit var emailLayout: androidx.constraintlayout.widget.ConstraintLayout
    private lateinit var keyLayout: androidx.constraintlayout.widget.ConstraintLayout
    private lateinit var textOr: TextView
    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var usernameInput: EditText
    lateinit var firstBook: Button
    lateinit var secondBook: Button

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
    private fun chooseParts() {
        val recyclerView: RecyclerView = findViewById(R.id.scrollView)
        val buttonAdapter = PartButton(this,this, List(60) { it }) { isInSelectionMode ->
        }
        val gridLayoutManager = GridLayoutManager(this, 3)
        recyclerView.apply {
            layoutManager = gridLayoutManager
            adapter = buttonAdapter
        }
        var isClicked1 = false
        var isClicked2 = false
        firstBook = findViewById<Button>(R.id.firstBook)
        secondBook = findViewById<Button>(R.id.secondBook)
        firstBook.setOnClickListener{
            if(!isClicked1){
                firstBook.background = ContextCompat.getDrawable(this, R.drawable.rectangle_button_green)
                firstBook.setTextColor(ContextCompat.getColor(this, R.color.white))
                buttonAdapter.add(false)
                isClicked1 = true
            }
            else {
                firstBook.background = ContextCompat.getDrawable(this, R.drawable.rectangle_button)
                firstBook.setTextColor(ContextCompat.getColor(this, R.color.black))
                buttonAdapter.remove(false)
                isClicked1 = false
            }
        }
       secondBook.setOnClickListener{
//            if(!isClicked2){
//                secondBook.background = ContextCompat.getDrawable(this, R.drawable.rectangle_button_green)
//                secondBook.setTextColor(ContextCompat.getColor(this, R.color.white))
//                buttonAdapter.add(true)
//                isClicked2 = true
//            }
//            else {
//                secondBook.background = ContextCompat.getDrawable(this, R.drawable.rectangle_button)
//                secondBook.setTextColor(ContextCompat.getColor(this, R.color.black))
//                buttonAdapter.remove(true)
//                isClicked2 = false
//            }
        }
        findViewById<Button>(R.id.start).setOnClickListener {
            if(buttonAdapter.selectedItems.isNotEmpty()) {
                val parts = mutableListOf<String>()
                println(buttonAdapter.selectedItems)
                for (i in buttonAdapter.selectedItems) parts.add("Тема ${i + 1}")
                val intent = Intent(this, SecondActivity::class.java)
                intent.putStringArrayListExtra("parts", ArrayList(parts))
                ContextCompat.startActivity(this, intent, null)

            }
        }

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

        button.setOnClickListener {
            findViewById<ConstraintLayout>(R.id.constraintLayout).visibility = android.view.View.VISIBLE
            findViewById<BlurView>(R.id.blurView).visibility = android.view.View.VISIBLE
            chooseParts()
            button.visibility = android.view.View.INVISIBLE
            button2.visibility = android.view.View.INVISIBLE
        }
        button2.setOnClickListener {
            val intent = Intent(this, ThirdActivity::class.java)
            startActivity(intent)
        }

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

        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            signIn()
        }
        analytics.setUserProperty("displayName", user?.displayName)

        val blurView: BlurView = findViewById(R.id.blurView)
        val decorView = window.decorView

        blurView.setupWith(decorView.findViewById(android.R.id.content))
            .setFrameClearDrawable(window.decorView.background) // Use RenderScript for the blur
            .setBlurRadius(3f) // Adjust the blur radius
    }
}