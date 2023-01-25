package com.yura.interstoryapp.ui.stories.add

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.yura.interstoryapp.R
import com.yura.interstoryapp.data.utils.Utils.bitmapToFile
import com.yura.interstoryapp.data.utils.Utils.dataStore
import com.yura.interstoryapp.data.utils.Utils.reduceFileImage
import com.yura.interstoryapp.data.utils.Utils.rotateBitmap
import com.yura.interstoryapp.data.utils.Utils.uriToFile
import com.yura.interstoryapp.data.local.prefs.UserPrefs
import com.yura.interstoryapp.databinding.ActivityAddStoryBinding
import com.yura.interstoryapp.ui.stories.add.camera.CameraActivity
import com.yura.interstoryapp.ui.viewmodel.VMFactory
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.util.*

class AddStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddStoryBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var lat: Double? = null
    private var lon: Double? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        getMyLastLocation()

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        binding.apply {
            btnCamera.setOnClickListener { startCamera() }
            btnGallery.setOnClickListener { startGallery() }
            btnSubmit.setOnClickListener {
                if (checkboxLocation.isChecked) {
                    uploadStory(true)
                } else {
                    uploadStory(false)
                }
            }
            btnBack.setOnClickListener { finish() }
        }
    }

    private fun uploadStory(withLocation: Boolean) {
        binding.apply {
            btnSubmit.isEnabled = false
            val context = this@AddStoryActivity
            if (getFile == null) {
                Toast.makeText(context, getString(R.string.add_image_first), Toast.LENGTH_SHORT)
                    .show()
                btnSubmit.isEnabled = true
            } else if (etDescription.text.isNullOrEmpty()) {
                Toast.makeText(context, getString(R.string.fill_description), Toast.LENGTH_SHORT)
                    .show()
                btnSubmit.isEnabled = true
            } else {
                val file = getFile
                val stringDescription = etDescription.text.toString()
                val latitude = lat.toString()
                val longitude = lon.toString()

                val reducedFile = file?.let { reduceFileImage(it) }

                val requestImageFile = reducedFile?.asRequestBody("image/jpeg".toMediaTypeOrNull())
                val imageMultipart: MultipartBody.Part? = requestImageFile?.let {
                    MultipartBody.Part.createFormData(
                        "photo",
                        reducedFile.name,
                        it
                    )
                }
                val pref = UserPrefs.getInstance(dataStore)
                val viewModel =
                    ViewModelProvider(
                        context,
                        VMFactory(pref, this@AddStoryActivity)
                    )[AddStoryViewModel::class.java]

                viewModel.getUserToken().observe(context) { token ->
                    imageMultipart?.let {
                        if (withLocation) {
                            viewModel.uploadStoryWithLocation(
                                token,
                                stringDescription,
                                it,
                                context,
                                latitude,
                                longitude
                            )
                                .observe(context) { state ->
                                    loadingState(state)
                                    if (state) {
                                        finish()
                                    }
                                }
                        } else {
                            viewModel.uploadStory(token, stringDescription, it, context)
                                .observe(context) { state ->
                                    loadingState(state)
                                    if (state) {
                                        finish()
                                    }
                                }
                        }
                    }
                }
            }
        }
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, getString(R.string.choose_picture))
        launcherIntentGallery.launch(chooser)
    }

    private fun startCamera() {
        val intent = Intent(this, CameraActivity::class.java)
        launcherIntentCameraX.launch(intent)
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
                    getString(R.string.not_permitted),
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) {
            getMyLastLocation()
        }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("MissingPermission")
    private fun getMyLastLocation() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) || checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    lat = location.latitude
                    lon = location.longitude
                } else {
                    binding.checkboxLocation.visibility = View.INVISIBLE
                    binding.tvWithoutLocation.visibility = View.VISIBLE
                    Toast.makeText(
                        this,
                        getString(R.string.location_info),
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

    private var getFile: File? = null

    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERA_X_RESULT) {
            val myFile = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                it.data?.getSerializableExtra("picture", File::class.java)
            } else {
                it.data?.getSerializableExtra("picture") as File
            }
            val isBackCamera = it.data?.getBooleanExtra("isBackCamera", true) as Boolean

            val result = rotateBitmap(
                BitmapFactory.decodeFile(myFile?.path),
                isBackCamera
            )

            val rotatedImage = bitmapToFile(result, this)
            val reducedFile = reduceFileImage(rotatedImage)

            getFile = reducedFile

            binding.imgPlaceholder.setImageBitmap(result)
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri

            val myFile = uriToFile(selectedImg, this@AddStoryActivity)
            val reducedFile = reduceFileImage(myFile)

            getFile = reducedFile

            binding.imgPlaceholder.setImageURI(selectedImg)
        }
    }

    private fun loadingState(state: Boolean) {
        if (state) {
            binding.apply {
                progressCircular.visibility = View.VISIBLE
                btnSubmit.isEnabled = false
            }
        } else {
            binding.apply {
                progressCircular.visibility = View.GONE
                btnSubmit.isEnabled = true
            }
        }
    }

    companion object {
        const val CAMERA_X_RESULT = 101
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }
}
