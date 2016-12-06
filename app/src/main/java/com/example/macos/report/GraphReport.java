package com.example.macos.report;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.transition.Explode;
import android.transition.Fade;
import android.transition.Transition;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.macos.database.BlueToothData;
import com.example.macos.database.DatabaseHelper;
import com.example.macos.duan.R;
import com.example.macos.libraries.Logger;
import com.example.macos.libraries.WorkaroundMapFragment;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Marker;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.ValueDependentColor;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.Series;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class GraphReport extends AppCompatActivity {
    private GraphView graph;
    private Spinner spnGraphType;
    private LineGraphSeries<DataPoint> series;
    private BarGraphSeries<DataPoint> barSeries;
    private final Handler mHandler = new Handler();
    private Runnable mTimer;
    private Button btnBack;
    private GoogleMap gMap;

    List<BlueToothData> blData;
    String dataUUID;
    long[] dp;

    GraphViewMode graphViewMode;

    enum GraphViewMode {
        Line, Bar
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_graph_report);

        initViewAndData();
        initGraph();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Transition transition = new Explode().setDuration(600);
            transition.addListener(new Transition.TransitionListener() {
                @Override
                public void onTransitionStart(Transition transition) {

                }

                @Override
                public void onTransitionEnd(Transition transition) {
                    try {
//                        loadAllDataGraph();
                        initMap();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onTransitionCancel(Transition transition) {

                }

                @Override
                public void onTransitionPause(Transition transition) {

                }

                @Override
                public void onTransitionResume(Transition transition) {

                }
            });
            getWindow().setEnterTransition(transition);
            getWindow().setSharedElementExitTransition(new Fade().setDuration(300));
            getWindow().setExitTransition(new Fade().setDuration(100));
        } else {
            try {
//                loadAllDataGraph();
                initMap();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    LinearLayout infoWindow;
    private void initMap(){
        ((WorkaroundMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapp)).getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                gMap = googleMap;
                ((WorkaroundMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapp)).setListener(new WorkaroundMapFragment.OnTouchListener() {
                    @Override
                    public void onTouch() {
                        if(gMap != null){
                            gMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                                public View getInfoWindow(Marker arg0) {
                                    return null;
                                }

                                @Override
                                public View getInfoContents(Marker marker) {

                                    infoWindow = new LinearLayout(GraphReport.this);
                                    infoWindow.setOrientation(LinearLayout.VERTICAL);

                                    TextView title = new TextView(GraphReport.this);
                                    title.setTextColor(Color.BLACK);
                                    title.setGravity(Gravity.CENTER);
                                    title.setTypeface(null, Typeface.BOLD);
                                    title.setText(marker.getTitle());

                                    TextView snippet = new TextView(GraphReport.this);
                                    snippet.setTextColor(Color.GRAY);
                                    snippet.setText(marker.getSnippet());

                                    infoWindow.addView(title);
                                    infoWindow.addView(snippet);
                                    return infoWindow;
                                }
                            });

                        }
                    }
                });
            }
        });
    }



    private void initViewAndData() {
        if (getIntent() != null)
            dataUUID = getIntent().getStringExtra("dataUUID");

        spnGraphType = (Spinner) findViewById(R.id.spnChooseGraphType);
        btnBack = (Button) findViewById(R.id.btnBack);

        ArrayAdapter<String> spnGraphAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,
                getResources().getStringArray(R.array.tuychonbando));
        spnGraphType.setAdapter(spnGraphAdapter);
        spnGraphType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Logger.error("selected :" + i);
                spnGraphType.setEnabled(false);
                if (i == 0) {
                    loadAllDataGraph();
                } else {
                    loadAllDataBarGraph();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        mHandler.removeCallbacks(mTimer);
        super.onPause();
    }

    private void initGraph() {
        graph = (GraphView) findViewById(R.id.graph);
        graph.getViewport().setScrollable(true); // enables horizontal scrolling
        graph.getViewport().setScrollableY(true); // enables vertical scrolling
        graph.getViewport().setScalable(true); // enables horizontal zooming and scrolling
        graph.getViewport().setScalableY(true); // enables vertical zooming and scrollingZ

        graph.getGridLabelRenderer().setTextSize(10f);
        graph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX) {
                    if (dp != null && dp.length > 0) {
                        try {
                            long timeVal = dp[(int) value];
                            SimpleDateFormat formatter = new SimpleDateFormat("hh.mm.ss");
                            return formatter.format(new Date(timeVal));
                        }catch(Exception e){
                            return super.formatLabel(value, isValueX);
                        }
                    } else {
                        return super.formatLabel(value, isValueX);
                    }
                } else {
                    return super.formatLabel(value, isValueX);
                }
            }
        });
    }

    int i = 0;

    private void loadAllDataGraph() {
        blData = new ArrayList<>();
        if (dataUUID != null)
            blData = DatabaseHelper.getBlueToothDataByID(dataUUID);

        if (blData.size() > 0) {
            setGraphData(GraphViewMode.Line);
        }else{
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Không tìm thấy dữ liệu độ sóc cho bản ghi này!");
            builder.setCancelable(false);
            builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    finish();
                }
            });
            builder.show();
        }
    }

    private void loadAllDataBarGraph() {
        blData = new ArrayList<>();
        if (dataUUID != null)
            blData = DatabaseHelper.getBlueToothDataByID(dataUUID);

        if (blData.size() > 0) {
            for (Iterator<BlueToothData> iterator = blData.iterator(); iterator.hasNext(); ) {
                BlueToothData value = iterator.next();
                if (value.getZaxisValue() == 0) {
                    iterator.remove();
                }
            }

            setGraphData(GraphViewMode.Bar);
        }
    }

    private void setGraphData(GraphViewMode mode) {
        graphViewMode = mode;
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(blData.size());

        i = 0;
        dp = new long[blData.size()];
        for (int j = 0; j < blData.size(); j++) {
            dp[j] = Long.parseLong(blData.get(j).getDateTimeLoging());
        }

        graph.removeAllSeries();

        if (mode == GraphViewMode.Line) {

            series = new LineGraphSeries<>();
            series.setColor(Color.RED);
            series.setThickness(1);
            graph.addSeries(series);

            for(int i = 0; i < blData.size(); i++){
                series.appendData(new DataPoint(i, blData.get(i).getZaxisValue()), false, blData.size());
            }
        } else {

            barSeries = new BarGraphSeries<>();
            barSeries.setDrawValuesOnTop(true);
            barSeries.setValuesOnTopColor(Color.RED);

            barSeries.setValueDependentColor(new ValueDependentColor<DataPoint>() {
                @Override
                public int get(DataPoint data) {
                    return Color.rgb((int) data.getX() * 255 / 4, (int) Math.abs(data.getY() * 255 / 6), 100);
                }
            });

            barSeries.setOnDataPointTapListener(new OnDataPointTapListener() {
                @Override
                public void onTap(Series series, DataPointInterface dataPoint) {
                    Toast.makeText(GraphReport.this, "Series1: On Data Point clicked: " + dataPoint, Toast.LENGTH_SHORT).show();
                }
            });

            graph.addSeries(barSeries);

            for(int i = 0; i < blData.size(); i++){
                barSeries.appendData(new DataPoint(i, blData.get(i).getZaxisValue()), false, blData.size());
            }
        }

        spnGraphType.setEnabled(true);
    }

}
