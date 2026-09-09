package uz.kmax.fizikatest.data.di.app

import android.app.ActivityManager
import android.app.Application
import android.os.Process
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.work.Configuration
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.HiltAndroidApp
import uz.kmax.fizikatest.data.manager.AppOpenAdManager
import uz.kmax.fizikatest.data.manager.NotificationWorker
import uz.kmax.fizikatest.data.tools.SharedPref
import java.util.Calendar
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class App : Application(), Configuration.Provider {

    @Inject
    lateinit var appOpenAdManager: AppOpenAdManager

//    override fun attachBaseContext(base: Context) {
//        val shared = SharedPref(base)
//        val lang = shared.getLanguage()
//        super.attachBaseContext(shared.applyLanguage(base, lang))
//    }

    override fun onCreate() {
        super.onCreate()
        val sharedPref = SharedPref(this)
        sharedPref.resetOnboardingForNewInstall()
        AppCompatDelegate.setApplicationLocales(
            LocaleListCompat.forLanguageTags(sharedPref.getInitializedLanguage())
        )
        if (isMainProcess()) {
            FirebaseApp.initializeApp(this)
            scheduleRetentionNotification()
            subscribeToTopics()
            appOpenAdManager.register(this)
        }
    }

    private fun isMainProcess(): Boolean {
        val am = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        val processes = am.runningAppProcesses
        if (processes != null) {
            val pid = Process.myPid()
            for (process in processes) {
                if (process.pid == pid) {
                    return process.processName == packageName
                }
            }
        }
        return false
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setMinimumLoggingLevel(Log.INFO)
            .build()

    private fun subscribeToTopics() {
        FirebaseMessaging.getInstance().subscribeToTopic("all")
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Muvaffaqiyatli obuna bo'lindi
                }
            }
    }

    private fun scheduleRetentionNotification() {
        val workRequest = PeriodicWorkRequest.Builder(NotificationWorker::class.java, 24, TimeUnit.HOURS)
            .setInitialDelay(calculateInitialDelay(), TimeUnit.MILLISECONDS)
            .addTag("retention_notification")
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "retention_notification",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }

    private fun calculateInitialDelay(): Long {
        val calendar = Calendar.getInstance()
        val now = calendar.timeInMillis

        calendar.set(Calendar.HOUR_OF_DAY, 18)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)

        if (calendar.timeInMillis <= now) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        return calendar.timeInMillis - now
    }
}
