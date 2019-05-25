package com.fiuba.hypechat_app.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.fiuba.hypechat_app.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_change_password.*

class ChangePasswordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)
        setSupportActionBar(findViewById(R.id.toolbarProfile))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        btnConfirmNewPassword.setOnClickListener {
            changePassword()
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }
    }

    private fun changePassword() {
        FirebaseAuth.getInstance().currentUser!!.updatePassword(etNewPassword.text.toString())
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(baseContext, "Successfully password change", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(baseContext, "Fail to change password", Toast.LENGTH_SHORT).show()
                }
            }
    }
}