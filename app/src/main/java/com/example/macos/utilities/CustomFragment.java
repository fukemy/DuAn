package com.example.macos.utilities;

import android.support.v4.app.Fragment;
import android.view.View;

import com.example.macos.interfaces.iListWork;

/**
 * Created by macos on 6/14/16.
 */
public class CustomFragment extends Fragment {

    private View rootView;
    private iListWork swapInterface;
    private boolean IS_NEXT_PAGE;

    public void setNextPage(boolean b){
        IS_NEXT_PAGE = b;
    }

    public void setInterface(iListWork swapInterface) {
        this.swapInterface = swapInterface;
    }
}
