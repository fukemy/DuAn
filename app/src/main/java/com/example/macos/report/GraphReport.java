package com.example.macos.report;

import android.app.SharedElementCallback;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.transition.Fade;
import android.transition.Transition;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.macos.database.BlueToothData;
import com.example.macos.database.DatabaseHelper;
import com.example.macos.duan.R;
import com.example.macos.libraries.Logger;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.ValueDependentColor;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.Series;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class GraphReport extends AppCompatActivity {
    private GraphView graph;
    private Spinner spnGraphType;
    private LineGraphSeries<DataPoint> series;
    private BarGraphSeries<DataPoint> barSeries;
    private final Handler mHandler = new Handler();
    private Runnable mTimer;

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
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private void initViewAndData() {
        if (getIntent() != null)
            dataUUID = getIntent().getStringExtra("dataUUID");

        spnGraphType = (Spinner) findViewById(R.id.spnChooseGraphType);

        ArrayAdapter<String> spnGraphAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,
                getResources().getStringArray(R.array.tuychonbando));
        spnGraphType.setAdapter(spnGraphAdapter);
        spnGraphType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Logger.error("selected :" + i);
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
        }
    }

    private void loadAllDataBarGraph() {
        blData = new ArrayList<>();
        if (dataUUID != null)
            blData = DatabaseHelper.getBlueToothDataByID(dataUUID);

        if (blData.size() > 0) {
            for (Iterator<BlueToothData> iterator = blData.iterator(); iterator.hasNext(); ) {
                BlueToothData value = iterator.next();
                if (value.getZValue() == 0) {
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
            dp[j] = Long.parseLong(blData.get(j).getTime());
        }

        graph.removeAllSeries();

        if (mode == GraphViewMode.Line) {

            series = new LineGraphSeries<>();
            series.setColor(Color.RED);
            series.setThickness(1);
            graph.addSeries(series);
            mTimer = new Runnable() {
                @Override
                public void run() {

                    series.appendData(new DataPoint(i, blData.get(i).getZValue()), false, blData.size());
                    mHandler.postDelayed(this, 5);
                    if (i == blData.size() - 1)
                        mHandler.removeCallbacks(mTimer);
                    i++;
                }
            };
            mHandler.postDelayed(mTimer, 700);
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

            mTimer = new Runnable() {
                @Override
                public void run() {
                    barSeries.appendData(new DataPoint(i, blData.get(i).getZValue()), false, blData.size());
                    mHandler.postDelayed(this, 5);

                    if (i == blData.size() - 1)
                        mHandler.removeCallbacks(mTimer);
                    i++;
                }
            };
            mHandler.postDelayed(mTimer, 800);
        }
    }

}
