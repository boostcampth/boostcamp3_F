package com.boostcamp.travery.dummy

import com.boostcamp.travery.data.model.Activity
import com.boostcamp.travery.data.model.Route
import java.util.*

class ActivityDummyData {
    companion object {
        fun getData(): List<Activity> {
            val data = ArrayList<Activity>()

            for (i in 0..10) {
                data.add(
                    Activity(
                        i,
                        "부스트 캠프 안드로이드조",
                        "여기 해시태그 자리가 아니었나?",
                        Date(System.currentTimeMillis()),
                        "해시태그!!!!!",
                        "",
                        "",
                        0,
                        0,
                        0
                    )
                )
            }

            return data
        }
    }
}