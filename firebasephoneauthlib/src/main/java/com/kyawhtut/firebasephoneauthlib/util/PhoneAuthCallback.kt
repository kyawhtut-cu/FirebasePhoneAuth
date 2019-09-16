package com.kyawhtut.firebasephoneauthlib.util

import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider

/**
 * Created by Kyaw Htut on 2019-09-16.
 */
abstract class PhoneAuthCallback : PhoneAuthListener {

    override fun onVerificationCompleted() {
    }

    override fun onCodeSent(verificationId: String) {
    }

    override fun onVerificationFailed(e: Exception) {
    }

    override fun onVerificationSuccessful(phone: Phone) {
    }
}