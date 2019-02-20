package com.boostcamp.travery.useraction.save

import android.app.Application
import android.location.Geocoder
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.boostcamp.travery.Injection
import com.boostcamp.travery.MyApplication
import com.boostcamp.travery.R
import com.boostcamp.travery.base.BaseViewModel
import com.boostcamp.travery.data.NewsFeedRepository
import com.boostcamp.travery.data.model.UserAction
import com.boostcamp.travery.eventbus.EventBus
import com.boostcamp.travery.utils.ImageUtils
import com.boostcamp.travery.utils.toLatLng
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.internal.it
import io.reactivex.Observable.just
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import org.json.JSONArray
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class UserActionSaveViewModel(application: Application) : BaseViewModel(application) {
    private val userActionRepository = Injection.provideCourseRepository(application)
    private val newsFeedRepository = NewsFeedRepository.getInstance()

    val userAction = ObservableField<UserAction>()

    val imageList = ObservableArrayList<UserActionImage>()
    val hashTagList = ObservableArrayList<String>()
    val psHashTag = PublishSubject.create<String>()

    val address = ObservableField<String>()
    private val geoCoder = Geocoder(application)
    private var location = getLastKnownLocation()?.toLatLng()

    fun getLocation() = location

    private var title = ""
        get() = if (field.isEmpty()) "empty" else field
    private var content = ""
        get() = if (field.isEmpty()) "empty" else field

    fun setUserAction(userAction: UserAction) {
        this.userAction.set(userAction)
        setAddress(userAction.latitude, userAction.longitude)

        imageList.clear()
        if (userAction.subImage.isNotEmpty()) {
            val jsonList = JSONArray(userAction.subImage)
            for (i in 0 until jsonList.length()) {
                imageList.add(UserActionImage(jsonList[i].toString()))
            }
        }

        hashTagList.clear()
        if (userAction.hashTag.isNotEmpty()) {
            parseHashTag(userAction.hashTag).forEach {
                psHashTag.onNext(it)
                hashTagList.add(it)
            }
        }

        this.title = userAction.title
        this.content = userAction.body
    }

    fun setAddress(latitude: Double, longitude: Double) {
        location = LatLng(latitude, longitude)

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
            address.set(it)
        })

    }

    fun saveUserAction(): UserAction? {
        val result = parseImagesToJsonArray()

        val userAction = this.userAction.get()?.apply {
            title = this@UserActionSaveViewModel.title
            body = this@UserActionSaveViewModel.content
            date = Date(System.currentTimeMillis())
            hashTag = listToString(hashTagList, ' ')
            mainImage = if (result.length() > 0) result.getString(0) else ""
            subImage = result.toString()
            latitude = location?.latitude ?: 0.0
            longitude = location?.longitude ?: 0.0
            courseCode = if (this.courseCode == 0L) null else this.courseCode
            address = this@UserActionSaveViewModel.address.get() ?: " "
        }

        userAction?.run {
            addDisposable(
                    userActionRepository.saveUserAction(userAction).subscribeOn(Schedulers.io()).subscribe()
            )
        }

        //TODO 서버로 전송하는 부분 주석처리 해놈 설정시에만 보낼수 있도록 추후 변경
//        addDisposable(newsFeedRepository.uploadFeed(userAction, "temp").subscribe({
//            Log.e("TEST", it.message)
//        }, { Log.e("TEST", it.message) }))

        return userAction
    }

    fun updateUserAction(): UserAction? {
        val result = parseImagesToJsonArray()
        val userAction = this.userAction.get()?.apply {
            title = this@UserActionSaveViewModel.title
            body = this@UserActionSaveViewModel.content
            hashTag = listToString(hashTagList, ' ')
            mainImage = if (result.length() > 0) result.getString(0) else ""
            subImage = result.toString()
            address = this@UserActionSaveViewModel.address.get() ?: " "
        }

        userAction?.let { user ->
            addDisposable(userActionRepository.updateUserAction(user)
                    .subscribeOn(Schedulers.io())
                    .subscribe {
                        EventBus.sendEvent(UserActionUpdateEvent(user))
                    })
        }

        return userAction
    }

    fun onRemoveItemClick(item: UserActionImage) {
        imageList.remove(item)
        if (imageList.isEmpty()) {
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
                psHashTag.onNext(body.substring(0, count - 1).also { hashTagList.add(it) })
            }
        }
    }

    private fun parseImagesToJsonArray(): JSONArray {
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
        return result
    }

    fun removeHashTag(hashTag: String) {
        hashTagList.remove(hashTag)
    }

    private fun parseHashTag(list: String?): List<String> {
        return list?.split(" ")?.map { if (it.startsWith('#')) it else "#$it" } ?: emptyList()
    }

    /**
     * list("#안녕","#하세요","#반가워요") -> "#안녕 #하세요 #반가워요"
     */
    private fun listToString(list: List<String>, divider: Char): String {
        return list.fold("") { acc, item ->
            if (!item.startsWith("#")) {
                if (acc.isEmpty()) "#$item" else "$acc$divider#$item"
            } else {
                if (acc.isEmpty()) item else "$acc$divider$item"
            }
        }
    }
}

data class UserActionUpdateEvent(val userAction: UserAction)