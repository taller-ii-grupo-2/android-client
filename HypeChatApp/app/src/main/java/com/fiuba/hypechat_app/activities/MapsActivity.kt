package com.fiuba.hypechat_app.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.fiuba.hypechat_app.DefaultResponse
import com.fiuba.hypechat_app.LocationUser
import com.fiuba.hypechat_app.R
import com.fiuba.hypechat_app.RetrofitClient
import com.fiuba.hypechat_app.models.Moi

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        updateMapFromSV()
    }

    private fun updateMapFromSV() {
        RetrofitClient.instance.getLocations(Moi.getCurrentOrganizationName())
            .enqueue(object : Callback<List<LocationUser>> {
                override fun onFailure(call: Call<List<LocationUser>>, t: Throwable) {

                }

                override fun onResponse(call: Call<List<LocationUser>>, response: Response<List<LocationUser>>) {
                    if (response.isSuccessful) {
                        val locations = response.body()
                        setOnMaps(mMap,locations!!)
                    } else {

                    }
                }
            })
    }

    private fun setOnMaps(mMap: GoogleMap, locations: List<LocationUser>) {
        locations.forEach {
            var pos = LatLng(it.longitude,it.latitude)
            mMap.addMarker(MarkerOptions().position(pos).title(it.username))
            mMap.moveCamera(CameraUpdateFactory.newLatLng(pos))
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(pos, 1f))
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val fiuba = LatLng(-34.617639, -58.368056)
        mMap.addMarker(MarkerOptions().position(fiuba).title("FIUBA"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(fiuba))
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(fiuba, 1f))

//        TODO check permision explicitly as suggested
        mMap.isMyLocationEnabled = true
    }
}
