package com.boostcamp.travery.data.model

import com.boostcamp.travery.Constants

data class Guide(
        var image: String
) : BaseItem {
    override fun getType(): Int {
        return Constants.TYPE_GIUDELINE
    }
}