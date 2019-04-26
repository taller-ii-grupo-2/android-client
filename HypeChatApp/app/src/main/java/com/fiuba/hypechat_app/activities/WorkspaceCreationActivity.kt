package com.fiuba.hypechat_app.activities

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.support.annotation.RequiresApi
import android.support.v7.view.menu.ActionMenuItemView
import android.util.Log
import com.fiuba.hypechat_app.R
import kotlinx.android.synthetic.main.activity_workspace_creation.*

class WorkspaceCreationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workspace_creation)

        btnSelectphoto.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent,0)
        }
    }


    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null){
            Log.d("WorkspaceCreationAct", "Enter if")
            val uri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
            CircleImageView.setImageBitmap(bitmap)
            btnSelectphoto.background=null
            btnSelectphoto.text=null
            //CircleImageView.alpha = 1f
            //val bitmapDrawable = BitmapDrawable(bitmap)
            //btnSelectphoto.setBackgroundDrawable(bitmapDrawable)
        }
    }
}
