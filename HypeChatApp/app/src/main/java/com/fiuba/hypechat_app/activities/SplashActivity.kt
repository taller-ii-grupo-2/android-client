package com.fiuba.hypechat_app.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.fiuba.hypechat_app.R
import com.fiuba.hypechat_app.activities.user_registration.SignInActivity
import android.content.Context
import android.os.Handler
import com.fiuba.hypechat_app.RetrofitClient


class SplashActivity : AppCompatActivity() {

    /** Duration of wait  */
    private val SPLASH_DISPLAY_LENGTH = 3000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        /* New Handler to start the Menu-Activity
         * and close this Splash-Screen after some seconds.*/
        Handler().postDelayed(Runnable {

            val prefs = this.getSharedPreferences("loginPreferences", Context.MODE_PRIVATE)
            val mail = prefs.getString("mail", "")
            val password = prefs.getString("password", "")
            val token = prefs.getString("token", "")
            val cookie = prefs.getString("cookie", "")

            /* Create an Intent that will start the Menu-Activity. */
            if (cookie.isEmpty()) {
                val intent = Intent(baseContext, SignInActivity::class.java)
                /*
                 * Dont allow go back
                 */
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }else{
                RetrofitClient.cookiesInterceptor.cookie = cookie
                val intent = Intent(baseContext, WorkspacesListActivity::class.java )
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        }, SPLASH_DISPLAY_LENGTH.toLong())
    }
}
