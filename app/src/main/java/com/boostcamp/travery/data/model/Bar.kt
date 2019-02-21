package com.boostcamp.travery.data.model

data class Bar(
        var location: Int,
        var title: String
) : BaseItem {
    override fun getType(): Int {
        return location
    }
}