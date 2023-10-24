package com.zendy.storyapp.ui.splashScreen

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.zendy.storyapp.databinding.ActivitySplashScreenBinding
import com.zendy.storyapp.preferences.UserPreferences
import com.zendy.storyapp.ui.ViewModelFactory
import com.zendy.storyapp.ui.login.LoginActivity
import com.zendy.storyapp.ui.main.MainActivity

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "token")

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashScreenBinding
    private lateinit var splashScreenViewModel: SplashScreenViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val pref = UserPreferences.getInstance(dataStore)
        splashScreenViewModel =
            ViewModelProvider(this, ViewModelFactory(pref, this))[SplashScreenViewModel::class.java]

        var nextActivity = Intent(this@SplashScreenActivity, LoginActivity::class.java)
        splashScreenViewModel.getToken().observe(this) {
            if (it != "" && it != null) {
                nextActivity = Intent(this@SplashScreenActivity, MainActivity::class.java)
            }
        }

        supportActionBar?.hide()
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(nextActivity)
            this.overridePendingTransition(0, 0)
            finish()
        }, 1000)
    }
}