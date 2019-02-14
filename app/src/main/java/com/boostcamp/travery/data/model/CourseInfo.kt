package com.boostcamp.travery.data.model

data class CourseInfo(
    var course: Course?=null,
    var userActionList: ArrayList<UserAction>?=null,
    var timeCodeList: ArrayList<TimeCode>?=null
){

}