package com.yura.interstoryapp.ui.stories.maps

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.yura.interstoryapp.R
import com.yura.interstoryapp.data.Utils.dataStore
import com.yura.interstoryapp.data.local.prefs.UserPrefs
import com.yura.interstoryapp.data.remote.response.ListStoryItem
import com.yura.interstoryapp.databinding.ActivityMapsBinding
import com.yura.interstoryapp.ui.stories.StoriesActivity
import com.yura.interstoryapp.ui.stories.detail.DetailActivity
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
                        onMarkerClickHandler(mMap, locationData)
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

    private fun onMarkerClickHandler(mMap: GoogleMap, locationData: ArrayList<ListStoryItem?>) {
        mMap.setOnMarkerClickListener { marker ->
            val story = locationData.find { it?.id == marker.tag }
            story?.let {
                val latLon = it.lat?.let { lat -> it.lon?.let { lon -> LatLng(lat, lon) } }
                latLon?.let { loc -> CameraUpdateFactory.newLatLngZoom(loc, 8f) }
                    ?.let { latlon -> mMap.animateCamera(latlon) }
                startActivity(
                    Intent(this, DetailActivity::class.java)
                        .putExtra(DetailActivity.DATA, it)
                )
            }
            true
        }
    }

    private fun showData(
        mMap: GoogleMap,
        locationData: ArrayList<ListStoryItem?>
    ) {
        val boundsBuilder = LatLngBounds.Builder()
        for (story in locationData) {
            val lat: Double? = story?.lat
            val lon: Double? = story?.lon

            if (lat != null && lon != null) {
                val latLng = LatLng(lat, lon)
                Glide.with(this)
                    .asBitmap()
                    .load(story.photoUrl)
                    .dontTransform()
                    .centerInside()
                    .circleCrop()
                    .into(object : CustomTarget<Bitmap?>() {
                        override fun onResourceReady(
                            resource: Bitmap,
                            transition: Transition<in Bitmap?>?
                        ) {
                            val scale: Float =
                                this@MapsActivity.resources.displayMetrics.density
                            val pixels = (80 * scale + 0.5f).toInt()
                            val bitmap = Bitmap.createScaledBitmap(resource, pixels, pixels, true)

                            mMap.addMarker(
                                MarkerOptions().position(latLng).title(story.name)
                                    .snippet(story.description)
                                    .icon(BitmapDescriptorFactory.fromBitmap(bitmap))
                            )?.tag =
                                story.id
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {
                            mMap.addMarker(
                                MarkerOptions().position(latLng).title(story.name)
                                    .snippet(story.description)
                            )?.tag =
                                story.id
                        }
                    })
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