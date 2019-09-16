package com.kyawhtut.firebasephoneauthlib.util

import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider

/**
 * Created by Kyaw Htut on 2019-09-16.
 */
interface PhoneAuthListener {

    fun onCodeSent(verificationId: String)
    fun onVerificationCompleted()
    fun onVerificationSuccessful(phone: Phone)
    fun onVerificationFailed(e: Exception)

}