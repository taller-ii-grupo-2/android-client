package com.fiuba.hypechat_app

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_forgot.*
import kotlinx.android.synthetic.main.activity_sign_up.*

class ForgotActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot)


        mAuth = FirebaseAuth.getInstance()

        btnConfirm.setOnClickListener {
            confirmation()

        }
    }


    private fun confirmation() {
        if (emailtxt.text.toString().isEmpty()){
            emailtxt.error = "Plase enter your email"
            emailtxt.requestFocus()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(emailtxt.text.toString()).matches()){
            emailtxt.error = "Plase enter a valid email"
            emailtxt.requestFocus()
            return
        }

        mAuth.sendPasswordResetEmail(emailtxt.text.toString())
            .addOnCompleteListener { task->
                if(task.isSuccessful){
                    Toast.makeText(this, "Email sent", Toast.LENGTH_SHORT).show()
                }
            }

    }
}
