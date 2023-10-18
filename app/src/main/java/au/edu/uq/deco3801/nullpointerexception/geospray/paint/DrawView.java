package au.edu.uq.deco3801.nullpointerexception.geospray.paint;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

/**
 * Class representing a view the user is able to draw within.
 * <br/>
 * Code adapted from geeksforgeeks, available at:
 * <a href="https://www.geeksforgeeks.org/how-to-create-a-paint-application-in-android/">
 *     https://www.geeksforgeeks.org/how-to-create-a-paint-application-in-android</a>
 * <br/>
 * Accessed on October 13.
 */
public class DrawView extends View {
    private static final float TOUCH_TOLERANCE = 4;
    private float mX, mY;
    private Path mPath;

    // the Paint class encapsulates the color
    // and style information about
    // how to draw the geometries,text and bitmaps
    private Paint mPaint;

    // ArrayList to store all the strokes
    // drawn by the user on the Canvas
    private ArrayList<Stroke> paths = new ArrayList<>();
    private int currentColor;
    private int strokeWidth;
    private int width1;
    private int height1;
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Paint mBitmapPaint = new Paint(Paint.DITHER_FLAG);

    /**
     * Constructor to initialise a View without attributes.
     *
     * @param context The Context of this View.
     */
    public DrawView(Context context) {
        this(context, null);
    }

    /**
     * Constructor to initialise a View with attributes.
     *
     * @param context The Context of this View.
     * @param attrs The attributes of this View.
     */
    public DrawView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint();

        // the below methods smoothens
        // the drawings of the user
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(Color.GREEN);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);

        // 0xff=255 in decimal
        mPaint.setAlpha(0xff);

    }

    /**
     * Instantiate a bitmap and object.
     *
     * @param height The height of the bitmap.
     * @param width The width of the bitmap.
     */
    public void init(int height, int width) {
        width1 = width;
        height1 = height;

        mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);

        // set an initial color of the brush
        currentColor = Color.BLACK;

        // set an initial brush size
        strokeWidth = 20;
    }

    /**
     * Set the colour of the stroke.
     *
     * @param color The colour of the stroke.
     */
    public void setColor(int color) {
        currentColor = color;
    }

    /**
     * Set the width of the stroke.
     *
     * @param width The width of the stroke.
     */
    public void setStrokeWidth(int width) {
        strokeWidth = width;
    }

    /**
     * Undoes the most recent operation.
     */
    public void undo() {
        // check whether the List is empty or not
        // if empty, the remove method will return an error
        if (paths.size() != 0) {
            paths.remove(paths.size() - 1);
            invalidate();
        }
    }

    /**
     * Returns the current bitmap from the canvas.
     *
     * @return The bitmap from the canvas.
     */
    public Bitmap save() {
        Paint mbp = new Paint(Paint.DITHER_FLAG);
        Bitmap mb = Bitmap.createBitmap(width1, height1, Bitmap.Config.ARGB_8888);

        Canvas c = new Canvas(mb);
        c.drawColor(Color.TRANSPARENT);

        for (Stroke fp : paths) {
            mPaint.setColor(fp.color);
            mPaint.setStrokeWidth(fp.strokeWidth);
            c.drawPath(fp.path, mPaint);
        }

        c.drawBitmap(mb, 0, 0, mbp);

        return mb;
    }

    // this is the main method where
    // the actual drawing takes place
    @Override
    protected void onDraw(Canvas canvas) {
        // save the current state of the canvas before,
        // to draw the background of the canvas
        canvas.save();

        // DEFAULT color of the canvas
        int backgroundColor = Color.WHITE;
        mCanvas.drawColor(backgroundColor);

        // now, we iterate over the list of paths
        // and draw each path on the canvas
        for (Stroke fp : paths) {
            mPaint.setColor(fp.color);
            mPaint.setStrokeWidth(fp.strokeWidth);
            mCanvas.drawPath(fp.path, mPaint);
        }
        canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
        canvas.restore();
    }

    /**
     * Handles the touch of the user.
     *
     * @param x The x coordinate of the touch.
     * @param y The y coordinate of the touch.
     */
    private void touchStart(float x, float y) {
        // firstly, we create a new Stroke
        // and add it to the paths list
        mPath = new Path();
        Stroke fp = new Stroke(currentColor, strokeWidth, mPath);
        paths.add(fp);

        // finally remove any curve
        // or line from the path
        mPath.reset();

        // this methods sets the starting
        // point of the line being drawn
        mPath.moveTo(x, y);

        // we save the current
        // coordinates of the finger
        mX = x;
        mY = y;
    }

    /**
     * Handles when the user moves their finger.
     *
     * @param x The final x coordinate of the movement.
     * @param y The final y coordinate of the movement.
     */
    private void touchMove(float x, float y) {
        // in this method we check
        // if the move of finger on the
        // screen is greater than the
        // Tolerance we have previously defined,
        // then we call the quadTo() method which
        // actually smooths the turns we create,
        // by calculating the mean position between
        // the previous position and current position
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);

        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            mX = x;
            mY = y;
        }
    }

    /**
     * Handles when the user releases their finger from the canvas.
     */
    private void touchUp() {
        // at the end, we call the lineTo method
        // which simply draws the line until
        // the end position
        mPath.lineTo(mX, mY);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // the onTouchEvent() method provides us with
        // the information about the type of motion
        // which has been taken place, and according
        // to that we call our desired methods
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchStart(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                touchMove(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                touchUp();
                invalidate();
                break;
        }

        return true;
    }
}