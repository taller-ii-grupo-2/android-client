package com.fiuba.hypechat_app

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        mAuth = FirebaseAuth.getInstance()


        btnSignUp.setOnClickListener {
            signUpValidation()

        }
    }

    private fun signUpValidation() {
        if (etUsername.text.toString().isEmpty()){
            etUsername.error = "Plase enter your username"
            etUsername.requestFocus()
            return
        }
        if (etEmail.text.toString().isEmpty()){
            etEmail.error = "Plase enter your email"
            etEmail.requestFocus()
            return
        }

       /* if (Patterns.EMAIL_ADDRESS.matcher(etEmail.text.toString()).matches()){
            etEmail.error = "Plase enter a valid email"
            etEmail.requestFocus()
            return
        }*/

        if (etPassword.text.toString().isEmpty()){
            etPassword.error = "Plase enter your password"
            etPassword.requestFocus()
            return
        }

        mAuth.createUserWithEmailAndPassword(etEmail.text.toString(), etPassword.text.toString() )
            .addOnCompleteListener(this) { task->
                if (task.isSuccessful) {
                   startActivity(Intent(this,SignInActivity::class.java))
                } else {
                    Toast.makeText(baseContext, "Sign up failed", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
