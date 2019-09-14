package com.kyawhtut.firebasephoneauthlib.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import androidx.core.widget.addTextChangedListener
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.kyawhtut.firebasephoneauthlib.R
import com.kyawhtut.firebasephoneauthlib.otp.OnOtpCompletionListener
import com.kyawhtut.firebasephoneauthlib.util.Phone
import com.kyawhtut.firebasephoneauthlib.util.SuccessDialog
import kotlinx.android.synthetic.main.phone_authentication.*
import java.util.concurrent.TimeUnit

class PhoneAuthentication : AppCompatActivity() {

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private var resendToken: PhoneAuthProvider.ForceResendingToken? = null
    private var verificationId: String = ""
    private var authCredential: PhoneAuthCredential? = null

    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            Log.d(TAG, "onVerificationCompleted")
            authCredential = credential
        }

        override fun onVerificationFailed(e: FirebaseException) {
            Log.e(TAG, "onVerificationFailed", e)
            if (e is FirebaseAuthInvalidCredentialsException) {
                et_phone_no.error = "Invalid phone number"
            } else if (e is FirebaseTooManyRequestsException) {
                et_phone_no.error = "Your request is unusual request. So blocked your phone number."
            }
        }

        override fun onCodeSent(
            vId: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {
            Log.d(TAG, "onCodeSent:$vId")
            verificationId = vId
            resendToken = token
            btn_verify.apply {
                isEnabled = false
                text = getString(R.string.lbl_btn_continue)
            }
            startTimer()
            tv_terms_of_service.visibility = View.GONE
            tv_privacy_policy.visibility = View.GONE
            tv_app_name.text = getString(R.string.lbl_verification_code)
            group_phone_input.visibility = View.GONE
            group_otp_input.visibility = View.VISIBLE
            tv_code_send_description.text = getString(
                R.string.lbl_verify_code_send_description,
                String.format("+%s%s", ccp.selectedCountryCode, ed_phone_number.text.toString())
            )
        }
    }

    override
    fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        setContentView(R.layout.phone_authentication)

        getDataAndBind()

        tv_terms_of_service.setOnClickListener {
            openBrowser(intent?.getStringExtra(extraTermsOfService) ?: "")
        }
        tv_privacy_policy.setOnClickListener {
            openBrowser(intent?.getStringExtra(extraPrivacyPolicy) ?: "")
        }

        ed_phone_number.addTextChangedListener(
            onTextChanged = { _, _, _, _ ->
                btn_verify.isEnabled = ed_phone_number.text.toString().length > 5
            }
        )

        otp_view.setOtpCompletionListener(object : OnOtpCompletionListener {
            override fun onOtpCompleted(otp: String?) {
                btn_verify.isEnabled = true
                tv_otp_code_error.visibility = View.GONE
            }

            override fun onOtpUnCompleted() {
                btn_verify.isEnabled = false
            }
        })

        btn_verify.setOnClickListener {
            if (authCredential == null && verificationId.isEmpty())
                verifyPhone()
            else {
                if (verificationId.isNotEmpty())
                    authCredential =
                        PhoneAuthProvider.getCredential(verificationId, otp_view.text.toString())
                signInWithPhoneAuthCredential(authCredential!!)
            }
        }

        btn_resend.setOnClickListener {
            resendCode()
        }
    }

    private fun openBrowser(link: String) {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(link)))
    }

    private fun getDataAndBind() {
        intent?.let {
            tv_app_name.text = it.getStringExtra(extraAppName) ?: "App Name"
            iv_app_logo.setImageResource(it.getIntExtra(extraAppLogo, R.drawable.login_bg))
            iv_header.setImageResource(it.getIntExtra(extraHeaderImage, R.drawable.login_bg))
            tv_terms_of_service.apply {
                text = HtmlCompat.fromHtml(
                    String.format(
                        "<u>%s</u>",
                        getString(R.string.lbl_terms_of_service)
                    ),
                    HtmlCompat.FROM_HTML_MODE_LEGACY
                )
                visibility = if ((it.getStringExtra(extraTermsOfService)
                        ?: "").isEmpty()
                ) View.GONE else View.VISIBLE
            }
            tv_privacy_policy.apply {
                text = HtmlCompat.fromHtml(
                    String.format(
                        "<u>%s</u>",
                        getString(R.string.lbl_privacy_policy)
                    ),
                    HtmlCompat.FROM_HTML_MODE_LEGACY
                )
                visibility = if ((it.getStringExtra(extraPrivacyPolicy)
                        ?: "").isEmpty()
                ) View.GONE else View.VISIBLE
            }
        }
    }

    private fun verifyPhone() {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            String.format("+%s%s", ccp.selectedCountryCode, ed_phone_number.text.toString()),
            60,
            TimeUnit.SECONDS,
            this,
            callbacks
        )
    }

    private fun resendCode() {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            String.format("+%s%s", ccp.selectedCountryCode, ed_phone_number.text.toString()),
            60,
            TimeUnit.SECONDS,
            this,
            callbacks,
            resendToken
        )
        btn_resend.isEnabled = false
    }

    private fun startTimer() {
        btn_resend.isEnabled = false
        object : CountDownTimer(60 * 1000, 1000) {
            override fun onFinish() {
                btn_resend.apply {
                    isEnabled = true
                    text = "Resend code"
                }
            }

            override fun onTick(p0: Long) {
                btn_resend.text =
                    getString(
                        R.string.lbl_btn_resend,
                        "%02d".format((p0 / 1000) / 60),
                        "%02d".format((p0 / 1000) % 60)
                    )
            }
        }.start()
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithCredential:success")
                    SuccessDialog.Builder(this).apply {
                        this.message = "Auth Success"
                        isCancelable = false
                        duration = 1000
                        callback = {
                            setResult(Activity.RESULT_OK)
                            finish()
                        }
                    }.show(supportFragmentManager, SuccessDialog::class.java.name)
                } else {
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        tv_otp_code_error.apply {
                            text = "Invalid code"
                            visibility = View.VISIBLE
                        }
                    }
                }
            }
    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_CANCELED)
        super.onBackPressed()
    }

    companion object {
        private val TAG = PhoneAuthentication::class.java.name
        const val extraTermsOfService = "extra.termsOfService"
        const val extraPrivacyPolicy = "extra.privacyPolicy"
        const val extraAppName = "extra.appName"
        const val extraAppLogo = "extra.appLogo"
        const val extraHeaderImage = "extra.headerImage"
    }
}