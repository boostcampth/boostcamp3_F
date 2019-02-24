package com.boostcamp.travery.presentation.community

import android.app.Application
import android.content.Context
import com.boostcamp.travery.Constants
import com.boostcamp.travery.base.BaseViewModel
import com.boostcamp.travery.data.repository.UserRepository
import com.boostcamp.travery.data.model.User
import com.boostcamp.travery.data.remote.model.MyResponse
import com.boostcamp.travery.utils.ImageUtils
import io.reactivex.Single


class SignInViewModel(application: Application) : BaseViewModel(application) {
    private val userRepository= UserRepository.getInstance()
    private var view:SignInViewModel.View?=null
    private val user=User()

    interface View {
        fun toastMessage(message:String)
        fun returnResult()
        fun onLoading()
        fun offLoading()
    }

    fun setView(view:View){
        this.view=view
    }

    fun onClick() {
        //true : 사용 가능
        if(user.nickname.isEmpty()){
            view?.toastMessage("닉네임을 입력하세요.")
        }else{
            view?.onLoading()
            addDisposable(userRepository.checkNickname(user.nickname)
                    .flatMap { it->if(it){
                        if(!user.image.isNullOrEmpty()){
                            user.image=ImageUtils.createImage(getApplication(), user.image!!).path
                        }
                        userRepository.registerUser(user)
                    }else{
                        Single.just(MyResponse(true,"중복닉네임"))
                    }
                    }.subscribe({
                        view?.offLoading()
                        if(it.error){
                            view?.toastMessage(it.message?:"")
                        }else{
                            savePreferences(user)
                            view?.returnResult()
                        }
                    },{
                        view?.offLoading()
                        view?.toastMessage(it.message?:"")
                    })
            )
        }
    }

    fun onTextChange(title: CharSequence) {
        user.nickname = title.toString()
    }

    fun setUserImage(path:String){
        user.image=path
    }

    fun setUserId(userId:String){
        user.id=userId
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
