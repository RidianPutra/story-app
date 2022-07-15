package com.ridianputra.storyapp.ui.maps

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.ridianputra.storyapp.R
import com.ridianputra.storyapp.data.Result
import com.ridianputra.storyapp.data.preferences.UserPreferences
import com.ridianputra.storyapp.data.preferences.UserViewModel
import com.ridianputra.storyapp.data.preferences.ViewModelFactory
import com.ridianputra.storyapp.databinding.ActivityMapsBinding
import com.ridianputra.storyapp.ui.detail.DetailActivity
import com.ridianputra.storyapp.ui.welcome.WelcomeActivity

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var userViewModel: UserViewModel
    private val factory: com.ridianputra.storyapp.ui.ViewModelFactory =
        com.ridianputra.storyapp.ui.ViewModelFactory.getInstance()
    private val mapViewModel: MapViewModel by viewModels {
        factory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()
        setupMapFragment()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.map_option, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.normal_type -> {
                mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
                true
            }
            R.id.satellite_type -> {
                mMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
                true
            }
            R.id.terrain_type -> {
                mMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
                true
            }
            R.id.hybrid_type -> {
                mMap.mapType = GoogleMap.MAP_TYPE_HYBRID
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        setupMapUi()
        setupMapStyle()
        getMyLocation()

        userViewModel.getUserSession().observe(this) {
            if (it.token.isNullOrBlank()) {
                val i = Intent(this, WelcomeActivity::class.java)
                i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(i)
                finish()
            } else {
                mapViewModel.getStoriesMap(it.token).observe(this) { result ->
                    if (result != null) {
                        when (result) {
                            is Result.Success -> {
                                val boundsBuilder = LatLngBounds.Builder()

                                result.data.forEach { item ->
                                    val loc = LatLng(item.lat, item.lon)
                                    mMap.addMarker(
                                        MarkerOptions()
                                            .position(loc)
                                            .title(item.name)
                                            .snippet(item.description)
                                    )
                                    boundsBuilder.include(loc)
                                    val bounds = boundsBuilder.build()
                                    mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 64))
                                }

                                mMap.setOnInfoWindowClickListener { marker ->
                                    result.data.forEach { item ->
                                        val loc = LatLng(item.lat, item.lon)
                                        if (marker.position == loc && marker.title == item.name && marker.snippet == item.description) {
                                            val i = Intent(
                                                this@MapsActivity,
                                                DetailActivity::class.java
                                            )
                                            i.putExtra(DetailActivity.EXTRA_NAME, item.name)
                                            i.putExtra(DetailActivity.EXTRA_PHOTO, item.photoUrl)
                                            i.putExtra(
                                                DetailActivity.EXTRA_DESCRIPTION,
                                                item.description
                                            )
                                            startActivity(i)
                                        }
                                    }
                                }
                            }
                            is Result.Error -> {
                                AlertDialog.Builder(this).apply {
                                    setTitle(getString(R.string.failed_title))
                                    setMessage(getString(R.string.load_map))
                                    create()
                                    show()
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setupViewModel() {
        userViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreferences.getInstance(dataStore))
        )[UserViewModel::class.java]
    }

    private fun setupMapFragment() {
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun setupMapUi() {
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true
    }

    private fun setupMapStyle() {
        try {
            val success =
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
            if (!success) {
                Log.e("MapActivity", "Style parsing failed.")
            }
        } catch (exception: Resources.NotFoundException) {
            Log.e("MapActivity", "Can't find style. Error: ", exception)
        }
    }

    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                getMyLocation()
            }
        }
}