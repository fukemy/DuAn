package com.example.macos.fragment.mainscreen;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.macos.activities.AcInput;
import com.example.macos.activities.MainScreen;
import com.example.macos.adapter.categoryAdapter;
import com.example.macos.database.DatabaseHelper;
import com.example.macos.database.Item;
import com.example.macos.duan.R;
import com.example.macos.entities.EnMainCatalogItem;
import com.example.macos.entities.EnWorkList;
import com.example.macos.fragment.Input.FragmentInputItem;
import com.example.macos.interfaces.iGridClick;
import com.example.macos.interfaces.iListWork;
import com.example.macos.libraries.Logger;
import com.example.macos.main.Application;
import com.example.macos.utilities.AnimationControl;
import com.example.macos.utilities.CustomFragment;
import com.example.macos.utilities.FunctionUtils;
import com.example.macos.utilities.GlobalParams;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by macos on 6/16/16.
 */
public class FragmentMainDataScreen extends CustomFragment {
    private RecyclerView recyclerView;
    private StaggeredGridLayoutManager gaggeredGridLayoutManager;
    categoryAdapter adapter;
    private View rootView;
    List<EnMainCatalogItem> list;

    public categoryAdapter getAdapter() {
        return adapter;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.grid_data, container, false);

        initLayout();

        list = new ArrayList<>();
        List<Item> catalogList = DatabaseHelper.getItemList();
        if(catalogList == null || catalogList.size() == 0){

        }else {

            list.add(new EnMainCatalogItem(catalogList.get(0), false, R.mipmap.roads, new FragmentInputItem()));
            list.add(new EnMainCatalogItem(catalogList.get(1), false, R.mipmap.nenduong, new FragmentInputItem()));
            list.add(new EnMainCatalogItem(catalogList.get(2), false, R.mipmap.daiphancach, new FragmentInputItem()));
            list.add(new EnMainCatalogItem(catalogList.get(3), false, R.mipmap.cauchui, new FragmentInputItem()));
            list.add(new EnMainCatalogItem(catalogList.get(4), false, R.mipmap.conghopbanthoatnuoc, new FragmentInputItem()));
            list.add(new EnMainCatalogItem(catalogList.get(5), false, R.mipmap.congtron, new FragmentInputItem()));
            list.add(new EnMainCatalogItem(catalogList.get(6), false, R.mipmap.maidoc, new FragmentInputItem()));
            list.add(new EnMainCatalogItem(catalogList.get(7), false, R.mipmap.tuongchan, new FragmentInputItem()));
            list.add(new EnMainCatalogItem(catalogList.get(8), false, R.mipmap.hangraobaove, new FragmentInputItem()));
            list.add(new EnMainCatalogItem(catalogList.get(9), false, R.mipmap.hethongsang, new FragmentInputItem()));
            list.add(new EnMainCatalogItem(catalogList.get(10), false, R.mipmap.bienbao, new FragmentInputItem()));
            list.add(new EnMainCatalogItem(catalogList.get(11), false, R.mipmap.vachsonduong, new FragmentInputItem()));
            list.add(new EnMainCatalogItem(catalogList.get(12), false, R.mipmap.cot_km, new FragmentInputItem()));
            list.add(new EnMainCatalogItem(catalogList.get(13), false, R.mipmap.cau, new FragmentInputItem()));
            list.add(new EnMainCatalogItem(catalogList.get(14), false, R.mipmap.coctieu, new FragmentInputItem()));
            list.add(new EnMainCatalogItem(catalogList.get(15), false, R.mipmap.hogahothu, new FragmentInputItem()));
            list.add(new EnMainCatalogItem(catalogList.get(16), false, R.mipmap.ranhdocranhbien, new FragmentInputItem()));
            list.add(new EnMainCatalogItem(catalogList.get(17), false, R.mipmap.congdoc, new FragmentInputItem()));

            setData();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            recyclerView.setNestedScrollingEnabled(true);
        }


        return rootView;
    }

    public void setData() {
        adapter = new categoryAdapter(getActivity(), list, new iGridClick() {
            @Override
            public void onClickView(int position, View clickedView) {
                AnimationControl.zoomImageFromThumb(clickedView, list.get(position).getImg(), rootView, new iListWork() {

                            @Override
                            public void goToInputPage(EnMainCatalogItem en) {
                                Logger.error("choose item: " + en.getItem().toString());
                                final Intent in = new Intent(getActivity(), AcInput.class);
                                List<Item> itemList = new ArrayList<>();
                                itemList.add(en.getItem());
                                EnWorkList enWorkLists = new EnWorkList(itemList);
                                in.putExtra(GlobalParams.LIST_WORKING_NAME, enWorkLists);
                                in.putExtra(GlobalParams.ACTION_TYPE, getResources().getString(R.string.road_test));
                                startActivity(in);
                            }
                        },
                        list.get(position));
            }
        });
        recyclerView.setAdapter(adapter);
    }

    private void initLayout() {
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);

        gaggeredGridLayoutManager = new StaggeredGridLayoutManager(3, 1);
        recyclerView.setLayoutManager(gaggeredGridLayoutManager);
    }
}
