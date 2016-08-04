package com.example.macos.fragment.Input;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.macos.duan.R;
import com.example.macos.entities.EnInputItem;
import com.example.macos.interfaces.iListWork;
import com.example.macos.utilities.CustomFragment;

/**
 * Created by macos on 6/17/16.
 */
public class FragmentInputSummary extends CustomFragment {
    private View rootView;
    private iListWork swapInterface;
    public void setInterface(iListWork swapInterface) {
        this.swapInterface = swapInterface;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fr_road_surface_summary, container, false);
        initLayout();
        EnInputItem en =  (EnInputItem) getArguments().getSerializable("data");
        return rootView;
    }

    private void initLayout(){

    }
}
