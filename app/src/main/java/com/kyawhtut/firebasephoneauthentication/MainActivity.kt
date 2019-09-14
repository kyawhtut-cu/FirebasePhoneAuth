package com.kyawhtut.firebasephoneauthentication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import com.kyawhtut.firebasephoneauthlib.util.Phone
import com.kyawhtut.firebasephoneauthlib.util.PhoneAuth
import com.kyawhtut.firebasephoneauthlib.util.PhoneVerifyCallback

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity() {

    private val phoneAuth: PhoneAuth = PhoneAuth.Builder(this).apply {
        appName = "Firebase Auth Lib"
        privacyPolicy = "https://kyawhtut.com"
        termsOfService = "https://kyawhtut.com/mmexchange"
    }.build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            phoneAuth.logout(
                success = {
                    phoneAuth.startActivity()
                },
                fail = {
                    Log.e(MainActivity::class.java.name, "error", it)
                }
            )
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        phoneAuth.onActivityResult(requestCode, resultCode, data, object : PhoneVerifyCallback {
            override fun Success(result: Phone) {
                Log.e(MainActivity::class.java.name, "Success $result")
                tv_login_result.text = "Login Result => $result"
            }

            override fun Error(error: String) {
                Log.e(MainActivity::class.java.name, "Error")
                tv_login_result.text = "Login Result => $error"
            }
        })
    }
}
