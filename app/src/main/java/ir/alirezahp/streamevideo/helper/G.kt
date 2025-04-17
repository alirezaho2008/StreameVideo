package ir.alirezahp.streamevideo.helper

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import dagger.hilt.android.HiltAndroidApp
import java.util.Locale

@HiltAndroidApp
class G : Application() {
    override fun onCreate() {
        super.onCreate()
        changeLanguageToFarsi(this)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        context = this
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(changeLanguageToFarsi(base))
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        changeLanguageToFarsi(this)
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        var context: Context? = null

        fun changeLanguageToFarsi(context: Context): Context {
            val locale = Locale("fa")
            Locale.setDefault(locale)

            val config = Configuration(context.resources.configuration)
            config.setLocale(locale)
            config.setLayoutDirection(locale)

            return context.createConfigurationContext(config)
        }
    }
}