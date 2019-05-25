package com.fiuba.hypechat_app.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import android.util.Log
import android.widget.Toast
import com.fiuba.hypechat_app.DefaultResponse
import com.fiuba.hypechat_app.R
import com.fiuba.hypechat_app.RetrofitClient
import com.fiuba.hypechat_app.models.Workgroup
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_workspace_creation.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class WorkspaceCreationActivity : AppCompatActivity() {

    var photoUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workspace_creation)
        setSupportActionBar(findViewById(R.id.toolbarProfile))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        btnSelectphoto.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }

        btnCreateWorkgroup.setOnClickListener {
            verifyTextGaps()
            uploadImageWorkgroupToFirebase()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun verifyTextGaps() {

        if (etName.text.toString().isEmpty()) {
            etName.error = "Plase enter your workspace name"
            etName.requestFocus()
            return
        }

        if (etDescription.text.toString().isEmpty()) {
            etDescription.error = "Plase enter your description"
            etDescription.requestFocus()
            return
        }

        if (etWelcome.text.toString().isEmpty()) {
            etWelcome.setText("Hello ! Wellcome to Hypechat")
            return
        }
    }

    private fun createNewWorkgroup(urlImage: String) {
        val name = etName.text.toString()
        val workgroup = Workgroup(name, etDescription.text.toString(), etWelcome.text.toString(), urlImage)
        val ref = FirebaseDatabase.getInstance().getReference("/workgroup/${name}")
        sendDataToSv(workgroup)
        ref.setValue(workgroup)
            .addOnSuccessListener {
                Log.d("WorkgroupCreationAct", "Workgroup added to database")
            }

        val intent = Intent(this, WorkspaceActivity::class.java)
        startActivity(intent)
    }

    private fun sendDataToSv(workgroup: Workgroup) {

        RetrofitClient.instance.createWorkgroup(workgroup)
            .enqueue(object : Callback<DefaultResponse> {
                override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                    Toast.makeText(baseContext, t.message, Toast.LENGTH_LONG).show()
                }

                override fun onResponse(call: Call<DefaultResponse>, response: Response<DefaultResponse>) {
                    if (response.isSuccessful) {
                        Toast.makeText(baseContext, "Successfully workgroup added", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(baseContext, "Failed to create group", Toast.LENGTH_SHORT).show()
                    }
                }
            })
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
                    createNewWorkgroup(url.toString())
                    Log.d("WorkspaceCreationAct", "Image added to firebase: ${url.toString()}")
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
            "https://firebasestorage.googleapis.com/v0/b/hypechatapp-ebdd6.appspot.com/o/images%2Ffiuba_logo.png?alt=media&token=ed9d116e-1b68-423b-87a3-06b6af21fb37"
        createNewWorkgroup(defaultImageUrl)
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            Log.d("WorkspaceCreationAct", "Enter if")
            photoUri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, photoUri)
            CircleImageView.setImageBitmap(bitmap)
            btnSelectphoto.background = null
            btnSelectphoto.text = null
        }
    }
}
