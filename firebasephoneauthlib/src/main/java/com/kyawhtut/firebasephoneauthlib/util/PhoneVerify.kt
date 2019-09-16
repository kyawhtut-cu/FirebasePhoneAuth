package com.kyawhtut.firebasephoneauthlib.util

import android.app.Activity
import android.content.Intent

interface PhoneVerify {

    fun startActivity(loginTheme: LoginTheme = LoginTheme.MaterialTheme)

    fun startActivity(phone: String, loginTheme: LoginTheme = LoginTheme.MaterialTheme)

    fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?,
        func: PhoneVerifyCallback
    )

    fun logout(success: () -> Unit, fail: (Exception) -> Unit)
}