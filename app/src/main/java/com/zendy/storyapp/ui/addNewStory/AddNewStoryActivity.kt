package com.zendy.storyapp.ui.addNewStory

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.zendy.storyapp.R
import com.zendy.storyapp.databinding.ActivityAddNewStoryBinding
import com.zendy.storyapp.helper.reduceFileImage
import com.zendy.storyapp.helper.rotateBitmap
import com.zendy.storyapp.helper.uriToFile
import com.zendy.storyapp.preferences.UserPreferences
import com.zendy.storyapp.ui.ViewModelFactory
import com.zendy.storyapp.ui.camera.CameraActivity
import com.zendy.storyapp.ui.main.MainActivity
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "token")

class AddNewStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddNewStoryBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var addNewStoryViewModel: AddNewStoryViewModel

    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERA_X_RESULT) {
            val myFile = it.data?.getSerializableExtra("picture") as File
            val isBackCamera = it.data?.getBooleanExtra("isBackCamera", true) as Boolean

            val result = rotateBitmap(
                BitmapFactory.decodeFile(myFile.path),
                isBackCamera
            )
            addNewStoryViewModel.bitmapImage = result
            addNewStoryViewModel.uriImage = null
            result.compress(Bitmap.CompressFormat.JPEG, 100, FileOutputStream(myFile))
            addNewStoryViewModel.getFile = myFile
            binding.previewImageView.setImageBitmap(result)
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->

        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            val myFile = uriToFile(selectedImg, this@AddNewStoryActivity)
            addNewStoryViewModel.getFile = myFile
            binding.previewImageView.setImageURI(selectedImg)
            addNewStoryViewModel.uriImage = selectedImg
            addNewStoryViewModel.bitmapImage = null
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                    uploadImage()

                }
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                    uploadImage()

                }
                else -> {
                    // No location access granted.
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNewStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val pref = UserPreferences.getInstance(dataStore)
        addNewStoryViewModel =
            ViewModelProvider(this, ViewModelFactory(pref, this))[AddNewStoryViewModel::class.java]

        binding.btnCapture.setOnClickListener { startCameraX() }
        binding.btnGallery.setOnClickListener { startGallery() }
        binding.btnUpload.setOnClickListener { uploadImage() }


        if (addNewStoryViewModel.bitmapImage != null) {
            binding.previewImageView.setImageBitmap(addNewStoryViewModel.bitmapImage)
        } else if (addNewStoryViewModel.uriImage != null) {
            binding.previewImageView.setImageURI(addNewStoryViewModel.uriImage)
        }

        subscribe()
    }

    private fun subscribe() {
        addNewStoryViewModel.message.observe(this) {
            Toast.makeText(this@AddNewStoryActivity, it, Toast.LENGTH_SHORT).show()
        }
        addNewStoryViewModel.isError.observe(this) {
            if (!it) {
                intent = Intent(this@AddNewStoryActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
        addNewStoryViewModel.isLoading.observe(this) {
            showLoading(it)
        }
    }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.apply {
                btnGallery.isEnabled = false
                btnCapture.isEnabled = false
                progressBar.visibility = View.VISIBLE
                btnUpload.isEnabled = false
            }

        } else {
            binding.apply {
                progressBar.visibility = View.GONE
                btnUpload.isEnabled = true
                btnGallery.isEnabled = true
                btnCapture.isEnabled = true
            }
        }
    }

    private fun uploadImage() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
            checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    if (addNewStoryViewModel.getFile != null && binding.editTextDescription.text.isNotEmpty()) {
                        val file = reduceFileImage(addNewStoryViewModel.getFile as File)

                        val description = binding.editTextDescription.text.toString()
                            .toRequestBody("text/plain".toMediaType())
                        val lat = location.latitude.toString()
                            .toRequestBody("text/plain".toMediaType())
                        val lon = location.longitude.toString()
                            .toRequestBody("text/plain".toMediaType())

                        val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                        val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                            "photo",
                            file.name,
                            requestImageFile
                        )
                        addNewStoryViewModel.getToken().observe(this) {
                            if (it != null) {
                                addNewStoryViewModel.uploadStory(
                                    it,
                                    imageMultipart,
                                    description,
                                    lat,
                                    lon
                                )
                            }
                        }

                    } else if (addNewStoryViewModel.getFile == null) {
                        Toast.makeText(
                            this@AddNewStoryActivity,
                            getString(R.string.enter_image),
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            this@AddNewStoryActivity,
                            getString(R.string.enter_description),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        this@AddNewStoryActivity,
                        "Location is not found. Try Again",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun startCameraX() {
        val intent = Intent(this, CameraActivity::class.java)
        launcherIntentCameraX.launch(intent)
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, getString(R.string.choose_picture))
        launcherIntentGallery.launch(chooser)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this,
                    getString(R.string.permission_denied),
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        const val CAMERA_X_RESULT = 200
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }
}