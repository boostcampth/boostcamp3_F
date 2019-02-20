package com.boostcamp.travery

object Constants {
    const val UPDATE_INTERVAL_MS: Long = 2500  // 1초
    const val FASTEST_UPDATE_INTERVAL_MS: Long = 1500 //

    const val FIREBASE_ITEM: String = "userAction_item"

    const val EXTRA_COURSE_LOCATION_LIST = "ROUTE_LOCATION_LIST"
    const val EXTRA_COURSE = "COURSE"
    const val EXTRA_LATITUDE = "LATITUDE"
    const val EXTRA_LONGITUDE = "LONGITUDE"
    const val EXTRA_COURSE_CODE = "COURSE_CODE"

    const val EXTRA_USER_ID="USER_ID"
    const val EXTRA_USER_ACTION = "USER_ACTION"
    const val EXTRA_USER_ACTION_DATE = "USER_ACTION_DATE"
    const val EXTRA_USERACTION_STATE = "USER_ACTION_STATE"
    const val EDIT_STATE = "EDIT"
    const val DELETE_STATE = "DELETE"

    const val SIGN_IN_GOOGLE = 900
    const val SIGN_IN_SERVER = 22

    const val REQUEST_CODE_USERACTION_REMOVE = 1003
    const val REQUEST_CODE_USERACTION = 1004
    const val REQUEST_CODE_LOGIN = 701
    const val REQUEST_CODE_USERACTION_EDIT = 1234

    const val EDIT_MODE = "edit"

    const val GPS_ENABLE_REQUEST_CODE = 2001

    const val PREF_NAME_LOGIN = "loginPref"
    const val PREF_USER_ID = "userID"
    const val PREF_USER_NAME = "userName"
    const val PREF_USER_IMAGE = "userImage"
    const val PREF_AUTO_UPLOAD = "autoUpload"
}