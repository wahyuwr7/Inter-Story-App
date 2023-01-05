package com.yura.interstoryapp.ui.stories.add

import android.Manifest
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.yura.interstoryapp.R
import com.yura.interstoryapp.data.Utils.bitmapToFile
import com.yura.interstoryapp.data.Utils.dataStore
import com.yura.interstoryapp.data.Utils.reduceFileImage
import com.yura.interstoryapp.data.Utils.rotateBitmap
import com.yura.interstoryapp.data.Utils.uriToFile
import com.yura.interstoryapp.data.local.prefs.UserPrefs
import com.yura.interstoryapp.databinding.ActivityAddStoryBinding
import com.yura.interstoryapp.ui.stories.add.camera.CameraActivity
import com.yura.interstoryapp.ui.viewmodel.VMFactory
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class AddStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddStoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
            btnSubmit.setOnClickListener { uploadStory() }
            btnBack.setOnClickListener { finish() }
        }
    }

    private fun uploadStory() {
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

                val requestImageFile = file?.asRequestBody("image/jpeg".toMediaTypeOrNull())
                val imageMultipart: MultipartBody.Part? = requestImageFile?.let {
                    MultipartBody.Part.createFormData(
                        "photo",
                        file.name,
                        it
                    )
                }

                val pref = UserPrefs.getInstance(dataStore)
                val viewModel =
                    ViewModelProvider(context, VMFactory(pref))[AddStoryViewModel::class.java]

                viewModel.getUserToken().observe(context) { token ->
                    imageMultipart?.let {
                        viewModel.uploadStory(token, stringDescription, it, context)
                            .observe(context) { state ->
                                btnSubmit.isEnabled = !state
                                if (state) {
                                    finish()
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

    private var getFile: File? = null
    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERA_X_RESULT) {
            val myFile = it.data?.getSerializableExtra("picture") as File
            val isBackCamera = it.data?.getBooleanExtra("isBackCamera", true) as Boolean

            val reducedFile = reduceFileImage(myFile)

            val result = rotateBitmap(
                BitmapFactory.decodeFile(reducedFile.path),
                isBackCamera
            )
            getFile = bitmapToFile(result)

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

    companion object {
        const val CAMERA_X_RESULT = 101

        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }
}
