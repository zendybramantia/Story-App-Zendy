package com.zendy.storyapp.ui.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.zendy.storyapp.R
import com.zendy.storyapp.databinding.ActivityLoginBinding
import com.zendy.storyapp.preferences.UserPreferences
import com.zendy.storyapp.ui.ViewModelFactory
import com.zendy.storyapp.ui.main.MainActivity
import com.zendy.storyapp.ui.register.RegisterActivity

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "token")

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private var pressedTime = 0L
    private lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val pref = UserPreferences.getInstance(dataStore)
        loginViewModel =
            ViewModelProvider(this, ViewModelFactory(pref, this))[LoginViewModel::class.java]

        binding.registerTextView.setOnClickListener { register() }

        binding.loginButton.setOnClickListener { login() }
        supportActionBar?.hide()
        subscribe()
        playAnimation()
    }

    private fun playAnimation() {
        val title = ObjectAnimator.ofFloat(binding.textView, View.ALPHA, 1f).setDuration(500)
        val email = ObjectAnimator.ofFloat(binding.emailEditText, View.ALPHA, 1f).setDuration(500)
        val password =
            ObjectAnimator.ofFloat(binding.passwordEditText, View.ALPHA, 1f).setDuration(500)
        val login = ObjectAnimator.ofFloat(binding.loginButton, View.ALPHA, 1f).setDuration(500)
        val register =
            ObjectAnimator.ofFloat(binding.registerTextView, View.ALPHA, 1f).setDuration(500)

        AnimatorSet().apply {
            playSequentially(title, email, password, login, register)
            start()
        }
    }

    override fun onBackPressed() {
        if (pressedTime + 2000 > System.currentTimeMillis()) {
            super.onBackPressed()
            finishAffinity()
        } else {
            Toast.makeText(this@LoginActivity, getString(R.string.press_back), Toast.LENGTH_SHORT)
                .show()
        }
        pressedTime = System.currentTimeMillis()
    }

    private fun register() {
        startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
    }

    private fun login() {
        val email = binding.emailEditText.text.toString()
        val password: String = binding.passwordEditText.text.toString()
        loginViewModel.login(email, password)
    }

    private fun subscribe() {
        loginViewModel.message.observe(this) {
            showMessage(it)
        }

        loginViewModel.isError.observe(this) {
            if (!it) {
                intent = Intent(this@LoginActivity, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            }
        }

        loginViewModel.isLoading.observe(this) {
            showLoading(it)
        }
    }

    private fun showMessage(message: String) {
        Toast.makeText(this@LoginActivity, message, Toast.LENGTH_SHORT).show()
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
            binding.loginButton.isEnabled = false
        } else {
            binding.progressBar.visibility = View.GONE
            binding.loginButton.isEnabled = true
        }
    }
}