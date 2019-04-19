package com.fiuba.hypechat_app

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        mAuth = FirebaseAuth.getInstance()


        signupbtn.setOnClickListener {
            signUpValidation()

        }
    }

    private fun signUpValidation() {
        if (usernamebox.text.toString().isEmpty()){
            usernamebox.error = "Plase enter your username"
            usernamebox.requestFocus()
            return
        }
        if (emailbox.text.toString().isEmpty()){
            emailbox.error = "Plase enter your email"
            emailbox.requestFocus()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(emailbox.text.toString()).matches()){
            emailbox.error = "Plase enter a valid email"
            emailbox.requestFocus()
            return
        }

        if (passwordbox.text.toString().isEmpty()){
            passwordbox.error = "Plase enter your password"
            passwordbox.requestFocus()
            return
        }

        mAuth.createUserWithEmailAndPassword(emailbox.text.toString(), passwordbox.text.toString() )
            .addOnCompleteListener(this) { task->
                if (task.isSuccessful) {
                    //saveUserToFirebaseDB()
                    startActivity(Intent(this,SignInActivity::class.java))
                    saveUserToFirebaseDB()
                } else {
                    Toast.makeText(baseContext, "Sign up failed", Toast.LENGTH_SHORT).show()
                }
            }
    }



    private fun saveUserToFirebaseDB (){
        val uid = FirebaseAuth.getInstance().uid ?:""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        val user = User(usernamebox.text.toString(), emailbox.text.toString(), uid)
        ref.setValue(user)
            .addOnSuccessListener {
                Log.d("SignUpActivity", "User added to database")

            }
    }
}


class User (val username:String, val email: String, val uid:String)
