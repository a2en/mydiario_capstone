package io.github.a2en.mydiario.ui.splash

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import io.github.a2en.mydiario.R
import io.github.a2en.mydiario.ui.home.MainActivity
import io.github.a2en.mydiario.ui.signup.welcome.WelcomeActivity
import io.github.a2en.mydiario.utils.Constants.Companion.IS_LOGGED_IN
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        val sharedPref = getSharedPreferences(
            getString(R.string.preference_file_key),
            Context.MODE_PRIVATE
        )

        val isLoggedIn = sharedPref.getBoolean(IS_LOGGED_IN,false)

        GlobalScope.launch {
            delay(2000)

            if (isLoggedIn)
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            else
                startActivity(Intent(this@SplashActivity, WelcomeActivity::class.java))

            finish()
        }
    }
}