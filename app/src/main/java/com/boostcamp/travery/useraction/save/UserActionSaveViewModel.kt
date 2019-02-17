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
import com.boostcamp.travery.utils.NewFileUtils
import io.reactivex.schedulers.Schedulers
import org.json.JSONArray
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class UserActionSaveViewModel(application: Application) : BaseViewModel(application) {
    val imageList = ObservableArrayList<UserActionImage>()


    private val userActionRepository = Injection.provideCourseRepository(application)
    private val newsFeedRepository = NewsFeedRepository.getInstance()

    private val geoCoder = Geocoder(application)
    private var address = MutableLiveData<String>()

    private var title = ""
        get() = if (field.isEmpty()) "empty" else field
    private var content = ""
        get() = if (field.isEmpty()) "empty" else field

    val hashTag = MutableLiveData<String>()
    private val hashTagList = ArrayList<String>()

    private var view: UserActionSaveViewModel.View? = null

    interface View {
        fun saveSelectedImage()
    }

    fun setView(view: View) {
        this.view = view
    }

    fun setAddress(latitude: Double, longitude: Double): LiveData<String> {
        // Geocode 변환
        Thread(Runnable {
            try {
                address.postValue(geoCoder.getFromLocation(latitude, longitude, 1)[0].getAddressLine(0))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }).start()
        return address
    }

    fun saveUserAction(latitude: Double, longitude: Double, courseCode: Long) {
        val fileList = ArrayList<File>()
        for (image in imageList) {
            if (!image.filePath.isNullOrEmpty()) {
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

    fun onAddItemClick() {
        view?.saveSelectedImage()
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