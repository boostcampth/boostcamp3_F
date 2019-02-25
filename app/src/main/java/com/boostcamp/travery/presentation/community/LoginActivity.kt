package com.boostcamp.travery.presentation.community

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.boostcamp.travery.Constants
import com.boostcamp.travery.Constants.EXTRA_USER_ID
import com.boostcamp.travery.R
import com.boostcamp.travery.base.BaseActivity
import com.boostcamp.travery.databinding.ActivityLoginBinding
import com.boostcamp.travery.utils.toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : BaseActivity<ActivityLoginBinding>() {
    override val layoutResourceId: Int
        get() = R.layout.activity_login

    private lateinit var gso: GoogleSignInOptions
    private lateinit var mGoogleSignInClient: GoogleSignInClient

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
            offProgress()
            when (it) {
                "success" -> {
                    //TODO 설정 창으로 돌아감
                    setResult(Activity.RESULT_OK)
                    finish()
                }
                "nonexistent"->{
                    val intent=Intent(this, SignInActivity::class.java)
                    intent.putExtra(EXTRA_USER_ID,viewDataBinding.viewmodel?.userId)
                    intent.flags=Intent.FLAG_ACTIVITY_FORWARD_RESULT
                    startActivity(intent)
                    finish()
                }
                else -> {
                    it.toast(this)
                    finish()
                }
            }
        })

        sign_in_button.setOnClickListener {
            val signInIntent = mGoogleSignInClient.signInIntent
            startActivityForResult(signInIntent, Constants.SIGN_IN_GOOGLE)
        }

        btn_close.setOnClickListener {
            finish()
        }
    }

    override fun onStart() {
        super.onStart()
        viewDataBinding.viewmodel?.getCurrentUser()?.let {
            //로그인이 되어 있을 때 view 처리
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.SIGN_IN_GOOGLE) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            onProgress(resources.getString(R.string.progress_bar_message))
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                Log.d("lologin", account.toString())
                viewDataBinding.viewmodel?.firebaseAuthWithGoogle(account)
            } catch (e: ApiException) {
                Log.e("lologin", e.toString())
                offProgress()
                // Google Sign In failed, update UI appropriately
                //Log.w(FragmentActivity.TAG, "Google sign in failed", e)
                // ...
            }

            //handleSignInResult(task)
        }
    }
}
