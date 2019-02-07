package com.boostcamp.travery.save

import android.app.Application
import androidx.databinding.ObservableArrayList
import com.boostcamp.travery.base.BaseViewModel
import com.boostcamp.travery.data.model.UserAction
import io.reactivex.schedulers.Schedulers
import java.util.*

class UserActionSaveViewModel(application: Application) : BaseViewModel(application) {
    val imageList = ObservableArrayList<UserActionImage>()

    private var title = ""
        get() = if (field.isEmpty()) "empty" else field
    private var content = ""
        get() = if (field.isEmpty()) "empty" else field
    private var hashTag = ""
        get() = if (field.isEmpty()) "empty" else field

    private var contract: UserActionSaveViewModel.Contract? = null

    interface Contract {
        fun saveSelectedImage()
    }

    fun setContract(contract: Contract) {
        this.contract = contract
    }

    fun saveUserAction(latitude: Double, longitude: Double, courseCode: Long) {
        // 사진 경로 리스트 저장. 구분자 : ,

        val result = imageList.subList(0, imageList.size - 1).fold("") { acc, item ->
            if (acc.isEmpty()) item.filePath else "$acc,${item.filePath}"
        }

        repository.saveUserAction(
                UserAction(title,
                        content,
                        Date(System.currentTimeMillis()),
                        hashTag,
                        imageList[0].filePath,
                        result,
                        latitude, longitude,
                        when (courseCode) {
                            0L -> null
                            else -> courseCode
                        })
        ).subscribeOn(Schedulers.io()).subscribe().also { addDisposable(it) }
    }

    fun onAddItemClick() {
        contract?.saveSelectedImage()
    }

    fun onTitleChange(title: CharSequence) {
        this.title = title.toString()
    }

    fun onContentChange(body: CharSequence) {
        this.content = body.toString()
    }

    fun onHashTagChange(body: CharSequence) {
        this.hashTag = body.toString()
    }
}