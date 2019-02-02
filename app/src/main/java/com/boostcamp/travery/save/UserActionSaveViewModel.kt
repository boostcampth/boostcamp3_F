package com.boostcamp.travery.save

import android.app.Application
import androidx.databinding.ObservableArrayList
import com.boostcamp.travery.base.BaseViewModel

class UserActionSaveViewModel(application: Application) : BaseViewModel(application) {
    val imageList = ObservableArrayList<UserActionImage>()

    private var contract: UserActionSaveViewModel.Contract? = null

    interface Contract {
        fun saveSelectedImage()
    }

    fun setContract(contract: Contract) {
        this.contract = contract
    }

    fun saveUserAction() {
        // DB에 저장
    }

    fun onAddItemClick() {
        contract?.saveSelectedImage()
    }
}