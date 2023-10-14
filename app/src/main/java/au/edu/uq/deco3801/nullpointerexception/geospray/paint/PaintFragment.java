package au.edu.uq.deco3801.nullpointerexception.geospray.paint;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.ContentValues;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import au.edu.uq.deco3801.nullpointerexception.geospray.MainActivity;
import au.edu.uq.deco3801.nullpointerexception.geospray.R;
import au.edu.uq.deco3801.nullpointerexception.geospray.fragments.CreateUploadFragment;
import petrov.kristiyan.colorpicker.ColorPicker;

public class PaintFragment extends Fragment {

    // creating the object of type DrawView
    // in order to get the reference of the View
    private DrawView paint;

    // creating objects of type button
    private ImageButton save, color, stroke, undo;

    // creating a RangeSlider object, which will
    // help in selecting the width of the Stroke
    private SeekBar rangeSlider;

    private ArrayList<String> hexColors;

    private Boolean brush;

    private Toast currentToast;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.paint, container, false);
        // getting the reference of the views from their ids
        paint = rootView.findViewById(R.id.draw_view);
        rangeSlider = rootView.findViewById(R.id.brush_slider);
        undo = rootView.findViewById(R.id.btn_undo);
        save = rootView.findViewById(R.id.btn_save);
        color = rootView.findViewById(R.id.btn_color);
        stroke = rootView.findViewById(R.id.btn_stroke);
        brush = false;
        hexColors = new ArrayList<>();
        hexColors.add("#2062af");
        hexColors.add("#58AEB7");
        hexColors.add("#F4B528");
        hexColors.add("#DD3E48");
        hexColors.add("#BF89AE");
        hexColors.add("#5C88BE");
        hexColors.add("#59BC10");
        hexColors.add("#E87034");
        hexColors.add("#f84c44");
        hexColors.add("#8c47fb");
        hexColors.add("#51C1EE");
        hexColors.add("#8cc453");
        hexColors.add("#C2987D");
        hexColors.add("#CE7777");
        hexColors.add("#9086BA");
        hexColors.add("#ffffff");
        hexColors.add("#42a5f5");
        hexColors.add("#000000");
        hexColors.add("#b190fc");

        ValueAnimator toAnimate = ValueAnimator.ofObject(new ArgbEvaluator(), Color.rgb(152, 154, 157), Color.rgb(253, 129, 74));
        ValueAnimator endAnimate = ValueAnimator.ofObject(new ArgbEvaluator(), Color.rgb(253, 129, 74), Color.rgb(152, 154, 157));

        // creating a OnClickListener for each button,
        // to perform certain actions

        // the undo button will remove the most
        // recent stroke from the canvas
        undo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toAnimate.setDuration(100);
                toAnimate.addUpdateListener(valueAnimator -> undo.setColorFilter((int) valueAnimator.getAnimatedValue()));
                toAnimate.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        endAnimate.setDuration(400);
                        endAnimate.addUpdateListener(valueAnimator -> undo.setColorFilter((int) valueAnimator.getAnimatedValue()));
                        endAnimate.start();
                    }
                });
                toAnimate.start();
                paint.undo();

            }
        });

        // the save button will save the current
        // canvas which is actually a bitmap
        // in form of PNG, in the storage
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showToast("Saving...");

                toAnimate.setDuration(100);
                toAnimate.addUpdateListener(valueAnimator -> save.setColorFilter((int) valueAnimator.getAnimatedValue()));
                toAnimate.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        endAnimate.setDuration(400);
                        endAnimate.addUpdateListener(valueAnimator -> save.setColorFilter((int) valueAnimator.getAnimatedValue()));
                        endAnimate.start();
                    }
                });
                toAnimate.start();

                // getting the bitmap from DrawView class
                Bitmap bmp = paint.save();

                // opening a OutputStream to write into the file
                OutputStream imageOutStream = null;

                ContentValues cv = new ContentValues();

                // name of the file
                cv.put(MediaStore.Images.Media.DISPLAY_NAME, "drawing.png");

                // type of the file
                cv.put(MediaStore.Images.Media.MIME_TYPE, "image/png");

                // location of the file to be saved
                cv.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);

                // get the Uri of the file which is to be created in the storage
                Uri uri = requireContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv);
                try {
                    // open the output stream with the above uri
                    imageOutStream = requireContext().getContentResolver().openOutputStream(uri);

                    // this method writes the files in storage
                    bmp.compress(Bitmap.CompressFormat.PNG, 100, imageOutStream);

                    // close the output stream after use
                    imageOutStream.close();

                    showToast("Image saved.");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // Send image to CreateUploadFragment
                Bundle args = new Bundle();

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);

                args.putByteArray("image", stream.toByteArray());

                CreateUploadFragment createUploadFragment = new CreateUploadFragment();
                createUploadFragment.setArguments(args);

                ((MainActivity) requireActivity()).replaceFrag(createUploadFragment);
            }
        });

        // the color button will allow the user
        // to select the color of his brush
        color.setOnClickListener(view -> {
            color.setColorFilter(Color.rgb(253, 129, 74));
            final ColorPicker colorPicker = new ColorPicker(requireActivity());
            colorPicker.setOnFastChooseColorListener(new ColorPicker.OnFastChooseColorListener() {
                        @Override
                        public void setOnFastChooseColorListener(int position, int color2) {
                            // get the integer value of color
                            // selected from the dialog box and
                            // set it as the stroke color
                            paint.setColor(color2);
                            color.setColorFilter(null);
                        }
                        @Override
                        public void onCancel() {
                            color.setColorFilter(null);
                            colorPicker.dismissDialog();
                        }
                    })
                    // set the number of color columns
                    // you want  to show in dialog.
                    .setColumns(5)
                    // set a default color selected
                    // in the dialog
                    .setDefaultColorButton(Color.parseColor("#000000"))
                    .setRoundColorButton(true)
                    .setColors(hexColors)
                    .show();

        });
        // the button will toggle the visibility of the RangeBar/RangeSlider
        stroke.setOnClickListener(view -> {
            if (rangeSlider.getVisibility() == View.VISIBLE) {
                rangeSlider.setVisibility(View.GONE);
                stroke.setColorFilter(Color.rgb(152, 154, 157));
            } else {
                rangeSlider.setVisibility(View.VISIBLE);
                stroke.setColorFilter(Color.rgb(253, 129, 74));
            }

        });

        // set the range of the RangeSlider
//        rangeSlider.setValueFrom(0.0f);
//        rangeSlider.setValueTo(100.0f);
        rangeSlider.setProgress(20);

        // adding a OnChangeListener which will
        // change the stroke width
        // as soon as the user slides the slider
//        rangeSlider.addOnChangeListener(new RangeSlider.OnChangeListener() {
//            @Override
//            public void onValueChange(@NonNull RangeSlider slider, float value, boolean fromUser) {
//                paint.setStrokeWidth((int) value);
//            }
//        });
        rangeSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                paint.setStrokeWidth(i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        // pass the height and width of the custom view
        // to the init method of the DrawView object
        ViewTreeObserver vto = paint.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                paint.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int width = paint.getMeasuredWidth();
                int height = paint.getMeasuredHeight();
                paint.init(height, width);
            }
        });
        return rootView;
    }

    private void showToast(String message) {
        if (currentToast != null) {
            currentToast.cancel();
        }

        currentToast = Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT);
        currentToast.show();
    }
}
