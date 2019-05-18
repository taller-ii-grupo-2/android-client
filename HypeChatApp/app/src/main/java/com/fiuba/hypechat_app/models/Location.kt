package com.fiuba.hypechat_app.models

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

import kotlinx.android.synthetic.main.activity_sign_up.*

private const val PERMISSION_REQUEST = 10

class Location: AppCompatActivity() {
    lateinit var locationManager: LocationManager
    private var hasGps = false
    private var hasNetwork = false
    private var locationGps: Location? = null
    private var locationNetwork: Location? = null
    private var permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)

    fun getLatitude():Double{
        return locationGps!!.latitude
    }

    fun getLongitud():Double{
        return locationGps!!.longitude
    }

     fun setLocation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (this.checkPermission(permissions)) {
                this.enableView()
            } else {
                this.requestPermissions(permissions, PERMISSION_REQUEST)
            }
        } else {
            enableView()
        }
    }

     fun disableView() {
        btnLocation.isEnabled = false
        btnLocation.alpha = 0.5F
    }

     fun enableView() {
        btnLocation.isEnabled = true
        btnLocation.alpha = 1F
        btnLocation.setOnClickListener { getLocation()
            disableView()}
        Toast.makeText(this, "Done", Toast.LENGTH_SHORT).show()
    }

     fun checkPermission(permissionArray: Array<String>): Boolean {
        var allSuccess = true
        for (i in permissionArray.indices) {
            if (checkCallingOrSelfPermission(permissionArray[i]) == PackageManager.PERMISSION_DENIED)
                allSuccess = false
        }
        return allSuccess
    }

    @SuppressLint("MissingPermission")
     fun getLocation() {
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        hasGps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        hasNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        if (hasGps || hasNetwork) {

            if (hasGps) {
                Log.d("CodeAndroidLocation", "hasGps")
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0F, object :
                    LocationListener {
                    override fun onLocationChanged(location: Location?) {
                        if (location != null) {
                            locationGps = location
                            /*  tv_result.setText("\nGPS ")
                              tv_result.setText("\nLatitude : " + locationGps!!.latitude)
                              tv_result.setText("\nLongitude : " + locationGps!!.longitude)*/
                            Log.d("CodeAndroidLocation", " GPS Latitude : " + locationGps!!.latitude)
                            Log.d("CodeAndroidLocation", " GPS Longitude : " + locationGps!!.longitude)
                        }
                    }

                    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {

                    }

                    override fun onProviderEnabled(provider: String?) {

                    }

                    override fun onProviderDisabled(provider: String?) {

                    }

                })

                val localGpsLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                if (localGpsLocation != null)
                    locationGps = localGpsLocation
            }
            if (hasNetwork) {
                Log.d("CodeAndroidLocation", "hasGps")
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 0F, object :
                    LocationListener {
                    override fun onLocationChanged(location: Location?) {
                        if (location != null) {
                            locationNetwork = location
                            /*        tv_result.setText("\nNetwork ")
                                    tv_result.setText("\nLatitude : " + locationNetwork!!.latitude)
                                    tv_result.setText("\nLongitude : " + locationNetwork!!.longitude)*/
                            Log.d("CodeAndroidLocation", " Network Latitude : " + locationNetwork!!.latitude)
                            Log.d("CodeAndroidLocation", " Network Longitude : " + locationNetwork!!.longitude)
                        }
                    }

                    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {

                    }

                    override fun onProviderEnabled(provider: String?) {

                    }

                    override fun onProviderDisabled(provider: String?) {

                    }

                })

                val localNetworkLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                if (localNetworkLocation != null)
                    locationNetwork = localNetworkLocation
            }

            if(locationGps!= null && locationNetwork!= null){
                if(locationGps!!.accuracy > locationNetwork!!.accuracy){
                    /*    tv_result.setText("\nNetwork ")
                        tv_result.setText("\nLatitude : " + locationNetwork!!.latitude)
                        tv_result.setText("\nLongitude : " + locationNetwork!!.longitude)*/
                    Log.d("CodeAndroidLocation", " Network Latitude : " + locationNetwork!!.latitude)
                    Log.d("CodeAndroidLocation", " Network Longitude : " + locationNetwork!!.longitude)
                }else{
                    /*tv_result.setText("\nGPS ")
                    tv_result.setText("\nLatitude : " + locationGps!!.latitude)
                    tv_result.setText("\nLongitude : " + locationGps!!.longitude)*/
                    Log.d("CodeAndroidLocation", " GPS Latitude : " + locationGps!!.latitude)
                    Log.d("CodeAndroidLocation", " GPS Longitude : " + locationGps!!.longitude)
                }
            }

        } else {
            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST) {
            var allSuccess = true
            for (i in permissions.indices) {
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    allSuccess = false
                    val requestAgain = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && shouldShowRequestPermissionRationale(permissions[i])
                    if (requestAgain) {
                        Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Go to settings and enable the permission", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            if (allSuccess)
                enableView()

        }
    }
}