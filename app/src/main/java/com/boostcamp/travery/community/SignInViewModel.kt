package com.boostcamp.travery.community

import android.app.Application
import com.boostcamp.travery.base.BaseViewModel


class SignInViewModel(application: Application) : BaseViewModel(application) {
    private var userName = ""
        get() = if (field.isEmpty()) "empty" else field

    fun onClick() {
        //TODO 가입하기
    }

    fun onTextChange(title: CharSequence) {
        this.userName = title.toString()
    }
}
