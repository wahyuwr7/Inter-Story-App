package com.yura.interstoryapp.ui.stories.maps

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.yura.interstoryapp.R
import com.yura.interstoryapp.data.Utils.dataStore
import com.yura.interstoryapp.data.local.prefs.UserPrefs
import com.yura.interstoryapp.data.remote.response.ListStoryItem
import com.yura.interstoryapp.databinding.ActivityMapsBinding
import com.yura.interstoryapp.ui.stories.StoriesActivity
import com.yura.interstoryapp.ui.viewmodel.VMFactory

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var viewModel: MapsViewModel
    private lateinit var locationData: ArrayList<ListStoryItem?>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backArrow.setOnClickListener {
            startActivity(Intent(this, StoriesActivity::class.java))
            finish()
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setPadding(0, 160, 0, 0)
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true

        val pref = UserPrefs.getInstance(dataStore)
        viewModel = ViewModelProvider(this, VMFactory(pref))[MapsViewModel::class.java]

        viewModel.getUserToken().observe(this) { token ->
            if (!token.isNullOrEmpty()) {
                viewModel.getMapStories(token, this).observe(this) { locData ->
                    if (!locData.isNullOrEmpty()) {
                        locationData = locData
                        showData(mMap, locationData)
                    }
                }
            }
        }

        mMap.setMapStyle(
            MapStyleOptions.loadRawResourceStyle(
                this, R.raw.night_map
            )
        )
    }

    private fun showData(mMap: GoogleMap, locationData: ArrayList<ListStoryItem?>) {
        val boundsBuilder = LatLngBounds.Builder()
        for (story in locationData) {
            val lat: Double? = story?.lat
            val lon: Double? = story?.lon

            if (lat != null && lon != null) {
                val latLng = LatLng(lat, lon)
                mMap.addMarker(MarkerOptions().position(latLng).title(story.name))?.tag =
                    story.id
                boundsBuilder.include(latLng)
            }

        }

        val bounds: LatLngBounds = boundsBuilder.build()
        mMap.animateCamera(
            CameraUpdateFactory.newLatLngBounds(
                bounds,
                resources.displayMetrics.widthPixels,
                resources.displayMetrics.heightPixels,
                300
            )
        )
    }
}