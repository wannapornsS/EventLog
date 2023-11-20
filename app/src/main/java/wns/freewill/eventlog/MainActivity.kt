package wns.freewill.eventlog

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.navigation.ui.AppBarConfiguration
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.FacebookSdk
import com.facebook.GraphRequest
import com.facebook.GraphResponse
import com.facebook.HttpMethod
import com.facebook.LoggingBehavior
import com.facebook.appevents.AppEventsConstants
import com.facebook.appevents.AppEventsLogger
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import wns.freewill.eventlog.databinding.ActivityMainBinding
import java.security.MessageDigest
import java.util.Arrays


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    private lateinit var logger: AppEventsLogger

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        try {
            val info = packageManager.getPackageInfo(
                "wns.freewill.evenlog",
                PackageManager.GET_SIGNATURES
            )
            Log.d("KeyHash0:", info.packageName)
            for (signature in info.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                Log.d("KeyHash1:", Base64.encodeToString(md.digest(), Base64.DEFAULT))
            }
        } catch (e: Exception) {
            Log.d("KeyHash2:", e.message + "")
        }

//        FacebookSdk.fullyInitialize()
//
        val accessToken = AccessToken.getCurrentAccessToken()
        fetchGraph(accessToken)

        Log.e("accessToken", accessToken?.token ?: "nullll")
//        FacebookSdk.setClientToken(accessToken?.token)
//
//        FacebookSdk.sdkInitialize(this)
        FacebookSdk.setIsDebugEnabled(true)
        FacebookSdk.addLoggingBehavior(LoggingBehavior.APP_EVENTS)
//        login()
        logger = AppEventsLogger.newLogger(this)

        logSearchedEvent("TYPE_SEARCH", "test", true)

//        logSentFriendRequestEvent()
    }

    /**
     * This function assumes logger is an instance of AppEventsLogger and has been
     * created using AppEventsLogger.newLogger() call.
     */


    private fun logSearchedEvent(contentType: String?, searchString: String?, success: Boolean) {
        val params = Bundle()
        params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_TYPE, contentType)
        params.putString(AppEventsConstants.EVENT_PARAM_SEARCH_STRING, searchString)
        params.putInt(AppEventsConstants.EVENT_PARAM_SUCCESS, if (success) 1 else 0)
        logger.logEvent(AppEventsConstants.EVENT_NAME_SEARCHED, params)
    }

    private fun fetchGraph(accessToken : AccessToken?){
        val request = GraphRequest.newMeRequest(accessToken) { _, response ->
            Log.d("fetchGraph", "$response")
        }

        val parameters = Bundle()
        parameters.putString("fields", "id,name,friends")
        request.parameters = parameters
        request.executeAsync()


        GraphRequest(
            AccessToken.getCurrentAccessToken(),
            "6503913743049547",
            null, HttpMethod.GET, {
                Log.d("fetchGraph 2", "${it.rawResponse}")
            }

        ).executeAsync()
    }



    private fun login(){

        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"));
        val callbackManager = CallbackManager.Factory.create()
        val EMAIL = "email"


        binding.loginButton.setReadPermissions(listOf(EMAIL))
        // If you are using in a fragment, call loginButton.setFragment(this);

        // Callback registration
        // If you are using in a fragment, call loginButton.setFragment(this);

        // Callback registration
        binding.loginButton.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onCancel() {

            }

            override fun onError(error: FacebookException) {

            }

            override fun onSuccess(result: LoginResult) {

            }

        })
    }


}