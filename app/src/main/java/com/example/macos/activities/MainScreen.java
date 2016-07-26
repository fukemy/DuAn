package com.example.macos.activities;

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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.macos.adapter.MainScreenAdapter;
import com.example.macos.duan.R;
import com.example.macos.entities.EnMainCatalogItem;
import com.example.macos.entities.EnWorkList;
import com.example.macos.fragment.FragmentAccident;
import com.example.macos.fragment.FragmentReportLastDay;
import com.example.macos.fragment.mainscreen.FragmentMainDataScreen;
import com.example.macos.fragment.report.FragmentProblem;
import com.example.macos.interfaces.iDialogAction;
import com.example.macos.interfaces.iListWork;
import com.example.macos.libraries.Logger;
import com.example.macos.main.SprashScreen;
import com.example.macos.report.FragmentReportDiary;
import com.example.macos.report.FragmentReportMap;
import com.example.macos.report.FragmentReportStatus;
import com.example.macos.utilities.CustomFragment;
import com.example.macos.utilities.FunctionUtils;
import com.example.macos.utilities.GlobalParams;
import com.example.macos.utilities.SharedPreferenceManager;

import java.util.ArrayList;
import java.util.List;

public class MainScreen extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private final String CURRENT_FRAGMENT = "Current Fragment";
    private Menu menu;
    private EnWorkList enWorkLists;
    private List<EnMainCatalogItem> dataList;
    private String ROAD_NAME = "";
    public String ACTION_TYPE = "";
    private SharedPreferenceManager pref;
    public String getRoadName(){
        return ROAD_NAME;
    }
    boolean IS_SYNC_NOW = false;
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Logger.error("on new intent");
        IS_SYNC_NOW = true;
        initUploadScreen();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_screen);

        pref = new SharedPreferenceManager(MainScreen.this);

//        FunctionUtils.setAlarm(this);

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

        String LOGIN_METHOD = pref.getString(GlobalParams.LOGGED_ON_METHOD, "normal");
        tvUserInfor.setText("geotech@thehegeo.com");

        if(LOGIN_METHOD.equals("facebook")) {
            String avatarString = pref.getString(GlobalParams.FB_AVATAR, "");
            imageView.setImageBitmap(FunctionUtils.StringToBitMap(avatarString));
            tvUserFullname.setText(pref.getString(GlobalParams.FB_USERNAME, "User"));
        }else{
            tvUserFullname.setText(pref.getString(GlobalParams.USERNAME, "User"));
        }

        ACTION_TYPE = getResources().getString(R.string.road_test);

        if(IS_SYNC_NOW)
            initUploadScreen();
        else
            initLayoutAndData();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            viewPager.setNestedScrollingEnabled(true);
        }else{
        }
    }

    private void showDialog(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.road_name));
        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
        View dialogRoadNameInput = inflater.inflate(R.layout.edittext_input, null, false);
        final EditText chooseRoadName = (EditText) dialogRoadNameInput.findViewById(R.id.edt);
        //FunctionUtils.setupKeyboard(chooseRoadName, MainScreen.this);

        builder.setView(dialogRoadNameInput);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ROAD_NAME = chooseRoadName.getText().toString().trim();
                //Toast.makeText(MainScreen.this, "ROAD_NAME: " + ROAD_NAME, Toast.LENGTH_SHORT).show();
                pref.saveString(GlobalParams.ROAD_NAME, ROAD_NAME);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        final AlertDialog alertDialog = builder.show();
        chooseRoadName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE){
                    ROAD_NAME = chooseRoadName.getText().toString().trim();
                    //Toast.makeText(MainScreen.this, "ROAD_NAME: " + ROAD_NAME, Toast.LENGTH_SHORT).show();
                    pref.saveString(GlobalParams.ROAD_NAME, ROAD_NAME);
                    alertDialog.dismiss();
                    return true;
                }
                return false;
            }
        });
    }

    ViewPager viewPager;
    TabLayout tabLayout;
    MainScreenAdapter adapter;

    private void initReportScreen(){
        //adapter = new MainScreenAdapter(getSupportFragmentManager());
        ReportScreenAdapter adapter = new ReportScreenAdapter(getSupportFragmentManager());
        /*
        // init data
        FragmentReportDiary mainDataScreen = new FragmentReportDiary();
        mainDataScreen.setInterface(swap);
        adapter.addFragment(mainDataScreen, getResources().getString(R.string.NhatKy));

        FragmentReportStatus status = new FragmentReportStatus();
        status.setInterface(swap);
        adapter.addFragment(status, getResources().getString(R.string.Tinhtrang));

        FragmentReportMap map = new FragmentReportMap();
        map.setInterface(swap);
        adapter.addFragment(map, getResources().getString(R.string.map));
        */

        // set data
        viewPager.setAdapter(null);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            viewPager.setNestedScrollingEnabled(true);
        }else{
        }

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                /*
                switch (tab.getPosition()){
                    case 0:
                        spinFilter.setVisibility(View.GONE);
                        break;
                    case 1:
                        spinFilter.setVisibility(View.GONE);
                        break;
                    case 2:
                        spinFilter.setVisibility(View.GONE);
                        break;
                }
                */

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
        //adapter = new MainScreenAdapter(getSupportFragmentManager());
        viewPager = (ViewPager)findViewById(R.id.viewpager);
        ReportScreenAdapter adapter = new ReportScreenAdapter(getSupportFragmentManager());
        /*
        // init data
        FragmentReportDiary mainDataScreen = new FragmentReportDiary();
        mainDataScreen.setInterface(swap);
        adapter.addFragment(mainDataScreen, getResources().getString(R.string.NhatKy));

        FragmentReportStatus status = new FragmentReportStatus();
        status.setInterface(swap);
        adapter.addFragment(status, getResources().getString(R.string.Tinhtrang));

        FragmentReportMap map = new FragmentReportMap();
        map.setInterface(swap);
        adapter.addFragment(map, getResources().getString(R.string.map));
        */

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
                    ((CustomFragment) f).setInterface(swap);
                    break;
                case 1 :
                    f = new FragmentReportStatus();
                    ((CustomFragment) f).setInterface(swap);
                    break;
                case 2:
                    f = new FragmentReportMap();
                    ((CustomFragment) f).setInterface(swap);
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
        viewPager = (ViewPager)findViewById(R.id.viewpager);
        adapter = new MainScreenAdapter(getSupportFragmentManager());

        // init data
        FragmentMainDataScreen mainDataScreen = new FragmentMainDataScreen();
        mainDataScreen.setInterface(swap);
        adapter.addFragment(mainDataScreen, getResources().getString(R.string.road_test));

        final FragmentReportLastDay lastday = new FragmentReportLastDay();
        lastday.setInterface(swap);
        adapter.addFragment(lastday, getResources().getString(R.string.lastday));

        final FragmentAccident accident = new FragmentAccident();
        accident.setInterface(swap);
        adapter.addFragment(accident, getResources().getString(R.string.accident));

        final FragmentProblem problem = new FragmentProblem();
        problem.setInterface(swap);
        adapter.addFragment(problem, getResources().getString(R.string.problem));

        // set data
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(4);
        tabLayout = (TabLayout)findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());

                if((adapter.getmFragmentList().get(viewPager.getCurrentItem())) instanceof FragmentMainDataScreen) {
                    ((FragmentMainDataScreen) adapter.getmFragmentList().get(viewPager.getCurrentItem())).setMultiSelect(false);
                    FunctionUtils.hideMenu(menu, true);
                }

                switch (tab.getPosition()){
                    case 0:
                        ACTION_TYPE = getResources().getString(R.string.road_test);
                        FunctionUtils.hideMenu(menu, true);
                        break;
                    case 1:
                        ACTION_TYPE = getResources().getString(R.string.lastday);
                        FunctionUtils.hideMenu(menu, false);
                        break;
                    case 2:
                        ACTION_TYPE = getResources().getString(R.string.problem);
                        accident.setUpMap();
                        FunctionUtils.hideMenu(menu, false);
                        break;
                    case 3:
                        ACTION_TYPE = getResources().getString(R.string.problem);
                        problem.setUpMap();
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

        ROAD_NAME = pref.getString(GlobalParams.ROAD_NAME, "");

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Warning");
        if(!ROAD_NAME.equals("")) {
            builder.setMessage("Tên đường trước đó bạn đã nhập là '" + ROAD_NAME +"'"
                    + ", " + getResources().getString(R.string.bancomuonchonlaitenduongchu));
        }else{
            builder.setMessage("Bạn chưa chọn tên đường, ấn OK để nhập!");
        }

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showDialog();
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
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

    public iListWork swap = new iListWork() {

        //for single work
        @Override
        public void doListWork(EnMainCatalogItem en) {
            Intent in = new Intent(MainScreen.this, AcInput.class);
            List<String> temp = new ArrayList<>();
            temp.add(en.getName());
            enWorkLists = new EnWorkList(temp);
            in.putExtra(GlobalParams.ROAD_NAME, ROAD_NAME);
            in.putExtra(GlobalParams.LIST_WORKING_NAME, enWorkLists);
            in.putExtra(GlobalParams.ACTION_TYPE, getResources().getString(R.string.road_test));
            startActivity(in);
        }

        //for multi works
        @Override
        public void doListWorks() {
            Intent in = new Intent(MainScreen.this, AcInput.class);
            in.putExtra(GlobalParams.ROAD_NAME, ROAD_NAME);
            in.putExtra(GlobalParams.LIST_WORKING_NAME, enWorkLists);
            in.putExtra(GlobalParams.ACTION_TYPE, getResources().getString(R.string.road_test));
            startActivity(in);
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        if(menu != null)
            menu.findItem(R.id.select_road_type).setTitle(getResources().getString(R.string.multi_select));
          //initLayoutAndData();
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
        menu.findItem(R.id.done).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.enter_road_name){
            showDialog();
            return true;
        }
        if (id == R.id.select_road_type){

            if(ROAD_NAME.equals("")){
                FunctionUtils.showErrorDialog(getResources().getString(R.string.bancanchontenduongtruoctien), MainScreen.this, dialogInterface);
            }

            if(menu.findItem(R.id.select_road_type).getTitle().equals(getResources().getString(R.string.multi_select))){
                ((FragmentMainDataScreen)adapter.getmFragmentList().get(viewPager.getCurrentItem())).setMultiSelect(true);
                menu.findItem(R.id.done).setVisible(true);
                menu.findItem(R.id.select_road_type).setTitle(getResources().getString(R.string.single_select));
                return false;
            }
            if(menu.findItem(R.id.select_road_type).getTitle().equals(getResources().getString(R.string.single_select))) {
                ((FragmentMainDataScreen) adapter.getmFragmentList().get(viewPager.getCurrentItem())).setMultiSelect(false);
                menu.findItem(R.id.select_road_type).setTitle(getResources().getString(R.string.multi_select));
                menu.findItem(R.id.done).setVisible(false);
                return false;
            }

        }

        if (id == R.id.done ) {
            if(menu.findItem(R.id.done).isVisible()){
                menu.findItem(R.id.done).setVisible(false);
                // collec checked item first
                List<EnMainCatalogItem> listMainData =  ((FragmentMainDataScreen)adapter.getmFragmentList().get(viewPager.getCurrentItem())).collectSelectedItem();
                if(listMainData.size() != 0) {
                    List<String> temp = new ArrayList<>();
                    for(EnMainCatalogItem str : listMainData){
                        temp.add(str.getName());
                    }
                    enWorkLists = new EnWorkList(temp);
                    swap.doListWorks();
                }else{
                    ((FragmentMainDataScreen) adapter.getmFragmentList().get(viewPager.getCurrentItem())).setMultiSelect(false);
                }
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        final String title = item.getTitle().toString();

        if (id == R.id.nav_input) {
            getSupportActionBar().setTitle(title);
            initLayoutAndData();
            FunctionUtils.hideMenu(menu, true);
        }  else if (id == R.id.nav_report) {
            getSupportActionBar().setTitle(title);
            initReportScreen();
            FunctionUtils.hideMenu(menu, false);
        }  else if (id == R.id.nav_logout) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainScreen.this);
            builder.setTitle("Warning");
            builder.setMessage(getResources().getString(R.string.bancochacchanmuondangxuat));

            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    pref.saveBoolean(GlobalParams.IS_LOGGED_ON, false);
                    pref.saveString(GlobalParams.USERNAME, "");
                    pref.saveLong(GlobalParams.LAST_LOGIN, 0);
                    pref.saveBoolean(GlobalParams.FACEBOOK_LOGED_IN, false);

                    startActivity(new Intent(MainScreen.this, SprashScreen.class));
                    finish();
                }
            });

            builder.setNegativeButton("Cancel", null);
            builder.show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
