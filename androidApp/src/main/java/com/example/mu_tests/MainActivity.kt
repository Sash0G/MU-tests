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
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings

class MainActivity : AppCompatActivity() {
    lateinit var firebaseRemoteConfig: FirebaseRemoteConfig
    private fun checkForUpdates(context: Context) {
        firebaseRemoteConfig.fetchAndActivate()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Fetch and activate succeeded
                    val latestVersionCode = firebaseRemoteConfig.getLong("latest_version_code").toInt()
                    val downloadUrl = firebaseRemoteConfig.getString("download_url")

                    // Check if an update is needed
                    if (latestVersionCode > getCurrentVersionCode()) {
                        showUpdateDialog(downloadUrl,context)
                    }
                }
            }
    }
    private fun getCurrentVersionCode(): Int {
        return try {
            val pInfo: PackageInfo = packageManager.getPackageInfo(packageName, 0)
            pInfo.versionCode
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            -1
        }
    }

    private fun showUpdateDialog(downloadUrl: String, context: Context) {
        AlertDialog.Builder(context)
            .setTitle("Update Available")
            .setMessage("A new version of the app is available. Please update to the latest version.")
            .setPositiveButton("Update") { _, _ ->
                // Open the URL in a browser
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(downloadUrl))
                ContextCompat.startActivity(context, intent, null)
            }
            .setNegativeButton("Later", null)
            .show()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        firebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
        val configSettings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(3600)  // Adjust this for your needs
            .build()
        firebaseRemoteConfig.setConfigSettingsAsync(configSettings)
        setContentView(R.layout.activity_main)

        val button = findViewById<Button>(R.id.button1)
        val button2 = findViewById<Button>(R.id.button2)
        var layoutParams = button.layoutParams
        layoutParams.height =(0.4 * resources.displayMetrics.widthPixels).toInt() // Set the width in pixels
        layoutParams.width =(0.4 * resources.displayMetrics.widthPixels).toInt() // Set the width in pixels
        button.layoutParams = layoutParams
        layoutParams = button2.layoutParams
        layoutParams.height =(0.4 * resources.displayMetrics.widthPixels).toInt() // Set the width in pixels
        layoutParams.width =(0.4 * resources.displayMetrics.widthPixels).toInt() // Set the width in pixels
        button2.layoutParams = layoutParams

        button2.setOnClickListener {
            val intent = Intent(this, ThirdActivity::class.java)
            startActivity(intent)
        }
        button.setOnClickListener {
            val intent = Intent(this, SecondActivity::class.java)
            startActivity(intent)
        }



        }
    }