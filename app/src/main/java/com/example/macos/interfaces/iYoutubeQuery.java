package com.example.macos.interfaces;


import com.example.macos.entities.EnVideoItem;

import java.util.List;

/**
 * Created by Microsoft on 12/5/16.
 */

public interface iYoutubeQuery {
    public void onSuccess(List<EnVideoItem> videoList);
}
