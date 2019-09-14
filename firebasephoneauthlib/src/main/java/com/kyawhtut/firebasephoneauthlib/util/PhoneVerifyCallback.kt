package com.kyawhtut.firebasephoneauthlib.util

interface PhoneVerifyCallback {

    fun Success(result: Phone)

    fun Error(error: String)
}