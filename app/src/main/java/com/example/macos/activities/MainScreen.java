package com.example.macos.activities;

import android.app.ActivityOptions;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.macos.adapter.ChooseRoadNameAdapter;
import com.example.macos.adapter.MainScreenAdapter;
import com.example.macos.database.DatabaseHelper;
import com.example.macos.database.RoadInformation;
import com.example.macos.duan.R;
import com.example.macos.fragment.mainscreen.FragmentMainDataScreen;
import com.example.macos.fragment.report.FragmentAccident;
import com.example.macos.fragment.report.FragmentReportDiary;
import com.example.macos.fragment.report.FragmentReportMap;
import com.example.macos.fragment.report.FragmentReportStatus;
import com.example.macos.interfaces.iDialogAction;
import com.example.macos.libraries.Logger;
import com.example.macos.utilities.FunctionUtils;
import com.example.macos.utilities.GlobalParams;
import com.example.macos.utilities.SharedPreferenceManager;
import com.example.macos.youtube.AcVideoList;
import com.google.android.gms.maps.MapsInitializer;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;

public class MainScreen extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    ViewPager viewPager;
    TabLayout tabLayout;
    MainScreenAdapter adapter;
    private Gson gson;
    private Menu menu;
    public String ACTION_TYPE = "";
    private SharedPreferenceManager pref;
    boolean IS_SYNC_NOW = false;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Logger.error("on new intent");
        IS_SYNC_NOW = true;
        if(intent.getBooleanExtra("isDeleteData", false))
            initLayoutAndData();
        else
            initUploadScreen();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_screen);

        pref = new SharedPreferenceManager(MainScreen.this);
        gson = new Gson();


        MapsInitializer.initialize(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.enter_infor));

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        View hView =  navigationView.getHeaderView(0);
        TextView tvUserFullname = (TextView)hView.findViewById(R.id.tvFullName);
        TextView tvUserInfor = (TextView)hView.findViewById(R.id.tvInformation);
        ImageView imageView = (ImageView)hView.findViewById(R.id.imageView);

        tvUserInfor.setText("geotech@thehegeo.com");
        tvUserFullname.setText(pref.getString(GlobalParams.USERNAME, "dungdv"));

        ACTION_TYPE = getResources().getString(R.string.road_test);

        if(IS_SYNC_NOW)
            initUploadScreen();
        else
            initLayoutAndData();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            viewPager.setNestedScrollingEnabled(true);
//            getWindow().setEnterTransition(new Explode().setDuration(400));
        }


    }
    AlertDialog d;
    private void showDialog(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.road_name));
        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
        View dialogRoadNameInput = inflater.inflate(R.layout.edittext_input, null, false);
        final AutoCompleteTextView chooseRoadName = (AutoCompleteTextView) dialogRoadNameInput.findViewById(R.id.edt);

        final List<RoadInformation> list = DatabaseHelper.getRoadInformationList();
        final HashMap<String, RoadInformation> listRoadName = new HashMap<String, RoadInformation>();
        for(RoadInformation road: list){
            listRoadName.put(road.getTenDuong(), road);
        }

        ChooseRoadNameAdapter adapter = new ChooseRoadNameAdapter(MainScreen.this, android.R.layout.simple_dropdown_item_1line,
                listRoadName.keySet().toArray(new String[listRoadName.size()]));
        chooseRoadName.setAdapter(adapter);
        chooseRoadName.setThreshold(1);

        chooseRoadName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean isFocused) {
                if(isFocused)
                    chooseRoadName.showDropDown();

                if(chooseRoadName.getText().toString().length() == 0)
                    d.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);

            }
        });

        chooseRoadName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                d.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s)) {
                    d.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                }
            }
        });

        chooseRoadName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Logger.error("selected item");
                d.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
            }

        });

        builder.setView(dialogRoadNameInput);
        builder.setCancelable(false);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String ROAD_NAME = chooseRoadName.getText().toString().trim();
                Logger.error("Road choosen: " + gson.toJson(listRoadName.get(ROAD_NAME)));
                pref.saveString(GlobalParams.ROAD_CHOOSEN, gson.toJson(listRoadName.get(ROAD_NAME)));
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        d = builder.show();
    }

    private void initReportScreen(){
        ReportScreenAdapter adapter = new ReportScreenAdapter(getSupportFragmentManager());

        // set data
        viewPager.setAdapter(null);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

    }

    private void initUploadScreen(){
        viewPager = (ViewPager)findViewById(R.id.viewpager);
        ReportScreenAdapter adapter = new ReportScreenAdapter(getSupportFragmentManager());

        // set data
        viewPager.setAdapter(null);
        viewPager.setAdapter(adapter);
        tabLayout = (TabLayout)findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setCurrentItem(1);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            viewPager.setNestedScrollingEnabled(true);
        }else{
        }

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

    }


    private class ReportScreenAdapter extends FragmentStatePagerAdapter{

        public ReportScreenAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment f = null;
            switch (position){
                case 0 :
                    f = new FragmentReportDiary();
                    break;
                case 1 :
                    f = new FragmentReportStatus();
                    break;
                case 2:
                    f = new FragmentReportMap();
                    break;
                default:
                    return null;
            }
            return f;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position){
                case 0 :
                    return getResources().getString(R.string.NhatKy);
                case 1 :
                    return getResources().getString(R.string.Status);
                case 2:
                    return getResources().getString(R.string.map);
                default:
                    return "";
            }
        }
    }

    public void initLayoutAndData(){
        Logger.error("initLayoutAndData");
        viewPager = (ViewPager)findViewById(R.id.viewpager);
        adapter = new MainScreenAdapter(getSupportFragmentManager());

        // init data
        FragmentMainDataScreen mainDataScreen = new FragmentMainDataScreen();
        adapter.addFragment(mainDataScreen, getResources().getString(R.string.road_test));

        final FragmentAccident accident = new FragmentAccident();
        adapter.addFragment(accident, "Lập báo cáo");

        // set data
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(3);
        tabLayout = (TabLayout)findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());

                if((adapter.getmFragmentList().get(viewPager.getCurrentItem())) instanceof FragmentMainDataScreen) {
                    FunctionUtils.hideMenu(menu, true);
                }

                switch (tab.getPosition()){
                    case 0:
                        ACTION_TYPE = getResources().getString(R.string.road_test);
                        FunctionUtils.hideMenu(menu, true);
                        FunctionUtils.hideSoftInput(viewPager, MainScreen.this);
                        break;
                    case 1:
                        ACTION_TYPE = "Lập báo cáo";
                        if(!accident.getProgressState())
                            accident.setUpMap();
                        FunctionUtils.hideMenu(menu, false);
                        break;
                    default:
                        break;

                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        FunctionUtils.hideMenu(menu, true);

        String ROAD_NAME = pref.getString(GlobalParams.ROAD_CHOOSEN, "");
        if(ROAD_NAME != "") {
            if(gson == null)
                gson = new Gson();
            try {
                ROAD_NAME = gson.fromJson(ROAD_NAME, RoadInformation.class).getTenDuong();
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Warning");
                builder.setMessage("Tên đường trước đó bạn đã nhập là '" + ROAD_NAME + "'"
                        + ", " + getResources().getString(R.string.bancomuonchonlaitenduongchu));
                builder.setCancelable(true);
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showDialog();
                    }
                });
                builder.setNegativeButton("Cancel", null);
                builder.show();
            }catch(Exception e){
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Hệ thống không nhận diện được tên đường đã chọn, xin vui lòng chọn lại");
                builder.setCancelable(true);
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showDialog();
                    }
                });
                builder.setCancelable(false);
                builder.show();
            }
        }else{
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Bạn chưa chọn tên đường, ấn OK để nhập!");
            builder.setCancelable(false);
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    showDialog();
                }
            });
            builder.show();
        }


    }


    public String getAction(){
        return ACTION_TYPE;
    }

    public iDialogAction dialogInterface = new iDialogAction() {
        @Override
        public void showRoadNameInputDialog() {
            showDialog();
            initLayoutAndData();
        }

        @Override
        public void refreshViewonly() {
            initLayoutAndData();
        }

        @Override
        public void isAcceptWarning(boolean b) {
            showDialog();
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainScreen.this);
            builder.setTitle("Confirm");
            builder.setMessage(getResources().getString(R.string.bancochacchanmuonthoat));
            builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });

            builder.setNegativeButton("cancel", null);
            builder.show();
            //super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_screen, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.enter_road_name){
            showDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    int currrentID = 0;
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        final String title = item.getTitle().toString();
        if (id != currrentID && id == R.id.nav_input) {
            getSupportActionBar().setTitle(title);
            initLayoutAndData();
            FunctionUtils.hideMenu(menu, true);

        } else if (id != currrentID && id == R.id.nav_report) {
            getSupportActionBar().setTitle(title);
            initReportScreen();
            FunctionUtils.hideMenu(menu, false);

        } else if (id == R.id.nav_ici) {
            Intent in = new Intent(MainScreen.this, AcICIChecking.class);
            startActivity(in);

        } else if (id == R.id.nav_video) {
            Intent in = new Intent(MainScreen.this, AcVideoList.class);
            startActivity(in);

        } else if (id == R.id.nav_logout) {
            Intent in = new Intent(MainScreen.this, AcSetting.class);
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                Bundle bundle = ActivityOptions.makeSceneTransitionAnimation(this).toBundle();
                startActivity(in,bundle);
            }else {
                startActivity(in);
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        currrentID = id;
        return true;
    }
}
