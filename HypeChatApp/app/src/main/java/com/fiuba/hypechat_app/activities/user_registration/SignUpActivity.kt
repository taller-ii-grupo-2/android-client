package com.fiuba.hypechat_app.activities.user_registration

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_sign_up.*
import android.provider.Settings
import androidx.annotation.RequiresApi
import com.fiuba.hypechat_app.*
import com.google.firebase.storage.FirebaseStorage
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

private const val PERMISSION_REQUEST = 10

class SignUpActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    lateinit var locationManager: LocationManager
    private var hasGps = false
    private var hasNetwork = false
    private var locationGps: Location? = null
    private var locationNetwork: Location? = null
    private var permissions =
        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
    private var geocoder: Geocoder? = null
    lateinit var service: ApiService
    var photoUri: Uri? = null

    private val FIREBASE_MIN_PASS_LENGTH = 6

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        mAuth = FirebaseAuth.getInstance()
        signupbtn.isEnabled = false

        signupbtn.setOnClickListener {
            if (signUpValidation()) {
                uploadImageWorkgroupToFirebase()
            }
        }

        btnLocation.setOnClickListener {
            setLocation()
            signupbtn.isEnabled = true
        }

        btnSelectProfilephoto.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
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
        Toast.makeText(this, "Location set", Toast.LENGTH_SHORT).show()
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

            if (locationGps != null && locationNetwork != null) {
                if (locationGps!!.accuracy > locationNetwork!!.accuracy) {
                    /*    tv_result.setText("\nNetwork ")
                        tv_result.setText("\nLatitude : " + locationNetwork!!.latitude)
                        tv_result.setText("\nLongitude : " + locationNetwork!!.longitude)*/
                    Log.d("CodeAndroidLocation", " Network Latitude : " + locationNetwork!!.latitude)
                    Log.d("CodeAndroidLocation", " Network Longitude : " + locationNetwork!!.longitude)
                } else {
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

        var geocoder = Geocoder(this, Locale.getDefault())
        var address = geocoder.getFromLocation(locationGps!!.latitude, locationGps!!.longitude, 1)
        tv_result.setText(address.get(0).getAddressLine(0))
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST) {
            var allSuccess = true
            for (i in permissions.indices) {
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    allSuccess = false
                    val requestAgain = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                            shouldShowRequestPermissionRationale(permissions[i])
                    if (requestAgain) {
                        Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(
                            this, "Go to settings and enable the permission",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
            if (allSuccess)
                enableView()
        }
    }

    private fun signUpValidation(): Boolean {
        if (usernamebox.text.toString().isEmpty()) {
            usernamebox.error = "Plase enter your username"
            usernamebox.requestFocus()
            return false
        }

        if (namebox.text.toString().isEmpty()) {
            namebox.error = "Plase enter your name"
            namebox.requestFocus()
            return false
        }

        if (surnamebox.text.toString().isEmpty()) {
            surnamebox.error = "Plase enter your surnamebox"
            surnamebox.requestFocus()
            return false
        }

        if (emailbox.text.toString().isEmpty()) {
            emailbox.error = "Plase enter your email"
            emailbox.requestFocus()
            return false
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(emailbox.text.toString()).matches()) {
            emailbox.error = "Plase enter a valid email"
            emailbox.requestFocus()
            return false
        }

        if (passwordbox.text.toString().isEmpty()) {
            passwordbox.error = "Plase enter your password"
            passwordbox.requestFocus()
            return false
        }

        if (passwordbox.text.toString().length <= FIREBASE_MIN_PASS_LENGTH) {
            passwordbox.error = "Password shoud be at least 6 characters long"
            passwordbox.requestFocus()
            return false
        }

        return true
    }

    private fun createUser(urlImageProfile: String) {
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        val username = usernamebox.text.toString()
        val name = namebox.text.toString()
        val surname = surnamebox.text.toString()
        val mail = emailbox.text.toString()
        val latitude = locationGps!!.latitude
        val longitude = locationGps!!.longitude
        val password = passwordbox.text.toString()

        val user = User(longitude, latitude, mail, urlImageProfile, surname, name, username)
        mAuth.createUserWithEmailAndPassword(mail, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    sendDataToSv(user)
                    startActivity(Intent(this, SignInActivity::class.java))
                } else {
                    Toast.makeText(baseContext, "Sign up failed", Toast.LENGTH_SHORT).show()
                }
            }
        ref.setValue(user)
            .addOnSuccessListener {
                Log.d("SignUpActivity", "User added to database")
            }
    }

    private fun sendDataToSv(user: User) {

        RetrofitClient.instance.createUser(user)
            .enqueue(object : Callback<DefaultResponse> {
                override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                    Toast.makeText(baseContext, t.message, Toast.LENGTH_LONG).show()
                }

                override fun onResponse(call: Call<DefaultResponse>, response: Response<DefaultResponse>) {
                    if (response.isSuccessful) {
                        Toast.makeText(baseContext, "User created", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(baseContext, "Failed to add item", Toast.LENGTH_SHORT).show()
                    }
                }
            })
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            Log.d("WorkspaceCreationAct", "Enter if")
            photoUri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, photoUri)
            CircleImageViewSignUp.setImageBitmap(bitmap)
            btnSelectProfilephoto.background = null
            btnSelectProfilephoto.text = null

        }
    }

    private fun uploadImageWorkgroupToFirebase() {
        if (photoUri == null) {
            loadDefaultImage()
            return
        }
        val filename = usernamebox.text.toString() + UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")
        val progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Creating user, just wait")
        progressDialog.show()
        ref.putFile(photoUri!!)
            .addOnSuccessListener { taskSnapshot ->
                ref.downloadUrl.addOnCompleteListener { taskSnapshot ->
                    var url = taskSnapshot.result
                    createUser(url.toString())
                    Log.d("ProfileAcitivity", "Image added to firebase: ${url.toString()}")
                }
            }
            .addOnProgressListener { taskSnapShot ->
                signupbtn.isEnabled = false
                val progress = 100 * taskSnapShot.bytesTransferred / taskSnapShot.totalByteCount
                progressDialog.setMessage("% ${progress}")
            }
    }

    private fun loadDefaultImage() {
        val defaultImageUrl =
            "https://firebasestorage.googleapis.com/v0/b/hypechatapp-ebdd6.appspot.com/o/images%2FTrama.jpg?alt=media&token=4d0375e4-5a04-4041-8f4c-b6b4738b9b48"
        createUser(defaultImageUrl)
    }
}