package au.edu.uq.deco3801.nullpointerexception.geospray.paint;

import android.graphics.Path;

import android.graphics.Path;

/**
 * Class representing a stroke on a canvas.
 *
 * Code adapted from geeksforgeeks, available at:
 * <a href="https://www.geeksforgeeks.org/how-to-create-a-paint-application-in-android/">
 *     https://www.geeksforgeeks.org/how-to-create-a-paint-application-in-android</a>
 */
public class Stroke {
    // color of the stroke
    public int color;

    // width of the stroke
    public int strokeWidth;

    // a Path object to
    // represent the path drawn
    public Path path;

    // constructor to initialise the attributes
    public Stroke(int color, int strokeWidth, Path path) {
        this.color = color;
        this.strokeWidth = strokeWidth;
        this.path = path;
    }
}
