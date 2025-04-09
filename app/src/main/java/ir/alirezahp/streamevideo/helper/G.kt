package ir.alirezahp.streamevideo.helper

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import java.util.Locale

class G : Application() {
    override fun onCreate() {
        super.onCreate()
        setPersianLocale()
    }

    private fun setPersianLocale() {

        //Set Default language to Persian
        val locale = Locale("fa")
        Locale.setDefault(locale)
        val config = resources.configuration
        config.setLocale(locale)
        config.setLayoutDirection(locale)

        createConfigurationContext(config)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
    }
}