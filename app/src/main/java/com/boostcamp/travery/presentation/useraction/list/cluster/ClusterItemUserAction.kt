package com.boostcamp.travery.presentation.useraction.list.cluster

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem

data class ClusterItemUserAction(val name: String,
                                 val image: String,
                                 val location: LatLng) : ClusterItem {

    override fun getSnippet() = null

    override fun getTitle() = name

    override fun getPosition() = location
}