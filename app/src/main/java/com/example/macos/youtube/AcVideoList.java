package com.example.macos.youtube;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Location;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.macos.activities.AcVideo;
import com.example.macos.adapter.VideoListAdapter;
import com.example.macos.duan.R;
import com.example.macos.entities.EnLocationItem;
import com.example.macos.entities.EnVideoItem;
import com.example.macos.interfaces.iLocationUpdate;
import com.example.macos.interfaces.iYoutubeQuery;
import com.example.macos.libraries.AutoResizeTextView;
import com.example.macos.libraries.Logger;
import com.example.macos.libraries.SlidingUpPaneLayout;
import com.example.macos.utilities.AnimationControl;
import com.example.macos.utilities.FunctionUtils;
import com.example.macos.utilities.GlobalParams;
import com.example.macos.utilities.LocationHelper;
import com.example.macos.utilities.NetworkUtil;
import com.example.macos.utilities.Utilities;
import com.github.florent37.viewanimator.AnimationListener;
import com.github.florent37.viewanimator.ViewAnimator;
import com.github.pedrovgs.DraggableListener;
import com.github.pedrovgs.DraggableView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.truizlop.fabreveallayout.FABRevealLayout;
import com.truizlop.fabreveallayout.OnRevealChangeListener;

import java.util.List;

public class AcVideoList extends YouTubeBaseActivity implements
        YouTubePlayer.OnInitializedListener, View.OnClickListener {
    private static final int RECOVERY_DIALOG_REQUEST = 1234;
    private ListView listView;
    private DraggableView draggableView;
    private VideoListAdapter adapter;
    private YouTubePlayerView videoView;
    private YouTubePlayer youtubePlayer;
    private TextView tvTitle, tvProvider, tvAuthor;
    private AutoResizeTextView tvLocation;
    private SwipeRefreshLayout swipeLayout;
    private ImageButton btnBack;
    private SlidingUpPanelLayout slidingLayout;
    private MapView mMapView;
    private GoogleMap googleMap;
//    private FABRevealLayout fabRevealLayout;
    private FloatingActionButton fabMapState;
    boolean isShowingFullscreen;
    String VIDEO_ID = "";
    int SCREEN_HEIGHT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.ac_video_list);

        SCREEN_HEIGHT = getResources().getDisplayMetrics().heightPixels;
        initView();
        populateData();

        mMapView = (MapView) findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        try {
            MapsInitializer.initialize(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                Logger.error("onmapReady");
                googleMap = mMap;
                //config google map
                googleMap.getUiSettings().setMapToolbarEnabled(false);
                googleMap.getUiSettings().setAllGesturesEnabled(false);
                googleMap.setMyLocationEnabled(false);

                //config style only when network is accessable
                FunctionUtils.modifyMapLayout(googleMap, AcVideoList.this);
            }

        });

    }


    private void initView(){
        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipeLayout);
        listView = (ListView) findViewById(R.id.list_view);
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvProvider = (TextView) findViewById(R.id.tvProvider);
        tvAuthor = (TextView) findViewById(R.id.tvAuthor);
        tvLocation = (AutoResizeTextView) findViewById(R.id.tvLocation);
        videoView = (YouTubePlayerView) findViewById(R.id.videoView);
        btnBack = (ImageButton) findViewById(R.id.btnBack);
        fabMapState = (FloatingActionButton) findViewById(R.id.fab_map_state);

        btnBack.setOnClickListener(this);
        tvLocation.setOnClickListener(this);

        videoView.initialize(GlobalParams.YOUTUBE_API_KEY, this);

        fabMapState.setOnClickListener(this);
        draggableView = (DraggableView) findViewById(R.id.draggable_view);
        draggableView.setVisibility(View.GONE);
        draggableView.setClickToMaximizeEnabled(true);
        draggableView.setClickToMinimizeEnabled(false);

        slidingLayout = (SlidingUpPanelLayout)findViewById(R.id.sliding_layout);
        slidingLayout.setParallaxOffset(200);
        slidingLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {

            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
            }
        });

        //modify height of slide panel because the slide panel not take all height of screen.
        slidingLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if(slidingLayout.getLayoutParams().height != SCREEN_HEIGHT - videoView.getHeight()) {
                    slidingLayout.getLayoutParams().height = SCREEN_HEIGHT - videoView.getHeight();
//                    mMapView.getLayoutParams().height = SCREEN_HEIGHT - videoView.getHeight()
//                            - fabRevealLayout.getHeight() + findViewById(R.id.fab_reveal_layout_main_view).getHeight();
//                    slidingLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            }
        });

        draggableView.setDraggableListener(new DraggableListener() {
            @Override
            public void onMaximized() {
                draggableView.bringToFront();
                isShowingFullscreen = false;
                slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                if(!youtubePlayer.isPlaying()){
                    youtubePlayer.play();
                }
            }

            @Override
            public void onMinimized() {
                draggableView.setAlpha(1.0f);
                draggableView.bringToFront();
                if(youtubePlayer.isPlaying()){
                    youtubePlayer.pause();
                }
            }

            @Override
            public void onClosedToLeft() {

            }

            @Override
            public void onClosedToRight() {

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                EnVideoItem videoData = (EnVideoItem) parent.getAdapter().getItem(position);
                VIDEO_ID = videoData.getId();

                tvTitle.setText(videoData.getTitle());
                tvAuthor.setText(videoData.getDescription());
                tvProvider.setText("THEHEGEO");

                LatLng caoTocLongThanhLatlng = GlobalParams.CAO_TOC_LONG_THANH_LATLNG;
                googleMap.clear();
                googleMap.addMarker(new MarkerOptions().position(caoTocLongThanhLatlng).title("TEST").snippet("Test"));
                CameraPosition cameraPosition = new CameraPosition.Builder().target(caoTocLongThanhLatlng).zoom(13).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                Location caoTocLongThanhLocation = new Location("");
                caoTocLongThanhLocation.setLatitude(caoTocLongThanhLatlng.latitude);
                caoTocLongThanhLocation.setLongitude(caoTocLongThanhLatlng.longitude);


                tvLocation.setText("");
                new LocationHelper().getLocationDetail(AcVideoList.this, caoTocLongThanhLocation, new iLocationUpdate() {
                    @Override
                    public void updateLocation(EnLocationItem lo) {
                        final String address = lo.getAddress().trim().replace("\n", ", ");
                        ViewAnimator.animate(tvLocation)
                                .slideBottom()
                                .alpha(0f,1f)
                                .bounceIn()
                                .descelerate()
                                .onStart(new AnimationListener.Start() {
                                    @Override
                                    public void onStart() {
                                        tvLocation.setText(getResources().getString(R.string.vitri) + ": " + address + ".");
                                    }
                                })
                                .duration(800)
                                .start();

                    }

                    @Override
                    public void onFailGetLocation() {
                        tvLocation.setText(getResources().getString(R.string.vitri) + ": N/A");
                    }
                });
                draggableView.setVisibility(View.VISIBLE);
                youtubePlayer.loadVideo(VIDEO_ID);
                draggableView.maximize();
            }
        });

        swipeLayout.setColorScheme(new int[]{android.R.color.holo_blue_dark,
                android.R.color.holo_blue_light,
                android.R.color.holo_green_light,
                android.R.color.holo_green_light});

        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeLayout.setRefreshing(true);
                (new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        populateData();
                    }
                }, 1000);
            }
        });
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider,
                                        YouTubeInitializationResult errorReason) {
        if (errorReason.isUserRecoverableError()) {
            errorReason.getErrorDialog(this, RECOVERY_DIALOG_REQUEST).show();
        } else {
            String errorMessage = String.format("Lỗi khởi động youtube (%1$s)",
                    errorReason.toString());
            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored) {
        if (!wasRestored) {
            draggableView.bringToFront();
            youtubePlayer = player;
            youtubePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);
            youtubePlayer.setShowFullscreenButton(true);
            youtubePlayer.setFullscreen(false);
            youtubePlayer.setOnFullscreenListener(new YouTubePlayer.OnFullscreenListener() {
                @Override
                public void onFullscreen(boolean b) {
                        isShowingFullscreen = b;
                }
            });
        }
    }

    private void toogleMapState(){
        if(slidingLayout.getPanelState() == SlidingUpPanelLayout.PanelState.COLLAPSED){
            slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
        }else{
            slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        }
    }

    private void switchSlideState(){
        if(slidingLayout.getPanelState() == SlidingUpPanelLayout.PanelState.COLLAPSED){
            slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
        }else if(slidingLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED){
            slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        }
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Display display = AcVideoList.this.getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            draggableView.setTopViewHeight(size.y);

        } else if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            Resources r = getResources();
            int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200, r.getDisplayMetrics()
            );
            draggableView.setTopViewHeight(px);
        }
    }

    private void populateData(){
        new YoutubeConnecter(this).searchVideoByChannel(GlobalParams.THEHEGEO_CHANNEL_ID, new iYoutubeQuery() {
            @Override
            public void onSuccess(List<EnVideoItem> videoList) {
                if(swipeLayout.isRefreshing())
                    swipeLayout.setRefreshing(false);
                if(videoList.size() > 0) {
                    adapter = new VideoListAdapter(videoList, AcVideoList.this);
                    listView.setAdapter(adapter);
                }else{
                    EnVideoItem item = new EnVideoItem();
                    item.setDescription("Test");
                    item.setTitle("test device in offline mode");
                    item.setId("r32r32rr3");
                    item.setThumbnailURL("google.com");

                    videoList.add(item);

                    adapter = new VideoListAdapter(videoList, AcVideoList.this);
                    listView.setAdapter(adapter);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if(VIDEO_ID == ""){
            //first show home screen
            super.onBackPressed();
        }else {
            if (draggableView.isMaximized()) {
                if (isShowingFullscreen == true) {
                    isShowingFullscreen = false;
                    youtubePlayer.setFullscreen(false);
                } else {
                    draggableView.minimize();
                }
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    protected void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnBack:
                onBackPressed();
                break;
            case R.id.tvLocation:
                switchSlideState();
                break;
            case R.id.fab_map_state:
                toogleMapState();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == RECOVERY_DIALOG_REQUEST){
            videoView.initialize(GlobalParams.YOUTUBE_API_KEY, this);
        }
    }
}
