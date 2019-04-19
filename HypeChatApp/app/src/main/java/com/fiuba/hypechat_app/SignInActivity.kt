package com.fiuba.hypechat_app

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_sign_in.*

class SignInActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        mAuth = FirebaseAuth.getInstance()

        btnSignIn.setOnClickListener{
            confirmSignIn()
        }

       btnSignUp.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))

        }
        btnForgot.setOnClickListener {
            startActivity(Intent(this, ForgotActivity::class.java))
        }

    }

    private fun confirmSignIn() {

        if (etEmail.text.toString().isEmpty()){
            etEmail.error = "Plase enter your email"
            etEmail.requestFocus()
            return
        }

         if (!Patterns.EMAIL_ADDRESS.matcher(etEmail.text.toString()).matches()){
             etEmail.error = "Plase enter a valid email"
             etEmail.requestFocus()
             return
         }

        if (etPassword.text.toString().isEmpty()){
            etPassword.error = "Plase enter your password"
            etPassword.requestFocus()
            return
        }

        mAuth.signInWithEmailAndPassword(etEmail.text.toString(), etPassword.text.toString() )
            .addOnCompleteListener(this) { task->
                if (task.isSuccessful) {
                    val user = mAuth.currentUser
                    updateUI(user)

                } else {
                    updateUI(null)                }
            }
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null){
            startActivity(Intent(this, MainActivity::class.java))
        } else{
            Toast.makeText(baseContext, "Sign in failed, wrong credentials", Toast.LENGTH_SHORT).show()
        }

    }
}
