package au.edu.uq.deco3801.nullpointerexception.geospray

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import au.edu.uq.deco3801.nullpointerexception.geospray.adapter.ItemAdapter
import au.edu.uq.deco3801.nullpointerexception.geospray.data.Datasource

/**
 * This class normally allows for setting the list of images into the scrolling part of the
 * home page - however it isn't compatible with the rest of the code since it is an
 * AppCompatActivity instead of a Fragment - and is written in Kotlin instead of Java
 */
class HomeArtworks : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home)

        // Initialize data.
        val myDataset = Datasource().loadArtworks()

        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.adapter = ItemAdapter(this, myDataset)

        // Use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true)
    }
}