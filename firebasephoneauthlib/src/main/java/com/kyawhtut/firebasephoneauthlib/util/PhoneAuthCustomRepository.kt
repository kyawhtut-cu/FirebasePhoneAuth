package com.kyawhtut.firebasephoneauthlib.util

import com.google.firebase.auth.PhoneAuthProvider

/**
 * Created by Kyaw Htut on 2019-09-16.
 */
interface PhoneAuthCustomRepository {

    fun sendCode(phone: String = "")
    fun sendCode()

    fun resendCode()

    fun verifyOtp(otp: String)
}