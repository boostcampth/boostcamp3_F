package com.boostcamp.travery.community

import android.app.Application
import android.content.Context
import com.boostcamp.travery.base.BaseViewModel
import com.google.firebase.auth.FirebaseAuth
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.boostcamp.travery.Constants
import com.google.firebase.auth.GoogleAuthProvider
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseUser


class LoginViewModel(application: Application) : BaseViewModel(application) {
    private val mAuth by lazy {
        FirebaseAuth.getInstance()
    }

    val loginSuccessString = MutableLiveData<String>()

    fun getCurrentUser(): FirebaseUser? {
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
                    val user = mAuth.currentUser
                    user?.let { _user-> savePreferences(_user.uid, _user.displayName?:"") }

                    //TODO user.uid, user?.photoUrl
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

    private fun savePreferences(userID : String, userName: String) {
        val pref = getApplication<Application>().getSharedPreferences(Constants.PREF_NAME_LOGIN, Context.MODE_PRIVATE)
        val editor = pref.edit()
        editor.putString(Constants.PREF_USER_ID, userID)
        editor.putString(Constants.PREF_USER_NAME, userName)
        editor.apply()
    }
}
