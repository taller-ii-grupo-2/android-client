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
import com.fiuba.hypechat_app.*
import com.fiuba.hypechat_app.activities.user_registration.ChangePasswordActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.activity_workspace_creation.*
import kotlinx.android.synthetic.main.list_row.view.*
import kotlinx.android.synthetic.main.workgroup_row.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
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
        progressDialog.setTitle("Uploading changes, just wait")
        progressDialog.show()
        ref.putFile(photoUri!!)
            .addOnSuccessListener { taskSnapshot ->

                ref.downloadUrl.addOnCompleteListener { taskSnapshot ->
                    var url = taskSnapshot.result
                    updateProfileDataToSv(url.toString())
                    Log.d("ProfileAcitivity", "Image added to firebase: ${url.toString()}")
                }

                Toast.makeText(applicationContext, "Profile updated", Toast.LENGTH_SHORT).show()
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
        getProfileDataFromSv()

    }

    private fun getProfileDataFromSv() {
        RetrofitClient.instance.getUserProfile()
            .enqueue(object: Callback<UserProfile> {
                override fun onFailure(call: Call<UserProfile>, t: Throwable) {
                    Toast.makeText(baseContext, t.message, Toast.LENGTH_LONG).show()
                }

                override fun onResponse(call: Call<UserProfile>, response: Response<UserProfile>) {
                    if (response.isSuccessful) {
                        loadFields( response.body()!!)

                    } else {
                        Toast.makeText(baseContext, "Failed to load profile ", Toast.LENGTH_SHORT).show()
                    }
                }
            })
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    private fun loadFields(body: UserProfile) {
        val adapter = GroupAdapter<ViewHolder>()
        etNameProfile.setText(body.name)
        etSurnameProfile.setText(body.surname)
        etUsernameProfile.setText(body.username)
        Picasso.get().load(body.url).into(CircleImageViewProfile)
        btnSelectphotoProfile.background = null
        btnSelectphotoProfile.text = null

        body.organizations.forEach {
            adapter.add(ListItemWorkgroupAndChannel(it))
            rvOrganizationsProfile.adapter = adapter
        }





    }

    private fun updateProfileDataToSv(url: String) {
        val username = etUsernameProfile.text.toString()
        val name = etNameProfile.text.toString()
        val surename = etSurnameProfile.text.toString()
        val userProf = updateUserProfile(username,name,surename,url)
        RetrofitClient.instance.updateUserProfile(userProf)
            .enqueue(object: Callback<DefaultResponse> {
                override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                    Toast.makeText(baseContext, t.message, Toast.LENGTH_LONG).show()
                }

                override fun onResponse(call: Call<DefaultResponse>, response: Response<DefaultResponse>) {
                    if (response.isSuccessful) {
                        Toast.makeText(baseContext, "Profile updated", Toast.LENGTH_SHORT).show()
                        val intent = Intent(baseContext, ChatActivity::class.java)
                        startActivity(intent)

                    } else {
                        Toast.makeText(baseContext, "Failed to update profile ", Toast.LENGTH_SHORT).show()
                    }
                }
            })


    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            photoUri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, photoUri)
            CircleImageViewProfile.setImageBitmap(bitmap)
            btnSelectphotoProfile.background = null
            btnSelectphotoProfile.text = null

        }


    }

}

class ListItemWorkgroupAndChannel(var item: WorkgroupAndChannelList) : Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.txtWorkgroupList.text = item.name
        var text : String =""
        item.channels.forEach {
           text = text + it+"\n"
        }
        viewHolder.itemView.txtChannelList.text = text
    }

    override fun getLayout(): Int {
        return R.layout.list_row
    }

}
