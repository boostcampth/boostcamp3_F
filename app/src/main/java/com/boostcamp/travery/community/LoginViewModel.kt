package com.boostcamp.travery.community

import android.app.Application
import android.content.Context
import com.boostcamp.travery.base.BaseViewModel
import com.google.firebase.auth.FirebaseAuth
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.boostcamp.travery.Constants
import com.boostcamp.travery.data.UserRepository
import com.boostcamp.travery.data.model.User
import com.google.firebase.auth.GoogleAuthProvider
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseUser


class LoginViewModel(application: Application) : BaseViewModel(application) {
    private val mAuth by lazy {
        FirebaseAuth.getInstance()
    }
    private val userRepository = UserRepository.getInstance()

    val loginSuccessString = MutableLiveData<String>()
    var userId=""

    fun getCurrentUser(): FirebaseUser? {
        return mAuth.currentUser
    }

    fun firebaseAuthWithGoogle(acct: GoogleSignInAccount?) {
        acct?.let {

            val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
            mAuth.signInWithCredential(credential).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = mAuth.currentUser
                    user?.let { _user ->
                        //_user.uid 이게 서버에 있는지 체크해서 오기
                        addDisposable(userRepository.registeredUserId(_user.uid)
                                .subscribe({ remoteUser ->
                                    if (!remoteUser.id.isEmpty()) {
                                        savePreferences(remoteUser)
                                        loginSuccessString.value = "success"
                                    }else{
                                        userId=_user.uid
                                        loginSuccessString.value = "nonexistent"
                                    }
                                }, {t->
                                    loginSuccessString.value = t.message
                                }))
                    }
                } else {
                    loginSuccessString.value = task.exception?.message
                }
            }
        }
    }

    private fun savePreferences(user: User) {
        val pref = getApplication<Application>().getSharedPreferences(Constants.PREF_NAME_LOGIN, Context.MODE_PRIVATE)
        val editor = pref.edit()
        editor.putString(Constants.PREF_USER_ID, user.id)
        editor.putString(Constants.PREF_USER_NAME, user.nickname)
        editor.putString(Constants.PREF_USER_IMAGE, user.image)
        editor.apply()
    }
}
