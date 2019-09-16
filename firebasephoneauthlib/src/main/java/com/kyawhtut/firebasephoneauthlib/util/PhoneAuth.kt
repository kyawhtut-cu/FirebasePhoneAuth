package com.kyawhtut.firebasephoneauthlib.util

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.fragment.app.Fragment
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.kyawhtut.firebasephoneauthlib.R
import com.kyawhtut.firebasephoneauthlib.ui.PhoneAuthentication

class PhoneAuth private constructor(
    private var activity: Activity? = null,
    var termsOfService: String = "",
    var privacyPolicy: String = "",
    var appName: String = "",
    var appLogo: Int = R.drawable.default_header,
    var headerImage: Int = R.drawable.default_header
) : PhoneVerify {

    constructor(
        fm: Fragment? = null,
        termsOfService: String = "",
        privacyPolicy: String = "",
        appName: String = "",
        appLogo: Int = R.drawable.default_header,
        headerImage: Int = R.drawable.default_header
    ) : this(fm?.activity ?: null, termsOfService, privacyPolicy, appName, appLogo, headerImage)

    private val providers = arrayListOf(
        AuthUI.IdpConfig.PhoneBuilder().build()
    )

    companion object {
        private const val PHONE_AUTH = 0x1497
        private const val PHONE_AUTH_DEFAULT = 0x1498

        fun logout(ctx: Context, success: () -> Unit, fail: (Exception) -> Unit) {
            AuthUI.getInstance().signOut(ctx).addOnCompleteListener {
                if (it.isSuccessful) success()
                else fail(it.exception ?: Exception("Something went wrong!!"))
            }
        }
    }

    override fun startActivity(loginTheme: LoginTheme) {
        if (activity == null) Throwable("Please set activity. Activity must not be null.")
        if (loginTheme is LoginTheme.MaterialTheme)
            activity?.startActivityForResult(
                getIntent(),
                PHONE_AUTH
            )
        else {
            activity?.startActivityForResult(
                AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(providers)
                    .setLogo(appLogo)
                    .setTosAndPrivacyPolicyUrls(
                        if (termsOfService.isEmpty()) "https://joebirch.co/terms.html" else termsOfService,
                        if (privacyPolicy.isEmpty()) "https://joebirch.co/privacy.html" else privacyPolicy
                    )
                    .build(),
                PHONE_AUTH_DEFAULT
            )
        }
    }

    private fun getIntent(): Intent {
        val intent = Intent(activity, PhoneAuthentication::class.java)
        intent.apply {
            putExtra(PhoneAuthentication.extraTermsOfService, termsOfService)
            putExtra(PhoneAuthentication.extraPrivacyPolicy, privacyPolicy)
            putExtra(PhoneAuthentication.extraAppName, appName)
            putExtra(PhoneAuthentication.extraAppLogo, appLogo)
            putExtra(PhoneAuthentication.extraHeaderImage, headerImage)
        }
        return intent
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?,
        func: PhoneVerifyCallback
    ) {
        if (requestCode == PHONE_AUTH || requestCode == PHONE_AUTH_DEFAULT) {
            if (resultCode == RESULT_OK) {
                val user = FirebaseAuth.getInstance().currentUser
                func.Success(Phone(user?.uid ?: "", user?.phoneNumber ?: ""))
            } else {
                func.Error("Login canceled!!")
            }
        }
    }

    override fun logout(success: () -> Unit, fail: (Exception) -> Unit) {
        if (activity == null) Throwable("Please set activity. Activity must not be null.")
        AuthUI.getInstance().signOut(activity!!).addOnCompleteListener {
            if (it.isSuccessful) success()
            else fail(it.exception ?: Exception("Something went wrong!!"))
        }
    }

    class Builder(var activity: Activity?) {
        private lateinit var phoneAuth: PhoneAuth

        var termsOfService: String = ""
        var privacyPolicy: String = ""
        var appName: String = ""
        var appLogo: Int = R.drawable.default_header
        var headerImage: Int = R.drawable.default_header

        fun build(): PhoneAuth {
            phoneAuth = PhoneAuth(
                activity,
                termsOfService,
                privacyPolicy,
                appName,
                appLogo,
                headerImage
            )
            return phoneAuth
        }
    }
}