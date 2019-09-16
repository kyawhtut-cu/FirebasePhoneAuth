package com.kyawhtut.firebasephoneauthentication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import com.kyawhtut.firebasephoneauthlib.util.*
import com.kyawhtut.firebasephoneauthlib.util.PhoneAuth.Companion.isLogin

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.content_main.btn_verify

class MainActivity : AppCompatActivity() {

    private val phoneAuth: PhoneAuth = PhoneAuth.Builder(this).apply {
        appName = "Firebase Auth Lib"
        privacyPolicy = "https://kyawhtut.com"
        termsOfService = "https://kyawhtut.com/mmexchange"
    }.build()

    private var index = 0

    private val phoneAuthCustom = PhoneAuthCustom.Builder(this).build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        phoneAuthCustom.listener = object : PhoneAuthCallback() {
            override fun onCodeSent(
                verificationId: String
            ) {
                Log.e("onCodeSent", "verificationId => $verificationId")
                tv_login_result.text =
                    String.format("%s\n%s", tv_login_result.text, "onCodeSend => verificationId")
            }

            override fun onVerificationCompleted() {
                Log.e("onVerificationCompleted", "onCompleted")
                tv_login_result.text =
                    String.format("%s\n%s", tv_login_result.text, "onVerificationCompleted")
            }

            override fun onVerificationFailed(e: Exception) {
                Log.e("onVerificationFailed", "Error", e)
                tv_login_result.text =
                    String.format("%s\n%s", tv_login_result.text, "onVerificattionFailed => $e")
            }

            override fun onVerificationSuccessful(phone: Phone) {
                tv_login_result.text =
                    String.format("%s\n%s", tv_login_result.text, "Login Result => $phone")
            }
        }

        btn_logout.isEnabled = PhoneAuth.isLogin()

        btn_logout.setOnClickListener {
            PhoneAuth.logout(
                this,
                success = {
                    tv_login_result.text =
                        String.format("%s\n%s", tv_login_result.text, "Logout success")
                },
                fail = {
                    tv_login_result.text =
                        String.format("%s\n%s", tv_login_result.text, "Login error => $it")
                }
            )
        }

        btn_code_send.setOnClickListener {
            phoneAuthCustom.sendCode("+95${ed_phone.text}")
        }

        btn_resend_code.setOnClickListener {
            phoneAuthCustom.resendCode()
        }

        btn_verify.setOnClickListener {
            phoneAuthCustom.verifyOtp(ed_code.text.toString())
        }

        btn_auto_verify.setOnClickListener {
            phoneAuth.startActivity("09973419006")
        }

        btn_go_with_ui.setOnClickListener {
            phoneAuth.startActivity()
        }

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        phoneAuth.onActivityResult(requestCode, resultCode, data, object : PhoneVerifyCallback {
            override fun Success(result: Phone) {
                Log.e(MainActivity::class.java.name, "Success $result")
                tv_login_result.text =
                    String.format("%s\n%s", tv_login_result.text, "Login Result => $result")
            }

            override fun Error(error: String) {
                Log.e(MainActivity::class.java.name, "Error")
                tv_login_result.text =
                    String.format("%s\n%s", tv_login_result.text, "Login Result => $error")
            }
        })
    }
}
