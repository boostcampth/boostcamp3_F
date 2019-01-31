package com.boostcamp.travery.base

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.boostcamp.travery.data.AppDataManager
import com.boostcamp.travery.data.local.db.AppDbHelper
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

open class BaseViewModel(application: Application) : AndroidViewModel(application) {
    private val compositeDisposable = CompositeDisposable()

    protected val repository = AppDataManager(AppDbHelper.getInstance(application))

    fun addDisposable(disposable: Disposable) {
        compositeDisposable.add(disposable)
    }

    override fun onCleared() {
        compositeDisposable.clear()
        super.onCleared()
    }
}