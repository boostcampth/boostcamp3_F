package com.boostcamp.travery.useraction.save

import android.app.Application
import android.location.Geocoder
import androidx.databinding.ObservableArrayList
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.boostcamp.travery.Injection
import com.boostcamp.travery.base.BaseViewModel
import com.boostcamp.travery.data.NewsFeedRepository
import com.boostcamp.travery.data.model.UserAction
import com.boostcamp.travery.utils.ImageUtils
import io.reactivex.Observable.just
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.json.JSONArray
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class UserActionSaveViewModel(application: Application) : BaseViewModel(application) {
    private val userActionRepository = Injection.provideCourseRepository(application)
    private val newsFeedRepository = NewsFeedRepository.getInstance()

    val imageList = ObservableArrayList<UserActionImage>()
    private val hashTag = MutableLiveData<String>()
    private val hashTagList = ArrayList<String>()

    private val geoCoder = Geocoder(application)
    private val address = MutableLiveData<String>()

    fun getHashTag(): LiveData<String> = hashTag

    fun getHashTagCount() = hashTagList.size

    fun getAddress(): LiveData<String> = address

    private var title = ""
        get() = if (field.isEmpty()) "empty" else field
    private var content = ""
        get() = if (field.isEmpty()) "empty" else field

    private var view: UserActionSaveViewModel.View? = null

    interface View {
        fun saveSelectedImage()
        fun imageListEmpty()
    }

    fun setView(view: View) {
        this.view = view
    }

    fun setAddress(latitude: Double, longitude: Double) {
        // Geocode 변환
        addDisposable(just(
                try {
                    geoCoder.getFromLocation(latitude, longitude, 1)[0].getAddressLine(0)
                } catch (e: Exception) {
                    "오류"
                }).map {
            val split = it.split(" ")
            split.subList(1, split.size).fold("") { acc, s -> "$acc $s" }
        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe {
            address.setValue(it)
        })
    }

    fun saveUserAction(latitude: Double, longitude: Double, courseCode: Long) {
        val fileList = ArrayList<File>()
        for (image in imageList) {
            if (!image.filePath.isEmpty()) {
                fileList.add(ImageUtils.createImage(getApplication(), image.filePath))
            }
        }

        val result = JSONArray()
        for (file in fileList) {
            result.put(file.path)
        }

        val userAction = UserAction(
                title,
                content,
                Date(System.currentTimeMillis()),
                listToString(hashTagList, ' '),
                if (result.length() > 0) result.getString(0) else "",
                result.toString(),
                latitude, longitude,
                when (courseCode) {
                    0L -> null
                    else -> courseCode
                }
        )
        addDisposable(
                userActionRepository.saveUserAction(userAction).subscribeOn(Schedulers.io()).subscribe()
        )

        //TODO 서버로 전송하는 부분 주석처리 해놈 설정시에만 보낼수 있도록 추후 변경
//        addDisposable(newsFeedRepository.uploadFeed(userAction, "temp").subscribe({
//            Log.e("TEST", it.message)
//        }, { Log.e("TEST", it.message) }))
    }

    fun onRemoveItemClick(item: UserActionImage) {
        imageList.remove(item)
        if (imageList.isEmpty()) {
            view?.imageListEmpty()
        }
    }

    fun onTitleChange(title: CharSequence) {
        this.title = title.toString()
    }

    fun onContentChange(body: CharSequence) {
        this.content = body.toString()
    }

    fun onHashTagChange(body: CharSequence) {
        val count = body.count()
        if (count > 0) {
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