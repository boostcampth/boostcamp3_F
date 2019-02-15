package com.boostcamp.travery.community

import android.app.Application
import com.boostcamp.travery.base.BaseViewModel
import com.google.firebase.auth.FirebaseAuth
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.GoogleAuthProvider
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseUser




class LoginViewModel(application: Application) : BaseViewModel(application) {
    private val mAuth by lazy {
        FirebaseAuth.getInstance()
    }

    val loginSuccessString = MutableLiveData<String>()

    fun getCurrentUser(): FirebaseUser?{
        return mAuth.currentUser
    }

    fun firebaseAuthWithGoogle(acct: GoogleSignInAccount?) {
        acct?.let {
            Log.d("lologin", "firebaseAuthWithGoogle:" + acct.id!!)

            val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
            mAuth.signInWithCredential(credential).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    loginSuccessString.value = "success"
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("lologin", "signInWithCredential:success")
                    //val user = mAuth.currentUser
                    //updateUI(user)
                } else {
                    loginSuccessString.value = task.exception?.message
                    // If sign in fails, display a message to the user.
                    //Log.w(TAG, "signInWithCredential:failure", task.getException())
                    //Snackbar.make(findViewById(R.id.main_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT)
                    //    .show()
                    //updateUI(null)
                }

                // ...
            }
        }
    }
}
