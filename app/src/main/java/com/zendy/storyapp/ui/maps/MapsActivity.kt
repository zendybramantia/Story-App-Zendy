package com.zendy.storyapp.ui.maps

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.zendy.storyapp.R
import com.zendy.storyapp.databinding.ActivityMapsBinding
import com.zendy.storyapp.preferences.UserPreferences
import com.zendy.storyapp.ui.ViewModelFactory

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "token")

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var mapsViewModel: MapsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val pref = UserPreferences.getInstance(dataStore)
        mapsViewModel =
            ViewModelProvider(this, ViewModelFactory(pref, this))[MapsViewModel::class.java]
        subscribe()

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mapsViewModel.listStories.observe(this){
            for (story in it){
                val mark = LatLng(story.lat, story.lon)
                mMap.addMarker(MarkerOptions().position(mark).title(story.name))

            }
            mMap.moveCamera(CameraUpdateFactory.newLatLng(LatLng(it[0].lat, it[0].lon)))
        }

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true
    }

    private fun subscribe(){
        mapsViewModel.message.observe(this){
            showMessage(it)
        }

        mapsViewModel.getToken().observe(this) {
            if (it != "" && it != null) {
                mapsViewModel.getAllStories(it)
            }
        }
    }

    private fun showMessage(message: String) {
        Toast.makeText(this@MapsActivity, message, Toast.LENGTH_SHORT).show()
    }
}