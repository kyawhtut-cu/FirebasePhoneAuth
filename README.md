# Firebase Phone Authentication

Application အတွက် ဖုန်းနံပါတ် Authentication အတွက်ကို အလွယ်တကူအသုံးပြုရန်အတွက်ဖြစ်ပါသည်။

Phone authentication အတွက်ကို Firebase ၏ Authentication ကိုအသုံးပြုထားပါသည်။ Facebook account သည် မကြာမှီ Shut down ဖြစ်တော့မှာဖြစ်သည့်အတွက် Phone authentication ကို လွယ်လွယ်ကူကူ Implementation သုံးလို့ရအောင်အတွက် လုပ်ထားဖြစ်ပါသည်။

Lib ထဲတွင် Firebase ၏ Default Auth UI နှင့်သုံးလို့ရသလို၊ Lib မှ Support ပေးထားသည့် UI နှင့်လဲသုံးလို့ရပါသည်။ ယခု Version တွင် မိမိ UI နှင့် Firebase authentication ကို လွယ်လွယ်ကူကူ တွဲသုံးလို့ရအောင် Support ပေးထားခြင်းမရှိသေးပါ။ 

နောက်လာမည့် Version တွင်တော့ Support ပေးသွားပါမည်။ Sample Usage ကို Read Me တွင်ဖတ်ပြီးစမ်းသပ်နိုင်ပါသည်။


How to add to your project
--------------

1. Add jitpack.io to your root build.gradle file:

     ```groovy
       allprojects {
         repositories {
           ...
           maven { url 'https://jitpack.io' }
         }
       }

2. Add library to your app build.gradle file then sync

	Release version - [![Download](https://raw.githubusercontent.com/kyawhtut-cu/FirebasePhoneAuth/master/screenshoot/download.svg?sanitize=true)](https://github.com/kyawhtut-cu/FirebasePhoneAuth/releases/tag/1.0.1)

	```groovy
	dependencies {
		...
		implementation 'com.github.kyawhtut-cu:FirebasePhoneAuth:<version-release>'
	}
	```

3. Add ```PhoneAuthentication``` into your AndroidManifest.xml
	
	```xml
	<activity
            android:name="com.kyawhtut.firebasephoneauthlib.ui.PhoneAuthentication"
            android:theme="@style/PhoneAuthentication.NoActionBar" />
	```

Usage
--------------


	/* *
	*
	* create phone auth object instance
	*
	* */
	private val phoneAuth: PhoneAuth = PhoneAuth.Builder(activity or fragment).apply {
		appName = "Firebase Auth Lib"
		privacyPolicy = "https://kyawhtut.com"
		termsOfService = "https://kyawhtut.com"
	}.build()
	
	/* *
	* 
	* logout current account
	*
	* */
	phoneAuth.logout(
		success = {
			Log.i("Logout success", "Success")
		},
		fail = {
			Log.e("Logout error", "error", it)
		}
	)

	/* *
	*
	* go To Login Screen
	* default parameter is LoginTheme.MaterialTheme
	* LoginTheme -> FirebaseTheme, MaterialTheme
	*
	* */
	phoneAuth.startActivity()

	/* *
    	*
    	* to check Account Login
    	* return @Boolean
    	*
    	* */
    PhoneAuth.isLogin()

    /* *
        *
        * to check Account Login
        * return @Boolean
        *
        * */
    PhoneAuth.logout(
        context,
        success = {
            Log.i("Logout success", "Success")
        },
        fail = {
            Log.e("Logout error", "error", it)
        }
    )

	

ပြီးသွားပါက onActivityResult(...){} ထဲမှာ အောက်က listener လေးကိုရေးပေးပါ။

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		...
		phoneAuth.onActivityResult(requestCode, resultCode, data, object : PhoneVerifyCallback {
			override fun Success(result: Phone) {
			}
    	
			override fun Error(error: String) {
			}
		})
	}
	
Firebase setup
--------------

1. Add google-service classpath to your root build.gradle file:

     ```groovy
	dependencies {
		...
		classpath 'com.google.gms:google-services:4.3.2'
	}

2. If you haven't yet connected your app to your Firebase project, do so from the [Firebase console](https://console.firebase.google.com/)

3. If you haven't already set your app's SHA-1 hash in the [Firebase console](https://console.firebase.google.com/), do so. See [Authenticating Your Client](https://developers.google.com/android/guides/client-auth) for information about finding your app's SHA-1 hash.

4. Enable Phone Number sign-in for your Firebase project

	1. In the [Firebase console](https://console.firebase.google.com/), open the Authentication section.
	
	2. On the Sign-in Method page, enable the Phone Number sign-in method.

5. If you have been added SHA-1 in project console and then download google-services.json file. And then add this file to app module.

Also, note that phone number sign-in requires a physical device and won't work on an emulator.

Theme
--------
Material Theme တွင် Privacy Policy, Terms of service တို့ကိုမထည့်လိုပါက Builder တွင် privacyPolicy, termsOfService တို့ကို empty string သို့မဟုတ် လုံးဝ(လုံးဝ) မထည့်ပေးပါနှင့်။ Default သည်ဖျောက်ပေးထားပါသည်။

Function | type | default value | description
--- | --- | --- | --- 
termsOfService | String | empty string | web url string, if you want to hide this button no need to add value default is hide.
privacyPolicy | String | empty string | web url string, if you want to hide this button no need to add value default is hide.
appName | String | App Name | Your application name to show in auth screen.
appLogo | @Resource Integer | R.drawable.default_header | Show your application logo in auth screen.
headerImage | @Resource Integer | R.drawable.default_header | To show as header image in auth scree.
loginTheme | LoginTheme | LoginTheme.MaterialTheme | To determine UI screen. If you want to use Firebase default UI, you add parameter in startActivity(LoginTheme.FirebaseTheme). But you want to you MaterialTheme, you no need to pass parameter default is MaterialTheme.

Screenshoot
--------

[![screen](https://raw.githubusercontent.com/kyawhtut-cu/FirebasePhoneAuth/master/screenshoot/photo-1.png)](https://github.com/kyawhtut-cu/FirebasePhoneAuth)

[![screen](https://raw.githubusercontent.com/kyawhtut-cu/FirebasePhoneAuth/master/screenshoot/photo-2.png)](https://github.com/kyawhtut-cu/FirebasePhoneAuth)

[![screen](https://raw.githubusercontent.com/kyawhtut-cu/FirebasePhoneAuth/master/screenshoot/photo-3.png)](https://github.com/kyawhtut-cu/FirebasePhoneAuth)

[![screen](https://raw.githubusercontent.com/kyawhtut-cu/FirebasePhoneAuth/master/screenshoot/photo-4.png)](https://github.com/kyawhtut-cu/FirebasePhoneAuth)

[![screen](https://raw.githubusercontent.com/kyawhtut-cu/FirebasePhoneAuth/master/screenshoot/photo-5.png)](https://github.com/kyawhtut-cu/FirebasePhoneAuth)

[![screen](https://raw.githubusercontent.com/kyawhtut-cu/FirebasePhoneAuth/master/screenshoot/photo-6.png)](https://github.com/kyawhtut-cu/FirebasePhoneAuth)



Credits
--------

Country Code Picker: joielechong [https://github.com/joielechong/CountryCodePicker/](https://github.com/joielechong/CountryCodePicker/)

DiagonalLayout: florent37 [https://github.com/florent37/DiagonalLayout/](https://github.com/florent37/DiagonalLayout/)

CircleImageView: hdodenhof [https://github.com/hdodenhof/CircleImageView/](https://github.com/hdodenhof/CircleImageView/)

License
--------

    Copyright 2019 kyawhtut-cu

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
