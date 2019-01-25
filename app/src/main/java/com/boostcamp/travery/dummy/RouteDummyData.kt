package com.boostcamp.travery.dummy

import com.boostcamp.travery.data.model.Route
import java.util.*

class RouteDummyData {
    companion object {
        fun getData(): List<Route> {
            val data = ArrayList<Route>()

            for (i in 0..10) {
                data.add(
                    Route(
                        i,
                        "부스트 캠프 안드로이드조",
                        "여기 해시태그 자리가 아니었나?",
                        "부스트캠프",
                        System.currentTimeMillis() - 10000 * i,
                            System.currentTimeMillis(),
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