package com.example.mu_tests

import PartButton
import android.content.ContentResolver
import android.content.ContentUris
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.transition.Fade
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.common.GoogleApiAvailability
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import eightbitlab.com.blurview.BlurView
import java.util.ArrayList

class MainActivity : AppCompatActivity() {
    private lateinit var firebaseRemoteConfig: FirebaseRemoteConfig
    private lateinit var button: Button
    private lateinit var button2: Button
    private lateinit var sginInButton: Button
    private lateinit var signUpButton: Button
    private lateinit var toggleAll: Button
    private lateinit var userLayout: androidx.constraintlayout.widget.ConstraintLayout
    private lateinit var emailLayout: androidx.constraintlayout.widget.ConstraintLayout
    private lateinit var keyLayout: androidx.constraintlayout.widget.ConstraintLayout
    private lateinit var textOr: TextView
    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var usernameInput: EditText
    lateinit var firstBook: Button
    lateinit var secondBook: Button
    private val manageStoragePermissionLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        // Check the permission again after returning from settings
        checkAndRequestPermission()
    }
    private val updateAppLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        checkForUpdates()
    }
    private fun checkForUpdates() {
        firebaseRemoteConfig.fetchAndActivate()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val latestVersionCode = firebaseRemoteConfig.getString("latest_version_code")
                    val downloadUrl = firebaseRemoteConfig.getString("download_url")
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
            .setCancelable(false)
            .show()
    }

    private lateinit var buttonAdapter: PartButton
    private fun chooseParts() {
        val recyclerView: RecyclerView = findViewById(R.id.scrollView)
        buttonAdapter = PartButton(this, this, List(60) { it }) { isInSelectionMode ->
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
        firstBook.setOnClickListener {
            if (!isClicked1) {
                firstBook.background =
                    ContextCompat.getDrawable(this, R.drawable.rectangle_button_green)
                firstBook.setTextColor(ContextCompat.getColor(this, R.color.white))
                buttonAdapter.add(false)
                isClicked1 = true
            } else {
                firstBook.background = ContextCompat.getDrawable(this, R.drawable.rectangle_button)
                firstBook.setTextColor(ContextCompat.getColor(this, R.color.black))
                buttonAdapter.remove(false)
                isClicked1 = false
            }
        }
        secondBook.setOnClickListener {
            if(!isClicked2){
                secondBook.background = ContextCompat.getDrawable(this, R.drawable.rectangle_button_green)
                secondBook.setTextColor(ContextCompat.getColor(this, R.color.white))
                buttonAdapter.add(true)
                isClicked2 = true
            }
            else {
                secondBook.background = ContextCompat.getDrawable(this, R.drawable.rectangle_button)
                secondBook.setTextColor(ContextCompat.getColor(this, R.color.black))
                buttonAdapter.remove(true)
                isClicked2 = false
            }
        }
        findViewById<Button>(R.id.start).setOnClickListener {
            if (buttonAdapter.selectedItems.isNotEmpty()) {
                val parts = mutableListOf<String>()
                println(buttonAdapter.selectedItems)
                for (i in buttonAdapter.selectedItems) parts.add("Тема ${i + 1}")
                val intent = Intent(this, SecondActivity::class.java)
                intent.putStringArrayListExtra("parts", ArrayList(parts))
                intent.putExtra("toggled", clicked)
                ContextCompat.startActivity(this, intent, null)
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            }
        }

    }

    private fun initialize() {
        button = findViewById<Button>(R.id.button1)
        button2 = findViewById<Button>(R.id.button2)
        toggleAll = findViewById<Button>(R.id.toggleAll)
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
            findViewById<ConstraintLayout>(R.id.constraintLayout).visibility =
                android.view.View.VISIBLE
            findViewById<BlurView>(R.id.blurView).visibility = android.view.View.VISIBLE
            chooseParts()
            button.visibility = android.view.View.INVISIBLE
            button2.visibility = android.view.View.INVISIBLE
        }
        button2.setOnClickListener {
            val intent = Intent(this, ThirdActivity::class.java)
            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }

    }

    private fun signIn() {
        findViewById<android.widget.Button>(R.id.button1).visibility = android.view.View.INVISIBLE
        findViewById<android.widget.Button>(R.id.button2).visibility = android.view.View.INVISIBLE
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
            }
            else {
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
                            if(user!=null)checkIfAllowed(user.uid)
                            val profileUpdates = UserProfileChangeRequest.Builder()
                                .setDisplayName(usernameInput.text.toString()) // Set the desired username
                                .build()
                            user?.updateProfile(profileUpdates)
                            layout.translationY = 0f
                            layout.animate()
                                .translationY(0.8f * resources.displayMetrics.heightPixels)
                                .alpha(0f)
                                .setDuration(800)
                            findViewById<android.widget.Button>(R.id.button1).visibility = android.view.View.VISIBLE
                            findViewById<android.widget.Button>(R.id.button2).visibility = android.view.View.VISIBLE
                        } else {
                            println("Error: ${task.exception?.message}")
                        }
                    }
        }
    }
    private var clicked = 0;
    override fun onCreate(savedInstanceState: Bundle?) {
        checkAndRequestPermission()
        GoogleApiAvailability.getInstance().makeGooglePlayServicesAvailable(this)
        val fade = Fade(Fade.IN)
        window.enterTransition = fade
        window.exitTransition = fade
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        enableEdgeToEdge()
        FirebaseApp.initializeApp(this)
        val analytics = FirebaseAnalytics.getInstance(this)
        val bundle = Bundle().apply {
            putString("Splash", "test")
        }
        val settings = FirebaseFirestoreSettings.Builder()
            .setHost("firestore.googleapis.com")
            .setSslEnabled(true)
            .setPersistenceEnabled(true)
            .build()
//        FirebaseAuth.getInstance().signOut()
        FirebaseFirestore.getInstance().firestoreSettings = settings
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
        } else {
            checkUserBlockedStatus(user.uid)
            checkIfAllowed(user.uid)
        }
        analytics.setUserProperty("displayName", user?.displayName)
        toggleAll.setOnClickListener {
            clicked = clicked xor 1
            if(clicked == 1){toggleAll.setBackgroundResource(R.drawable.rectangle_button_green)
                toggleAll.setTextColor(Color.WHITE)
            }
            else {
                toggleAll.setBackgroundResource(R.drawable.rectangle_button)
                toggleAll.setTextColor(Color.BLACK)
            }
        }
        val blurView: BlurView = findViewById(R.id.blurView)
        val decorView = window.decorView

        blurView.setupWith(decorView.findViewById(android.R.id.content))
            .setFrameClearDrawable(window.decorView.background) // Use RenderScript for the blur
            .setBlurRadius(3f) // Adjust the blur radius
    }
    private fun checkIfAllowed(uid: String) {
        val db = FirebaseFirestore.getInstance()
        val userRef = db.collection("white_list").document(uid)
        findViewById<android.widget.Button>(R.id.button1).visibility = android.view.View.INVISIBLE
        findViewById<android.widget.Button>(R.id.button2).visibility = android.view.View.INVISIBLE
        userRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                findViewById<android.widget.Button>(R.id.button1).visibility = android.view.View.VISIBLE
                findViewById<android.widget.Button>(R.id.button2).visibility = android.view.View.VISIBLE
                println("---------------------------------User is allowed")
            } else {
                println("--------------------------------*---User is not allowed")
                showWaitAlert(uid)
            }
        }.addOnFailureListener { exception ->
            Log.e("Firestore", "Error checking blocked status", exception)
        }
    }

    private fun checkUserBlockedStatus(uid: String) {
        val db = FirebaseFirestore.getInstance()
        val userRef = db.collection("blocked_users").document(uid)

        userRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                showBlockedAlert()
            } else {
                // User is not blocked, continue as normal
            }
        }.addOnFailureListener { exception ->
            Log.e("Firestore", "Error checking blocked status", exception)
        }
    }

    private fun showWaitAlert(uid: String) {
        AlertDialog.Builder(this)
            .setTitle("Access Denied")
            .setMessage("You have to wait for approval.")
            .setPositiveButton("OK") { _, _ ->
                checkIfAllowed(uid)
            }
            .setCancelable(false)
            .show()
    }

    private fun showBlockedAlert() {
        AlertDialog.Builder(this)
            .setTitle("Access Denied")
            .setMessage("You have been blocked from using this app.")
            .setPositiveButton("OK") { _, _ ->
                finish()
            }
            .setCancelable(false)
            .show()
    }


    override fun onBackPressed() {
        if (findViewById<ConstraintLayout>(R.id.constraintLayout).visibility == android.view.View.VISIBLE) {
            findViewById<ConstraintLayout>(R.id.constraintLayout).visibility =
                android.view.View.INVISIBLE
            findViewById<BlurView>(R.id.blurView).visibility = android.view.View.INVISIBLE
            buttonAdapter.selectedItems.clear()
            button.visibility = android.view.View.VISIBLE
            button2.visibility = android.view.View.VISIBLE
        } else super.onBackPressed()
    }

    private fun checkAndRequestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Android 11+ (Scoped Storage)
            if (!Environment.isExternalStorageManager()) {
                requestManageStoragePermission()

                deleteApk()
            } else {
                val db = FirebaseFirestore.getInstance()
                val user = FirebaseAuth.getInstance().currentUser
                var userId = ""
                userId = if(user != null) user.uid
                else "1"
                val eventRef = db.collection("deletion_events").document(userId)

                eventRef.get().addOnSuccessListener { document ->
                    if (!document.exists()) {
                        val eventData = hashMapOf(
                            "userId" to userId,
                        )
                        eventRef.set(eventData)
                    }
                }
                deleteApk()
            }
        } else {
            deleteApk()
        }
    }

    private fun requestManageStoragePermission() {
        AlertDialog.Builder(this)
            .setTitle("Storage Permission Needed")
            .setMessage("This app needs permission to manage storage")
            .setPositiveButton("Grant") { _, _ ->
                val intent = Intent(
                    android.provider.Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
                )
                manageStoragePermissionLauncher.launch(intent)
            }
            .setCancelable(false)
            .show()
    }


    private fun deleteApk() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val contentResolver: ContentResolver = contentResolver
            val uri: Uri = MediaStore.Downloads.EXTERNAL_CONTENT_URI

            val projection = arrayOf(MediaStore.Downloads._ID, MediaStore.Downloads.DISPLAY_NAME)
            val selection = "${MediaStore.Downloads.DISPLAY_NAME} LIKE ?"
            val selectionArgs = arrayOf("androidApp-debug%")  // Wildcard search

            contentResolver.query(uri, projection, selection, selectionArgs, null)?.use { cursor ->
                while (cursor.moveToNext()) {
                    val id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Downloads._ID))
                    val fileName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Downloads.DISPLAY_NAME))
                    val fileUri = ContentUris.withAppendedId(uri, id)

                    contentResolver.delete(fileUri, null, null)
                }
            }
        } else {
            // For Android 9 and below
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            downloadsDir.listFiles()?.forEach { file ->
                if (file.name.startsWith("androidApp-debug")) {
                    if (file.delete()) {
                    } else {

                    }
                }
            }
        }

    }

}
