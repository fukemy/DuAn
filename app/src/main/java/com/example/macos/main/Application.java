package com.example.macos.main;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.multidex.MultiDex;

import com.example.macos.database.DaoMaster;
import com.example.macos.database.DaoSession;

/**
 * Created by macos on 6/10/16.
 */
public class Application extends android.app.Application {
    public static Application instance;
    private DaoMaster daoMaster;
    public DaoSession daoSession;
    private SQLiteDatabase db;
    public static Application getInstance(){
        return instance;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "RoadProject-db",
                null);
        db = helper.getWritableDatabase();
        daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();

        instance = this;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
