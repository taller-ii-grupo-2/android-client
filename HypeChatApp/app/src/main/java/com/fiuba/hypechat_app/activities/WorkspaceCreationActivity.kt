package com.fiuba.hypechat_app.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import androidx.appcompat.view.menu.ActionMenuItemView
import android.util.Log
import android.widget.Toast
import androidx.annotation.NonNull
import com.fiuba.hypechat_app.R
import com.fiuba.hypechat_app.models.Workgroup
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FileDownloadTask
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_workspace_creation.*
import java.util.*

class WorkspaceCreationActivity : AppCompatActivity() {

    var photoUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workspace_creation)

        btnSelectphoto.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent,0)
        }

        btnCreateWorkgroup.setOnClickListener {
            verifyTextGaps()

        }
    }

    @SuppressLint("SetTextI18n")
    private fun verifyTextGaps() {

      /*  if (etName.text.toString().isEmpty()){
            etName.error = "Plase enter your workspace name"
            etName.requestFocus()
            return
        }
        if (etUbication.text.toString().isEmpty()){
            etUbication.error = "Plase enter your ubication"
            etUbication.requestFocus()
            return
        }
        if (etUserCreator.text.toString().isEmpty()){
            etUserCreator.error = "Plase enter your mail"
            etUserCreator.requestFocus()
            return
        }
        if (etDescription.text.toString().isEmpty()){
            etDescription.error = "Plase enter your description"
            etDescription.requestFocus()
            return
        }

        if (etWelcome.text.toString().isEmpty()){
            etWelcome.setText("Hello ! Wellcome to Hypechat")
            return
        }*/

        uploadImageWorkgroupToFirebase()


    }

    private fun createNewWorkgroup(urlImage:String) {
        val workgroup = Workgroup (etName.text.toString(),etUbication.text.toString(),etUserCreator.text.toString(),etDescription.text.toString(),etWelcome.text.toString(), urlImage)
        val ref = FirebaseDatabase.getInstance().getReference("/workgroup/${etName.text}")
        ref.setValue(workgroup)
            .addOnSuccessListener {
                Log.d("WorkgroupCreationAct", "Workgroup added to database")
            }

       val intent = Intent(this, WorkspaceActivity::class.java)
        startActivity(intent)
        //sendDataToSv(workgroup)
    }

    private fun uploadImageWorkgroupToFirebase(){
        var urlString:String? = null
        if (photoUri== null)
            loadDefaultImage()
        val filename = etName.text.toString() + UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")
        val progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Creating workgroup, just wait")
        progressDialog.show()
        var uploadTask = ref.putFile(photoUri!!)
            .addOnSuccessListener {

                Log.d("WorkspaceCreationAct", "Image added to firebase: ${it.metadata?.path}")
                createNewWorkgroup(it.uploadSessionUri.toString())
                Toast.makeText(applicationContext,"Workgroup created", Toast.LENGTH_SHORT).show()
                }
            .addOnProgressListener {taskSnapShot->
                btnCreateWorkgroup.isEnabled = false
                val progress = 100 * taskSnapShot.bytesTransferred/taskSnapShot.totalByteCount
                progressDialog.setMessage("% ${progress}")
            }

    }




        // en it se guarda la url para despues descargar la imagen




    private fun loadDefaultImage() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null){
            Log.d("WorkspaceCreationAct", "Enter if")
            photoUri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, photoUri)
            CircleImageView.setImageBitmap(bitmap)
            btnSelectphoto.background=null
            btnSelectphoto.text=null

        }
    }
}
