package com.zendy.storyapp.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.zendy.storyapp.R
import com.zendy.storyapp.databinding.ActivityMainBinding
import com.zendy.storyapp.preferences.UserPreferences
import com.zendy.storyapp.ui.ViewModelFactory
import com.zendy.storyapp.ui.addNewStory.AddNewStoryActivity
import com.zendy.storyapp.ui.login.LoginActivity
import com.zendy.storyapp.ui.maps.MapsActivity

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "token")

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var mainViewModel: MainViewModel
    private var pressedTime = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val pref = UserPreferences.getInstance(dataStore)
        mainViewModel =
            ViewModelProvider(this, ViewModelFactory(pref,this))[MainViewModel::class.java]

        binding.rvStory.layoutManager = LinearLayoutManager(this)

        binding.fabAdd.setOnClickListener {
            startActivity(Intent(this@MainActivity, AddNewStoryActivity::class.java))
        }

        subscribe()
    }

    override fun onBackPressed() {
        if (pressedTime + 2000 > System.currentTimeMillis()) {
            super.onBackPressed()
            finishAffinity()
        } else {
            Toast.makeText(this@MainActivity, "Press back again to exit", Toast.LENGTH_SHORT)
                .show()
        }
        pressedTime = System.currentTimeMillis()
    }

    private fun getData(token : String) {
        val adapter = ListStoryAdapter()
        binding.rvStory.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                adapter.retry()
            }
        )
        mainViewModel.getListStories(token).observe(this) {
            adapter.submitData(lifecycle, it)
        }
    }

    private fun subscribe() {
        mainViewModel.getToken().observe(this) {
            if (it != "" && it != null) {
                getData(it)
            }
        }
    }

    private fun logOut() {
        mainViewModel.saveToken("")
        intent = Intent(this@MainActivity, LoginActivity::class.java)
        intent.clearStack()
        startActivity(intent)
        finish()
    }

    private fun Intent.clearStack(additionalFlags: Int = 0) {
        flags = additionalFlags or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.option_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu1 -> {
                logOut()
                true
            }
            R.id.menu2 -> {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
                true
            }
            R.id.menu3 -> {
                intent = Intent(this@MainActivity, MapsActivity::class.java)
                startActivity(intent)
                true
            }
            else -> true
        }
    }
}