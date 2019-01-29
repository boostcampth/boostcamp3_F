package com.boostcamp.travery.dummy

import com.boostcamp.travery.data.model.Course
import java.util.*

class CourseDummyData {
    companion object {
        fun getData(): List<Course> {
            val data = ArrayList<Course>()

            for (i in 0..10) {
                data.add(
                    Course(
                        "부스트 캠프 안드로이드조",
                        "여기 해시태그 자리가 아니었나?",
                        "부스트캠프",
                            System.currentTimeMillis() - 1000 * 60 * 60 * 24 * (i + i % 2),
                            System.currentTimeMillis() - 1000 * 60 * 60 * 24 * (i + i % 2) + 1000 * 60,
                        100,
                        "",
                        ""
                    )
                )
            }

            return data
        }
    }
}