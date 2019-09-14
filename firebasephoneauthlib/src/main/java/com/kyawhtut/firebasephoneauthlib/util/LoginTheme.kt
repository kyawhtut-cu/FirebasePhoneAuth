package com.kyawhtut.firebasephoneauthlib.util

import java.io.Serializable

sealed class LoginTheme : Serializable {

    object FirebaseTheme : LoginTheme()
    object MaterialTheme : LoginTheme()
}