package com.fiuba.hypechat_app.activities.user_registration

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns

import android.widget.Toast
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException

import com.facebook.login.LoginResult
import com.fiuba.hypechat_app.*
import com.fiuba.hypechat_app.activities.WorkspacesListActivity
import com.fiuba.hypechat_app.models.SocketHandler
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.activity_sign_in.*

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SignInActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private var callbackManager: CallbackManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        FirebaseAuth.getInstance().signOut()
        val galletita = RetrofitClient.CookiesInterceptor()
        galletita.clearCookie()
        SocketHandler.disconnect()

        mAuth = FirebaseAuth.getInstance()

        btnSignIn.setOnClickListener {
            confirmSignIn()
        }

        btnSignUp.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        btnForgot.setOnClickListener {
            startActivity(Intent(this, ForgotActivity::class.java))
        }

        callbackManager = CallbackManager.Factory.create()

        btnfb.setReadPermissions("email", "public_profile")
        btnfb.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                handleFacebookAccessToken(loginResult.accessToken)
            }

            override fun onCancel() {
            }

            override fun onError(error: FacebookException) {
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        /*
         * Pass the activity result back to the Facebook SDK
         */
        callbackManager!!.onActivityResult(requestCode, resultCode, data)
    }

    private fun confirmSignIn() {
        if (etEmail.text.toString().isEmpty()) {
            etEmail.error = "Plase enter your email"
            etEmail.requestFocus()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(etEmail.text.toString()).matches()) {
            etEmail.error = "Plase enter a valid email"
            etEmail.requestFocus()
            return
        }

        if (etPassword.text.toString().isEmpty()) {
            etPassword.error = "Plase enter your password"
            etPassword.requestFocus()
            return
        }

        mAuth.signInWithEmailAndPassword(etEmail.text.toString(), etPassword.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val tokenUser = FirebaseAuth.getInstance().currentUser!!.getIdToken(true)
                        .addOnCompleteListener {
                            val idToken = it.result?.token
                            Log.d("TokenActivity", "Token:${idToken}")

                            saveLoginInfoToSharedPref(etEmail.text.toString(), etPassword.text.toString(), idToken)
                            subscribeToNotificationTopic(etEmail.text.toString().replace("@","~at~"))

                            signUserToSV(idToken!!)
                        }
                } else {
                    updateUI(null)
                }
            }
    }

    private fun subscribeToNotificationTopic(topic: String) {
        FirebaseMessaging.getInstance().subscribeToTopic(topic)
            .addOnCompleteListener { task ->
                var msg = "successfully subscribed to notif topic"
                if (!task.isSuccessful) {
                    msg = "couldn't get subscribed to notif topic"
                }
//                Log.d(null, msg)
            }
    }

    private fun saveLoginInfoToSharedPref(mail: String, password: String, token: String?) {
        val sharedPreferences = getSharedPreferences("loginPreferences", Context.MODE_PRIVATE)

        val editor = sharedPreferences.edit()
        editor.putString("mail", mail)
        editor.putString("password", password)
        editor.putString("token", token)
        editor.apply()
    }

    private fun handleFacebookAccessToken(token: AccessToken) {
        Log.d("SiginActivity", "handleFacebookAccessToken:$token")

        val credential = FacebookAuthProvider.getCredential(token.token)
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val user = mAuth.currentUser
                    updateUI(user)
                } else {
                    /*
                     * If sign in fails, display a message to the user.
                     */
                    Log.w("SiginActivity", "signInWithCredential:failure", task.exception)
                    Toast.makeText(
                        baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                    updateUI(null)
                }
            }
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            saveUserToFirebaseDB()
            val intent = Intent(this, WorkspacesListActivity::class.java)
            /*
             * Dont allow go back
             */
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        } else {
            Toast.makeText(baseContext, "Sign in failed, wrong credentials", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveUserToFirebaseDB() {
        val user = FirebaseAuth.getInstance().currentUser
        val name = user?.displayName
        val email = user?.email
        val uid = user?.uid
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")

        val userFb = User(-58.368508,-34.618625, email!!,"https://firebasestorage.googleapis.com/v0/b/hypechatapp-ebdd6.appspot.com/o/images%2Findex.png?alt=media&token=b43e8b85-6ad9-4659-8caf-df3e50a62c2e","" ,"" ,email!! )
         ref.setValue(userFb)
             .addOnSuccessListener {
                 Log.d("SignInActivity", "User added to database")
             }

         sendDataToSv(userFb)

        val tokenUser = FirebaseAuth.getInstance().currentUser!!.getIdToken(true)
            .addOnCompleteListener {
                val idToken = it.result?.token
                Log.d("TokenActivity", "Token:${idToken}")
                signUserToSV(idToken!!)
            }
    }

    private fun signUserToSV(uid: String) {
        val token = Token(uid)
        RetrofitClient.instance.signInUser(token)
            .enqueue(object : Callback<DefaultResponse> {
                override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                    Toast.makeText(baseContext, t.message, Toast.LENGTH_LONG).show()
                }

                override fun onResponse(call: Call<DefaultResponse>, response: Response<DefaultResponse>) {
                    if (response.isSuccessful) {
                        Toast.makeText(baseContext, "Successfully Logged in", Toast.LENGTH_SHORT).show()
                        saveCookieToSharedPref(RetrofitClient.cookiesInterceptor.cookie!!)
                        val intent = Intent(baseContext, WorkspacesListActivity::class.java )
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)

                    } else {
                        Toast.makeText(baseContext, "Failed to log in", Toast.LENGTH_SHORT).show()
                    }
                }
            })


    }

    private fun saveCookieToSharedPref(cookie: String) {
        val sharedPreferences = getSharedPreferences("loginPreferences", Context.MODE_PRIVATE)

        val editor = sharedPreferences.edit()
        editor.putString("cookie", cookie)
        editor.apply()
    }

    private fun sendDataToSv(user: User) {

        RetrofitClient.instance.createUser(user)
            .enqueue(object : Callback<DefaultResponse> {
                override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                    Toast.makeText(baseContext, t.message, Toast.LENGTH_LONG).show()
                }

                override fun onResponse(call: Call<DefaultResponse>, response: Response<DefaultResponse>) {
                    if (response.isSuccessful) {
                        Toast.makeText(baseContext, "Successfully Added", Toast.LENGTH_SHORT).show()
                        val intent = Intent(baseContext, WorkspacesListActivity::class.java )
                        //intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                    } else {
                        Toast.makeText(baseContext, "Failed to add item", Toast.LENGTH_SHORT).show()
                    }
                }
            })
    }
}