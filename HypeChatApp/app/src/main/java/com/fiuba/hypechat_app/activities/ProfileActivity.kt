package com.fiuba.hypechat_app.activities

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.fiuba.hypechat_app.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.activity_workspace_creation.*
import java.util.*

class ProfileActivity : AppCompatActivity() {
    var photoUri: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        setSupportActionBar(findViewById(R.id.toolbarProfile))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        btnSelectphotoProfile.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }

        setProfileFields()

        btnChangePassword.setOnClickListener {
            val intent = Intent(this, ChangePasswordActivity::class.java)
            startActivity(intent)
        }
        btnConfirmProfile.setOnClickListener {
            uploadImageWorkgroupToFirebase()
        }


    }

    private fun uploadImageWorkgroupToFirebase() {
        if (photoUri == null) {
            loadDefaultImage()
            return
        }
        val filename = etName.text.toString() + UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")
        val progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Creating workgroup, just wait")
        progressDialog.show()
        ref.putFile(photoUri!!)
            .addOnSuccessListener { taskSnapshot ->

                ref.downloadUrl.addOnCompleteListener { taskSnapshot ->
                    var url = taskSnapshot.result
                    updateProfileDataToSv(url.toString())
                    Log.d("ProfileAcitivity", "Image added to firebase: ${url.toString()}")
                }

                Toast.makeText(applicationContext, "Workgroup created", Toast.LENGTH_SHORT).show()
            }
            .addOnProgressListener { taskSnapShot ->
                btnCreateWorkgroup.isEnabled = false
                val progress = 100 * taskSnapShot.bytesTransferred / taskSnapShot.totalByteCount
                progressDialog.setMessage("% ${progress}")
            }
    }

    private fun loadDefaultImage() {
        val defaultImageUrl =
            "https://firebasestorage.googleapis.com/v0/b/hypechatapp-ebdd6.appspot.com/o/images%2FTrama.jpg?alt=media&token=4d0375e4-5a04-4041-8f4c-b6b4738b9b48"
        updateProfileDataToSv(defaultImageUrl)
    }

    private fun setProfileFields() {
        var user = FirebaseAuth.getInstance().currentUser
        getProfileDataFromSv()

        //etMailProfile.setText(user?.email.toString())


    }

    private fun getProfileDataFromSv() {
        /*
        *  RetrofitClient.instance.getListUsers()
            .enqueue(object: Callback<List<User>> {
                override fun onFailure(call: Call<List<User>>, t: Throwable) {
                    Toast.makeText(baseContext, t.message, Toast.LENGTH_LONG).show()
                }

                override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
                    if (response.isSuccessful) {
                        Toast.makeText(baseContext, response.message(), Toast.LENGTH_SHORT).show()

                    } else {
                        Toast.makeText(baseContext, "Failed to add item", Toast.LENGTH_SHORT).show()
                    }
                }
            })*/
    }

    private fun updateProfileDataToSv(url: String) {

    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            Log.d("WorkspaceCreationAct", "Enter if")
            photoUri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, photoUri)
            CircleImageViewProfile.setImageBitmap(bitmap)
            btnSelectphotoProfile.background = null
            btnSelectphotoProfile.text = null

        }


    }
/*
* Armar interfaz basica de profile DONE
Cargar imagen por defecto si no suben una
Cargar localizacion gps
Añadir al profile el servicio de cambio de pw

*/

}
