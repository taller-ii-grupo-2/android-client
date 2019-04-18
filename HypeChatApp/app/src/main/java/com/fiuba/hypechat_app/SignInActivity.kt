package com.fiuba.hypechat_app

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_sign_in.*

class SignInActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        mAuth = FirebaseAuth.getInstance()

       /* btnSignIn.setOnClickListener{
            confirmSignIn()
        }*/

        btnSignUp.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))

        }

    }

    private fun confirmSignIn() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
