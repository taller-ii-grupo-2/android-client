package com.fiuba.hypechat_app

import android.content.Intent
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_sign_up.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.*
import java.net.HttpURLConnection
import java.net.URL

class SignUpActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth

    lateinit var service: ApiService
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        mAuth = FirebaseAuth.getInstance()


        signupbtn.setOnClickListener {
            signUpValidation()
            //sendDataToSv2()

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

        sendDataToSv(user)


    }

    private fun sendDataToSv(user:User) {

        RetrofitClient.instance.createUser( user.username, user.email, user.uid)
            .enqueue(object: Callback<DefaultResponse>{
                override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                    Toast.makeText(baseContext, t.message, Toast.LENGTH_LONG).show()
                }

                override fun onResponse(call: Call<DefaultResponse>, response: Response<DefaultResponse>) {
                    Toast.makeText(baseContext, "Se agrego a la base de datos externa", Toast.LENGTH_LONG).show()
                }

            })


    }

    private fun sendDataToSv2() {

        RetrofitClient.instance.createUser("gabrielpiñeiro", "gabipiñeiro@gmail.com","218fhf317f7h31f3h7")
            .enqueue(object: Callback<DefaultResponse>{
                override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                    Toast.makeText(baseContext, t.message, Toast.LENGTH_LONG).show()
                }

                override fun onResponse(call: Call<DefaultResponse>, response: Response<DefaultResponse>) {
                    Toast.makeText(baseContext, "Se agrego a la base de datos externa", Toast.LENGTH_LONG).show()
                }

            })


    }


}

