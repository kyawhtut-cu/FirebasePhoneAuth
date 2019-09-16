package com.kyawhtut.firebasephoneauthlib.util

import android.app.Activity
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.kyawhtut.firebasephoneauthlib.R
import com.kyawhtut.firebasephoneauthlib.ui.PhoneAuthentication
import kotlinx.android.synthetic.main.phone_authentication.*
import java.util.concurrent.TimeUnit

/**
 * Created by Kyaw Htut on 2019-09-16.
 */

class PhoneAuthCustom private constructor(
    private var activity: Activity? = null,
    private val fragment: Fragment? = null,
    private var phone: String = ""
) : PhoneAuthCustomRepository {

    private var authCredential: PhoneAuthCredential? = null
    private var verificationId: String = ""
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private var token: PhoneAuthProvider.ForceResendingToken? = null

    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            authCredential = credential
            if (credential.smsCode == null) {
                Log.w("onVerificationCompleted", "OTP verify with auto pass")
                val user = FirebaseAuth.getInstance().currentUser
                listener?.onVerificationSuccessful(
                    Phone(
                        user?.uid ?: "",
                        user?.phoneNumber ?: ""
                    )
                )
            }
        }

        override fun onVerificationFailed(e: FirebaseException) {
            if (listener != null) {
                listener?.onVerificationFailed(
                    Exception(
                        if (e is FirebaseTooManyRequestsException) "Your request is unusual request. So blocked your phone number"
                        else e.localizedMessage
                    )
                )
            }
        }

        override fun onCodeSent(
            vId: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {
            verificationId = vId
            this@PhoneAuthCustom.token = token
            if (listener != null) {
                listener?.onCodeSent(vId)
            }
        }
    }

    override fun sendCode(phone: String) {
        this.phone = phone
        if (phone.isEmpty()) throw IllegalArgumentException("Phone no is not empty. Please add phone number")
        if (activity == null && fragment == null) throw IllegalArgumentException("Activity or Fragment must not null. Please assign Activity or Fragment")
        if (activity == null)
            activity = fragment?.activity
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            phone.apply {
                Log.e("Phone => ", phone)
            },
            60,
            TimeUnit.SECONDS,
            activity!!,
            callbacks
        )
    }

    override fun sendCode() {
        sendCode(phone)
    }

    override fun resendCode() {
        if (phone.isEmpty()) throw IllegalArgumentException("Phone No. is empty or null.")
        if (activity == null && fragment == null) throw IllegalArgumentException("Activity or Fragment must not null. Please assign Activity or Fragment")
        if (token == null) throw IllegalArgumentException("Please first send code to use resend code")
        if (activity == null)
            activity = fragment?.activity
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            phone,
            60,
            TimeUnit.SECONDS,
            activity!!,
            callbacks,
            token
        )
    }

    override fun verifyOtp(otp: String) {
        if (otp.isEmpty()) throw IllegalArgumentException("OTP is null.")
        if (activity == null && fragment == null) throw IllegalArgumentException("Activity or Fragment must not null. Please assign Activity or Fragment")
        if (activity == null)
            activity = fragment?.activity

        if (verificationId.isNotEmpty())
            authCredential = PhoneAuthProvider.getCredential(verificationId, otp)
        firebaseAuth.signInWithCredential(authCredential!!)
            .addOnCompleteListener(activity!!) {
                if (listener != null) {
                    if (it.isSuccessful) {
                        val user = FirebaseAuth.getInstance().currentUser
                        listener?.onVerificationSuccessful(
                            Phone(
                                user?.uid ?: "",
                                user?.phoneNumber ?: ""
                            )
                        )
                    } else if (it.isCanceled) {
                        listener?.onVerificationFailed(Exception("Verification Canceled."))
                    } else {
                        if (it.exception is FirebaseAuthInvalidCredentialsException) {
                            listener?.onVerificationFailed(Exception("Invalid code"))
                        } else {
                            listener?.onVerificationFailed(
                                it.exception ?: java.lang.Exception("Something went wrong")
                            )
                        }
                    }
                }
            }

    }

    var listener: PhoneAuthListener? = null


    class Builder private constructor(
        private val activity: Activity? = null,
        private val fragment: Fragment? = null
    ) {

        constructor(activity: Activity?) : this(activity, null)
        constructor(fragment: Fragment?) : this(null, fragment)

        var phoneNumber: String = ""
        var phoneAuthCustom: PhoneAuthCustom? = null
        var listener: PhoneAuthListener? = null

        fun build(): PhoneAuthCustom {
            if (phoneAuthCustom == null) {
                phoneAuthCustom = PhoneAuthCustom(activity, fragment).apply {
                    phone = this@Builder.phoneNumber
                    listener = this@Builder.listener
                }
            }
            return phoneAuthCustom!!
        }
    }

}