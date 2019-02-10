package com.boostcamp.travery.eventbus

import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

object EventBus {
    private val eventBus by lazy {
        PublishSubject.create<Any>()
    }

    fun sendEvent(event: Any) {
        eventBus.onNext(event)
    }

    fun getEvents(): Observable<Any> = eventBus
}