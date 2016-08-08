package com.example.macos.information;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.macos.duan.R;
import com.example.macos.utilities.CustomFragment;

/**
 * Created by devil2010 on 8/8/16.
 */
public class FragmentViewImageInformation extends CustomFragment {
    private View rootView;
    private ImageView img;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.image_content_layout, container, false);
        initLayout();

        return rootView;
    }

    private void initLayout(){
        img = (ImageView) rootView.findViewById(R.id.img);
    }
}
