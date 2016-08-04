package com.example.macos.entities;

import com.example.macos.database.Item;
import com.example.macos.utilities.CustomFragment;

import java.io.Serializable;

/**
 * Created by macos on 6/15/16.
 */
public class EnMainCatalogItem implements Serializable{
    private Item item;
    private boolean checked;
    private int img;
    private CustomFragment f;

    public EnMainCatalogItem(Item item, boolean checked, int img, CustomFragment f){
        this.item = item;
        this.checked = checked;
        this.img = img;
        this.setFragment(f);

    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public int getImg() {
        return img;
    }

    public void setImg(int img) {
        this.img = img;
    }

    public CustomFragment getFragment() {
        return f;
    }

    public void setFragment(CustomFragment f) {
        this.f = f;
    }
}
