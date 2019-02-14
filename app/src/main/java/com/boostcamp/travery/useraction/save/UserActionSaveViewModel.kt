package com.boostcamp.travery.useraction.save

import android.app.Application
import androidx.databinding.ObservableArrayList
import androidx.lifecycle.MutableLiveData
import com.boostcamp.travery.base.BaseViewModel
import com.boostcamp.travery.data.model.UserAction
import io.reactivex.schedulers.Schedulers
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.channels.FileChannel
import java.util.*
import kotlin.collections.ArrayList

class UserActionSaveViewModel(application: Application) : BaseViewModel(application) {
    val imageList = ObservableArrayList<UserActionImage>()

    private val dirPath = application.filesDir

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

        val fileList = createFileList()

        // 사진 경로 리스트 저장. 구분자 : ,
        val result = fileList.subList(1, fileList.size).fold("") { acc, item ->
            if (acc.isEmpty()) item.absolutePath else "$acc,${item.absolutePath}"
        }

        addDisposable(repository.saveUserAction(
                UserAction(title,
                        content,
                        Date(System.currentTimeMillis()),
                        listToString(hashTagList, ' '),
                        fileList[0].absolutePath,
                        result,
                        latitude, longitude,
                        when (courseCode) {
                            0L -> null
                            else -> courseCode
                        })
        ).subscribeOn(Schedulers.io()).subscribe())
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

    private fun copyFile(sourceFile: File, destFile: File) {
        if (!sourceFile.exists()) {
            return
        }

        // 양쪽 채널을 열어서 파일 복제
        val source: FileChannel? = FileInputStream(sourceFile).channel
        val destination: FileChannel? = FileOutputStream(destFile).channel

        source?.apply {
            destination?.transferFrom(this, 0, this.size())
        }?.close()

        destination?.run { close() }
    }

    private fun createFileList(): List<File> {

        // 폴더가 존재하지않을 경우 생성
        if (!dirPath.exists()) {
            dirPath.mkdirs()
        }

        // 이미지리스트 개수 만큼 내부 저장소에 복사할 File 객체 생성
        // 파일 이름은 현재시간.jpg
        val fileList = imageList.subList(0, imageList.size - 1).map {
            val name = it.filePath.split("/").let { list ->
                list[list.size - 1]
            }
            File(dirPath, name)
        }

        // 파일 복사
        fileList.forEachIndexed { i, file ->
            try {
                if (!file.exists()) {
                    file.createNewFile()
                    copyFile(File(imageList[i].filePath), file)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        return fileList
    }
}