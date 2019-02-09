package com.boostcamp.travery.save

import android.app.Application
import androidx.databinding.ObservableArrayList
import androidx.lifecycle.MutableLiveData
import com.boostcamp.travery.base.BaseViewModel
import com.boostcamp.travery.data.model.UserAction
import io.reactivex.schedulers.Schedulers
import java.util.*
import kotlin.collections.ArrayList

class UserActionSaveViewModel(application: Application) : BaseViewModel(application) {
    val imageList = ObservableArrayList<UserActionImage>()

    private var title = ""
        get() = if (field.isEmpty()) "empty" else field
    private var content = ""
        get() = if (field.isEmpty()) "empty" else field

    val hashTag = MutableLiveData<String>()
    private val hashTagList = ArrayList<String>()

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
                        listToString(hashTagList, ' '),
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
        val count = body.count()
        if (count > 0 && body[0] == '#') { // # 으로 시작할 경우 해시태그
            // 마지막 문자가 ' '으로 끝날 경우, 해당 해시태그를 observe 하는 액티비티에게 변경사항 알림
            if (body[count - 1] == ' ' || body[count - 1] == '\n') {
                hashTag.value = body.substring(0, count - 1).also {
                    hashTagList.add(it)
                }
            }
        }
    }

    fun removeHashTag(hashTag: String) {
        hashTagList.remove(hashTag)
    }

    /**
     * list("#안녕","#하세요","#반가워요") -> "#안녕 #하세요 #반가워요"
     */
    private fun listToString(list: List<String>, divider: Char): String {
        return list.fold("") { acc, item ->
            if (acc.isEmpty()) item else "$acc$divider$item"
        }
    }
}