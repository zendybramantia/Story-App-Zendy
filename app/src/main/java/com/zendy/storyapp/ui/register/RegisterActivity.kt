package com.zendy.storyapp.ui.register

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.zendy.storyapp.databinding.ActivityRegisterBinding
import com.zendy.storyapp.ui.login.LoginActivity

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val registerViewModel: RegisterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()
        binding.registerButton.setOnClickListener { register() }
        binding.loginTextView.setOnClickListener { login() }

        playAnimation()
        subscribe()
    }

    private fun playAnimation() {
        val title = ObjectAnimator.ofFloat(binding.textView, View.ALPHA, 1f).setDuration(500)
        val name = ObjectAnimator.ofFloat(binding.nameEditText, View.ALPHA, 1f).setDuration(500)
        val email = ObjectAnimator.ofFloat(binding.emailEditText, View.ALPHA, 1f).setDuration(500)
        val password =
            ObjectAnimator.ofFloat(binding.passwordEditText, View.ALPHA, 1f).setDuration(500)
        val login = ObjectAnimator.ofFloat(binding.registerButton, View.ALPHA, 1f).setDuration(500)
        val register =
            ObjectAnimator.ofFloat(binding.loginTextView, View.ALPHA, 1f).setDuration(500)

        AnimatorSet().apply {
            playSequentially(title, name, email, password, login, register)
            start()
        }
    }

    private fun login() {
        startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
        intent.clearStack()
        finish()
    }

    private fun Intent.clearStack(additionalFlags: Int = 0) {
        flags = additionalFlags or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }

    private fun register() {
        val name = binding.nameEditText.text.toString()
        val email = binding.emailEditText.text.toString()
        val password: String = binding.passwordEditText.text.toString()

        registerViewModel.register(email, name, password)
    }

    private fun subscribe() {
        registerViewModel.message.observe(this) {
            showMessage(it)
        }

        registerViewModel.isError.observe(this) {
            if (!it) {
                intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            }
        }

        registerViewModel.isLoading.observe(this) {
            showLoading(it)
        }
    }

    private fun showMessage(message: String) {
        Toast.makeText(this@RegisterActivity, message, Toast.LENGTH_SHORT).show()
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
            binding.registerButton.isEnabled = false
        } else {
            binding.progressBar.visibility = View.GONE
            binding.registerButton.isEnabled = true
        }
    }
}