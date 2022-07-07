package com.dicoding.todoapp.setting

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.NotificationCompat
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import androidx.work.*
import com.dicoding.todoapp.R
import com.dicoding.todoapp.notification.NotificationWorker
import com.dicoding.todoapp.utils.NOTIFICATION_CHANNEL_ID
import java.util.concurrent.TimeUnit

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


    }

    class SettingsFragment : PreferenceFragmentCompat() {
        private lateinit var workManager: WorkManager
        private lateinit var periodicWorkRequest: PeriodicWorkRequest
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)

            val prefNotification =
                findPreference<SwitchPreference>(getString(R.string.pref_key_notify))
            prefNotification?.setOnPreferenceChangeListener { _, newValue ->
                val channelName = getString(R.string.notify_channel_name)

                //TODO 13 : Schedule and cancel daily reminder using WorkManager with data channelName
                workManager = WorkManager.getInstance(requireContext())
                val constraints = Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
                val data = Data.Builder()
                    .putString(NOTIFICATION_CHANNEL_ID, channelName)
                    .build()
                periodicWorkRequest =
                    PeriodicWorkRequest.Builder(NotificationWorker::class.java, 24, TimeUnit.HOURS)
                        .setInputData(data)
                        .setConstraints(constraints)
                        .build()
                when (newValue) {
                    true -> {
                        workManager.enqueue(periodicWorkRequest)
                        workManager.getWorkInfoByIdLiveData(periodicWorkRequest.id)
                            .observe(this) { workInfo ->
                                val status = workInfo.state.name
                                Log.d("statusk", status)
                            }
                    }
                    false -> {
                        workManager.cancelWorkById(periodicWorkRequest.id)
                    }
                }
                true


            }
        }

        private fun updateTheme(mode: Int): Boolean {
            AppCompatDelegate.setDefaultNightMode(mode)
            requireActivity().recreate()
            return true
        }
    }
}