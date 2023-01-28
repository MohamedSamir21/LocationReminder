package com.udacity.project4.locationreminders.savereminder.selectreminderlocation


import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.udacity.project4.R
import com.udacity.project4.base.BaseFragment
import com.udacity.project4.base.NavigationCommand
import com.udacity.project4.databinding.FragmentSelectLocationBinding
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import com.udacity.project4.utils.setDisplayHomeAsUpEnabled
import org.koin.android.ext.android.inject
import java.util.*

class SelectLocationFragment : BaseFragment(),  OnMapReadyCallback{

    // source: https://stackoverflow.com/questions/45958226/get-location-android-kotlin
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationManager: LocationManager
    private lateinit var map: GoogleMap
    private val REQUEST_LOCATION_PERMISSION = 1
    private val REQUEST_GPS_ENABLING = 2
    //Use Koin to get the view model of the SaveReminder
    override val _viewModel: SaveReminderViewModel by inject()
    private var marker: Marker? = null
    private lateinit var binding: FragmentSelectLocationBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_select_location, container, false)

        binding.viewModel = _viewModel
        binding.lifecycleOwner = this
        setHasOptionsMenu(true)
        setDisplayHomeAsUpEnabled(true)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context!!)

        // map setup
        val mapFragment = childFragmentManager
            .findFragmentById(R.id.fragment_map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        binding.saveButton.setOnClickListener {
            onLocationSelected()
        }

        return binding.root
    }

    private fun onLocationSelected() {
        marker?.let {marker ->
            _viewModel.latitude.value = marker.position.latitude
            _viewModel.longitude.value = marker.position.longitude
            _viewModel.reminderSelectedLocationStr.value = marker.title
            _viewModel.navigationCommand.value = NavigationCommand.Back
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.map_options, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        // Change the map type based on the user's selection.
        R.id.normal_map -> {
            map.mapType = GoogleMap.MAP_TYPE_NORMAL
            true
        }
        R.id.hybrid_map -> {
            map.mapType = GoogleMap.MAP_TYPE_HYBRID
            true
        }
        R.id.satellite_map -> {
            map.mapType = GoogleMap.MAP_TYPE_SATELLITE
            true
        }
        R.id.terrain_map -> {
            map.mapType = GoogleMap.MAP_TYPE_TERRAIN
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        enableMyLocation()
        getCurrentLocation()
        setMapLongClick(map)
        setPoiClick(map)
        setMapStyle(map)
    }

    //To listen to the user touch and add marker to it.
    private fun setMapLongClick(map:GoogleMap) {
        map.setOnMapLongClickListener { latLng ->
            // A Snippet is Additional text that's displayed below the title.
            val snippet = String.format(
                Locale.getDefault(),
                "Lat: %1$.5f, Long: %2$.5f",
                latLng.latitude,
                latLng.longitude
            )
             marker = map.addMarker(
                        MarkerOptions()
                            .position(latLng)
                            .title(getString(R.string.dropped_pin))
                            .snippet(snippet)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                    )
            marker?.showInfoWindow()
        }
    }

    // This method enables the user to select a point of interest(featured place) on the map.
    private fun setPoiClick(map: GoogleMap) {
        map.setOnPoiClickListener { poi ->
            marker = map.addMarker(
                MarkerOptions()
                    .position(poi.latLng)
                    .title(poi.name)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
            )
            marker?.showInfoWindow()
        }
    }

    // This method to style the map with specified json style file.
    private fun setMapStyle(map: GoogleMap) {
            // Customize the styling of the base map using a JSON object defined
            // in a raw resource file.
            map.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    context,
                    R.raw.map_style
                )
            )
    }
    // This method to get the current location.
    private fun getCurrentLocation() {
        try {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location->
                    if (location != null) {
                        // Get latitude and longitude. Assign them to homeLatLng
                        val userLatLng = LatLng(location.latitude, location.longitude)
                        // Determine the level of zoom to the map
                        val zoomLevel = 16f
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, zoomLevel))
                        // Enable the user to put marker on the place he prefers and store in marker.
                        marker = map.addMarker(MarkerOptions().position(userLatLng))
                    }

                }
        } catch (ex: SecurityException){
            Log.i("SelectLocationFragment", ex.message!!)
        }


    }

    // This method to check if the location permission has been granted or not.
    private fun isPermissionGranted() : Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION) === PackageManager.PERMISSION_GRANTED
    }

    // This method to enable location tracking.
    private fun enableMyLocation() {
            if (!checkGPS()){
                // Then, GPS need to be enabled.
                popUpAlertDialog()
            }else if(!isPermissionGranted()){
                // In case of not granting the location permission, Request it.
                requestPermissions(
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_LOCATION_PERMISSION
                )
            }else if (isPermissionGranted() && checkGPS()){
                try {
                    // Enable the my-location layer.
                    // It continuously draws an indication of a user's current location and bearing.
                    map.isMyLocationEnabled = true
                }catch (ex: SecurityException){
                    Log.i("SelectLocationFragment", ex.message!!)
                }
            }
        }

    // This method to check the GPS status.
    private fun checkGPS(): Boolean{
        locationManager = context!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
    }

    // This method to pop up an alert dialog asking the user to enable GPS.
    private fun popUpAlertDialog(){
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Enable GPS")
        builder.setMessage("To continue, Turn on GPS so you can use the service")
        builder.setPositiveButton("OK") { dialog, which ->
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivityForResult(intent, REQUEST_GPS_ENABLING)
        }.setNegativeButton("No thanks"){ dialog, which ->
            // Do nothing
        }
        val dialog = builder.create()
        dialog.show()
    }

    // This callback method checks if the permission is granted. If yes, call enableMyLocation().
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray) {
        // Check if the requestCode is equal to REQUEST_LOCATION_PERMISSION. If it is, that means that the permission is granted.
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            // Check if the grantResults array contains PackageManager.PERMISSION_GRANTED. If that is true, call enableMyLocation() method.
            if (grantResults.contains(PackageManager.PERMISSION_GRANTED)) {
                enableMyLocation()
            }
            else{// This means that the user doesn't give the Location permission.
                Toast.makeText(context, "Location permission hasn't been granted", Toast.LENGTH_LONG).show()
            }
        }
    }

    // This callback checks if the user enables GPS.If yes, call enableMyLocation() method.
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_GPS_ENABLING){
            enableMyLocation()
        }
    }
}
