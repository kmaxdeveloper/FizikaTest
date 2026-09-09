package uz.kmax.fizikatest.data.tools

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import java.io.File
import java.util.*

class SharedPref(var context: Context) {

    private var preferences: SharedPreferences

    private var editor: SharedPreferences.Editor

    init {
        preferences = context.getSharedPreferences("FIZIKA_TEST", MODE_PRIVATE)
        editor = preferences.edit()
    }

    fun getLanguage(): String {
        return preferences.getString("LANG", "uz") ?: "uz"
    }

    /**
     * Older installs did not apply the saved locale at startup. Their backed-up
     * LANG value may therefore be stale. Migrate that legacy value once to the
     * app's intended default; choices made after this version remain untouched.
     */
    fun getInitializedLanguage(): String {
        if (!preferences.getBoolean(LANGUAGE_MIGRATION_DONE, false)) {
            editor.putString("LANG", DEFAULT_LANGUAGE)
                .putBoolean(LANGUAGE_MIGRATION_DONE, true)
                .apply()
        }
        return getLanguage()
    }

    /**
     * A file in noBackupFilesDir survives normal app updates but is deleted on
     * uninstall and is never restored from Android Auto Backup. This lets a
     * reinstalled app show onboarding even if old preferences are restored.
     */
    fun resetOnboardingForNewInstall() {
        val installMarker = File(context.noBackupFilesDir, INSTALL_MARKER_FILE)
        if (installMarker.exists()) return

        installMarker.parentFile?.mkdirs()
        installMarker.createNewFile()
        editor.putString("LANG", DEFAULT_LANGUAGE)
            .putBoolean("LANG_APP", true)
            .putBoolean("WELCOME_APP", true)
            .putBoolean(LANGUAGE_MIGRATION_DONE, true)
            .apply()
    }

    /**
     * Returns a context configured with the saved application language.
     * This must be used before an Activity creates its views, otherwise Android
     * selects resources using the device language (for example, values-en).
     */
    fun applyLanguage(baseContext: Context, language: String = getLanguage() ?: "uz"): Context {
        val locale = Locale(language)
        Locale.setDefault(locale)

        val configuration = Configuration(baseContext.resources.configuration)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            configuration.setLocale(locale)
        } else {
            @Suppress("DEPRECATION")
            configuration.locale = locale
        }
        configuration.setLayoutDirection(locale)
        return baseContext.createConfigurationContext(configuration)
    }

    fun loadLocale(context: Context) {
        setLanguage(getLanguage()!!, context)
    }

    fun setLanguage(lang: String, context: Context) {
        editor.putString("LANG", lang)
        editor.apply()
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(lang))
    }

    private fun updateResources(context: Context, language: String): Context? {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val configuration = context.resources.configuration
        configuration.setLocale(locale)
        configuration.setLayoutDirection(locale)//bu joyni uchirib kor
        return context.createConfigurationContext(configuration)
    }


    private fun updateResourcesLegacy(context: Context, language: String): Context? {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val resources = context.resources
        val configuration = resources.configuration
        configuration.locale = locale
        configuration.setLayoutDirection(locale)
        resources.updateConfiguration(configuration, resources.displayMetrics)
        return context
    }

    fun setWelcomeStatus(resume : Boolean) {
        editor.putBoolean("WELCOME_APP",resume)
        editor.apply()
    }

    //available

    fun getWelcomeStatus() = preferences.getBoolean("WELCOME_APP",true)

    fun setLangStatus(lang : Boolean) {
        editor.putBoolean("LANG_APP",lang)
        editor.apply()
    }

    fun getLangStatus() = preferences.getBoolean("LANG_APP", true)

    fun setUpdateStatus(update : Boolean){
        editor.putBoolean("UPDATE_AVAILABLE",update)
        editor.apply()
    }

    fun getUpdateAvailable() = preferences.getBoolean("UPDATE_AVAILABLE",false)

    fun setTestType(type : Int){
        editor.putInt("TEST_TYPE",type)
        editor.apply()
    }

    fun getTestType() = preferences.getInt("TEST_TYPE",3)

    fun setChooseTestType(chooseStatus : Boolean){
        editor.putBoolean("CHOOSE_TEST_TYPE",chooseStatus)
        editor.apply()
    }

    fun getChooseTestType() = preferences.getBoolean("CHOOSE_TEST_TYPE",false)

    fun setThemeMode(mode: Int) {
        preferences.edit().putInt(THEME_MODE, mode).apply()
    }

    fun getThemeMode(): Int {
        return preferences.getInt(THEME_MODE, -1) // -1 is AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM equivalent for "default"
    }

    /** Umumiy int qiymatlarini saqlash va olish (o'yinlar va boshqa xususiyatlar uchun) */
    fun saveInt(key: String, value: Int) {
        preferences.edit().putInt(key, value).apply()
    }

    fun getInt(key: String, default: Int = 0): Int {
        return preferences.getInt(key, default)
    }

    fun saveString(key: String, value: String) {
        preferences.edit().putString(key, value).apply()
    }

    fun getString(key: String, default: String = ""): String {
        return preferences.getString(key, default) ?: default
    }

    companion object {
        private const val DEFAULT_LANGUAGE = "uz"
        private const val LANGUAGE_MIGRATION_DONE = "LANGUAGE_MIGRATION_DONE"
        private const val INSTALL_MARKER_FILE = "fizika_test_install_marker"
        private const val THEME_MODE = "theme_mode"
    }
}
