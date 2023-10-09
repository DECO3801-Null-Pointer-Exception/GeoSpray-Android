package au.edu.uq.deco3801.nullpointerexception.geospray.data

import au.edu.uq.deco3801.nullpointerexception.geospray.R
import au.edu.uq.deco3801.nullpointerexception.geospray.model.Artwork

/**
 * [Datasource] generates a list of [Artwork]
 */
class Datasource {

    /**
     * Temporary - for now just returns a fixed list of sample images.
     * In future could be changed to fetch artwork from the database.
     */
    fun loadArtworks(): List<Artwork> {
        return listOf<Artwork>(
            Artwork(R.drawable.image1),
            Artwork(R.drawable.image2),
            Artwork(R.drawable.image3),
            Artwork(R.drawable.image4),
            Artwork(R.drawable.image5),
            Artwork(R.drawable.image6),
            Artwork(R.drawable.image7),
            Artwork(R.drawable.image8),
            Artwork(R.drawable.image9),
            Artwork(R.drawable.image10))
    }
}