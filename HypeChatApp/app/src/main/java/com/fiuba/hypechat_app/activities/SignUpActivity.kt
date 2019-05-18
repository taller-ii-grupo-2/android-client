package com.fiuba.hypechat_app

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_sign_up.*
import android.provider.Settings
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
private const val PERMISSION_REQUEST = 10

class SignUpActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    lateinit var locationManager: LocationManager
    private var hasGps = false
    private var hasNetwork = false
    private var locationGps: Location? = null
    private var locationNetwork: Location? = null
    private var permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
    lateinit var service: ApiService


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        mAuth = FirebaseAuth.getInstance()


        signupbtn.setOnClickListener {
            signUpValidation()

        }

        btnLocation.setOnClickListener {
            setLocation()
        }
    }

    private fun setLocation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkPermission(permissions)) {
                enableView()
            } else {
                requestPermissions(permissions, PERMISSION_REQUEST)
            }
        } else {
            enableView()
        }
    }

    private fun disableView() {
        btnLocation.isEnabled = false
        btnLocation.alpha = 0.5F
    }

    private fun enableView() {
        btnLocation.isEnabled = true
        btnLocation.alpha = 1F
        getLocation()
        disableView()
        Toast.makeText(this, "Location setted", Toast.LENGTH_SHORT).show()
    }

    private fun checkPermission(permissionArray: Array<String>): Boolean {
        var allSuccess = true
        for (i in permissionArray.indices) {
            if (checkCallingOrSelfPermission(permissionArray[i]) == PackageManager.PERMISSION_DENIED)
                allSuccess = false
        }
        return allSuccess
    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        hasGps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        hasNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        if (hasGps || hasNetwork) {

            if (hasGps) {
                Log.d("CodeAndroidLocation", "hasGps")


                val localGpsLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                if (localGpsLocation != null)
                    locationGps = localGpsLocation
            }
            if (hasNetwork) {
                Log.d("CodeAndroidLocation", "hasGps")


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

    private fun signUpValidation() {
        if (usernamebox.text.toString().isEmpty()){
            usernamebox.error = "Plase enter your username"
            usernamebox.requestFocus()
            return
        }
        if (emailbox.text.toString().isEmpty()){
            emailbox.error = "Plase enter your email"
            emailbox.requestFocus()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(emailbox.text.toString()).matches()){
            emailbox.error = "Plase enter a valid email"
            emailbox.requestFocus()
            return
        }

        if (passwordbox.text.toString().isEmpty()){
            passwordbox.error = "Plase enter your password"
            passwordbox.requestFocus()
            return
        }

        mAuth.createUserWithEmailAndPassword(emailbox.text.toString(), passwordbox.text.toString() )
            .addOnCompleteListener(this) { task->
                if (task.isSuccessful) {
                    //saveUserToFirebaseDB()
                    startActivity(Intent(this,SignInActivity::class.java))
                    saveUserToFirebaseDB()
                } else {
                    Toast.makeText(baseContext, "Sign up failed", Toast.LENGTH_SHORT).show()
                }
            }
    }



    private fun saveUserToFirebaseDB (){
        val uid = FirebaseAuth.getInstance().uid ?:""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        val user = User( emailbox.text.toString(),usernamebox.text.toString(), locationGps!!.latitude, locationGps!!.longitude)
        ref.setValue(user)
            .addOnSuccessListener {
                Log.d("SignUpActivity", "User added to database")

            }


        sendDataToSv(user)


    }

    private fun sendDataToSv(user:User) {

        RetrofitClient.instance.createUser(user)
            .enqueue(object: Callback<DefaultResponse>{
                override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                    Toast.makeText(baseContext, t.message, Toast.LENGTH_LONG).show()
                }

                override fun onResponse(call: Call<DefaultResponse>, response: Response<DefaultResponse>) {
                    if (response.isSuccessful) {
                        Toast.makeText(baseContext, "Successfully Added", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(baseContext, "Failed to add item", Toast.LENGTH_SHORT).show()
                    }
                }
            })
    }



}

