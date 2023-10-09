package au.edu.uq.deco3801.nullpointerexception.geospray.model

import androidx.annotation.DrawableRes

/**
 * [Artwork] is the data class to represent the imageResourceId of an artwork
 */
data class Artwork(
    @DrawableRes val imageResourceId: Int
)