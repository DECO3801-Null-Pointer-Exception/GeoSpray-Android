package au.edu.uq.deco3801.nullpointerexception.geospray;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class PreviewFragment extends Fragment {
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.preview_fragment, container, false);

        BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(rootView.findViewById(R.id.bottom_sheet));
        // Convert 100 dp to px
        behavior.setPeekHeight((int) (100 * getResources().getDisplayMetrics().density));
        behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        ImageView imageView = rootView.findViewById(R.id.preview_image);

        return rootView;
    }
}
