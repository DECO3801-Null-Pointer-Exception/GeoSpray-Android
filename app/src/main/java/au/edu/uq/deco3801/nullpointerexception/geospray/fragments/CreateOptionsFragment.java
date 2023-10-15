package au.edu.uq.deco3801.nullpointerexception.geospray.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import au.edu.uq.deco3801.nullpointerexception.geospray.MainActivity;
import au.edu.uq.deco3801.nullpointerexception.geospray.R;
import au.edu.uq.deco3801.nullpointerexception.geospray.paint.PaintFragment;

public class CreateOptionsFragment extends Fragment {
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.create_page_options_fragment, container, false);

        ImageButton backButton = rootView.findViewById(R.id.create_option_back);
        backButton.setOnClickListener(v -> getParentFragmentManager().popBackStack());

        ImageButton uploadButton = rootView.findViewById(R.id.create_upload);
        uploadButton.setOnClickListener(v -> ((MainActivity) requireActivity()).replaceFrag(new CreateUploadFragment()));

        ImageButton createButton = rootView.findViewById(R.id.create_create);
        createButton.setOnClickListener(v -> ((MainActivity) requireActivity()).replaceFrag(new PaintFragment()));

        return rootView;
    }
}
