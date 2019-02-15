package com.boostcamp.travery.community

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.boostcamp.travery.Constants
import com.boostcamp.travery.R
import com.boostcamp.travery.base.BaseActivity
import com.boostcamp.travery.databinding.ActivityLoginBinding
import com.boostcamp.travery.utils.toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.activity_login.*
import java.security.AccessController.getContext


class LoginActivity : BaseActivity<ActivityLoginBinding>() {
    override val layoutResourceId: Int
        get() = R.layout.activity_login

    private lateinit var gso:GoogleSignInOptions
    private lateinit var mGoogleSignInClient:GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewDataBinding.viewmodel = ViewModelProviders.of(this).get(LoginViewModel::class.java)
        setContentView(viewDataBinding.root)

        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("932842954747-fld93ub0aj6i4hu2f0htdrm6ef1isgp5.apps.googleusercontent.com")
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        viewDataBinding.viewmodel?.loginSuccessString?.observe(this, Observer {
            when(it){
                "success" -> finish()
                else -> it.toast(this)
            }
        })

        sign_in_button.setOnClickListener {
            val signInIntent = mGoogleSignInClient.signInIntent
            startActivityForResult(signInIntent, Constants.SIGN_IN_GOOGLE)
        }
    }

    override fun onStart() {
        super.onStart()
        viewDataBinding.viewmodel?.getCurrentUser()?.let{
            //로그인이 되어 있을 때 view 처리
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.SIGN_IN_GOOGLE) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                Log.d("lologin",account.toString())
                viewDataBinding.viewmodel?.firebaseAuthWithGoogle(account)
            } catch (e: ApiException) {
                Log.e("lologin",e.toString())
                // Google Sign In failed, update UI appropriately
                //Log.w(FragmentActivity.TAG, "Google sign in failed", e)
                // ...
            }

            //handleSignInResult(task)
        }
    }
}
