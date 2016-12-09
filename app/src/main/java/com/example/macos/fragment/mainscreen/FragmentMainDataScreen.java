package com.example.macos.fragment.mainscreen;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.macos.adapter.categoryAdapter;
import com.example.macos.database.DatabaseHelper;
import com.example.macos.database.Item;
import com.example.macos.duan.R;
import com.example.macos.entities.EnMainCatalogItem;
import com.example.macos.fragment.Input.FragmentInputItem;
import com.example.macos.interfaces.iListWork;
import com.example.macos.libraries.Logger;
import com.example.macos.main.Application;
import com.example.macos.utilities.AnimationControl;
import com.example.macos.utilities.CustomFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by macos on 6/16/16.
 */
public class FragmentMainDataScreen extends CustomFragment {
    GridView gridView;
    categoryAdapter adapter;
    private View rootView;
    private iListWork swapInterface;
    private boolean IS_SELECT_MULTI = false;
    List<EnMainCatalogItem> list;

    public void setInterface(iListWork swapInterface) {
        this.swapInterface = swapInterface;
    }

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


//        list.add(new EnMainCatalogItem(getResources().getString(R.string.road_surface), false, R.mipmap.roads, new FragmentInputItem()));
//        list.add(new EnMainCatalogItem(getResources().getString(R.string.roadbed), false, R.mipmap.nenduong, new FragmentInputItem()));
//        list.add(new EnMainCatalogItem(getResources().getString(R.string.divider), false, R.mipmap.daiphancach, new FragmentInputItem()));
//        list.add(new EnMainCatalogItem(getResources().getString(R.string.underground_drain_people), false, R.mipmap.cauchui, new FragmentInputItem()));
//        list.add(new EnMainCatalogItem(getResources().getString(R.string.drainage_box_culvert), false, R.mipmap.roads, new FragmentInputItem()));
//        list.add(new EnMainCatalogItem(getResources().getString(R.string.culverts), false, R.mipmap.congtron, new FragmentInputItem()));
//        list.add(new EnMainCatalogItem(getResources().getString(R.string.slopes), false, R.mipmap.roads, new FragmentInputItem()));
//        list.add(new EnMainCatalogItem(getResources().getString(R.string.retaining_walls), false, R.mipmap.tuongchan, new FragmentInputItem()));
//        list.add(new EnMainCatalogItem(getResources().getString(R.string.protective_barrier), false, R.mipmap.hangraobaove, new FragmentInputItem()));
//        list.add(new EnMainCatalogItem(getResources().getString(R.string.lighting_Systems), false, R.mipmap.hethongsang, new FragmentInputItem()));
//        list.add(new EnMainCatalogItem(getResources().getString(R.string.notice_board), false, R.mipmap.bienbao, new FragmentInputItem()));
//        list.add(new EnMainCatalogItem(getResources().getString(R.string.road_markings), false, R.mipmap.roads, new FragmentInputItem()));
//        list.add(new EnMainCatalogItem(getResources().getString(R.string.column_Km), false, R.mipmap.cot_km, new FragmentInputItem()));
//        list.add(new EnMainCatalogItem(getResources().getString(R.string.bridge), false, R.mipmap.cau, new FragmentInputItem()));
//        list.add(new EnMainCatalogItem(getResources().getString(R.string.marker), false, R.mipmap.coctieu, new FragmentInputItem()));
//        list.add(new EnMainCatalogItem(getResources().getString(R.string.manhole_sumps), false, R.mipmap.roads, new FragmentInputItem()));
//        list.add(new EnMainCatalogItem(getResources().getString(R.string.longitudinal_grooves_groove_Border), false, R.mipmap.roads, new FragmentInputItem()));
//        list.add(new EnMainCatalogItem(getResources().getString(R.string.vertical_drain), false, R.mipmap.roads, new FragmentInputItem()));

            adapter = new categoryAdapter(Application.getInstance().getApplicationContext(), list);
            setData();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            gridView.setNestedScrollingEnabled(true);
        }

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!IS_SELECT_MULTI) {
                    Logger.error("select pos: " + position + ", id:" + id);
                    if(position >= 11) {
                        AnimationControl.zoomImageFromThumb(adapter.getViewAt(position - 3)
                                , list.get(position).getImg(), rootView, swapInterface,
                                list.get(position));
                    }else{
                        AnimationControl.zoomImageFromThumb(adapter.getViewAt(position)
                                , list.get(position).getImg(), rootView, swapInterface,
                                list.get(position));
                    }
                } else {
                    list.get(position).setChecked(!list.get(position).isChecked());
                    adapter.notifyDataSetChanged();
                }
            }
        });

        return rootView;
    }

    public List<EnMainCatalogItem> collectSelectedItem() {
        List<EnMainCatalogItem> listChecked = new ArrayList<>();
        for (EnMainCatalogItem en :list) {
            if (en.isChecked()) {
                listChecked.add(en);
            }
        }
        return listChecked;
    }

    public void setMultiSelect(boolean multiSelect) {
        if (!multiSelect)
            for (EnMainCatalogItem en : list)
                en.setChecked(false);
        IS_SELECT_MULTI = multiSelect;
        adapter.setMultiSelect(multiSelect);
        adapter.notifyDataSetChanged();
    }


    public void setData() {
        gridView.setAdapter(adapter);
    }

    private void initLayout() {
        gridView = (GridView) rootView.findViewById(R.id.gridData);
    }
}
