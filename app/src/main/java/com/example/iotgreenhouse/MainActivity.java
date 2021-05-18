package com.example.iotgreenhouse;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;


public class MainActivity extends AppCompatActivity {

    // log
    private final static String TAG = "LOI";
    Button mSumBtn, mAuto, mManual, mFanOn, mFanOff, mPumpOn, mPumpOff;
    // Firebase instance
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference mRootReference = firebaseDatabase.getReference();
    private DatabaseReference mTempReference = mRootReference.child("Right").child("Temperature");
    private DatabaseReference mHumReference = mRootReference.child("Right").child("Hummidity");
    private DatabaseReference mSetTemReference = mRootReference.child("Right").child("Control").child("SetValueTemp");
    private DatabaseReference mSetHumReference = mRootReference.child("Right").child("Control").child("SetValueHum");
    private DatabaseReference mAutoReference = mRootReference.child("Right").child("Control").child("Auto");
    private DatabaseReference mManualReference = mRootReference.child("Right").child("Control").child("Manual");
    private DatabaseReference mFanReference = mRootReference.child("Right").child("Control").child("Fan");
    private DatabaseReference mPumpReference = mRootReference.child("Right").child("Control").child("Pump");
    // UI
    private TextView mTvTemp, mTvHum, mTvSetTemp, mTvSetHum;
    private EditText mSetTemp, mSetHum;
    private LineChart mChart;
    private Thread thread;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Function to set id to view
        initView();
        initChart(mChart);
        setSupportActionBar(mToolbar);

        mAuto.setOnClickListener(v -> {
            mAutoReference.setValue(1);
            mManualReference.setValue(0);

            mFanOn.setClickable(false);
            mFanOn.setFocusable(false);
            mFanOn.setFocusableInTouchMode(false);

            mFanOff.setClickable(false);
            mFanOff.setFocusable(false);
            mFanOff.setFocusableInTouchMode(false);

            mPumpOn.setClickable(false);
            mPumpOn.setFocusable(false);
            mPumpOn.setFocusableInTouchMode(false);

            mPumpOff.setClickable(false);
            mPumpOff.setFocusable(false);
            mPumpOff.setFocusableInTouchMode(false);

        });

        mManual.setOnClickListener(v -> {
            mManualReference.setValue(1);
            mAutoReference.setValue(0);
        });

        mFanOn.setOnClickListener(v -> {
            mFanReference.setValue(1);
            Toast.makeText(MainActivity.this, "Fan On", Toast.LENGTH_SHORT).show();
        });

        mFanOff.setOnClickListener(v -> {
            mFanReference.setValue(0);
            Toast.makeText(MainActivity.this, "Fan Off", Toast.LENGTH_SHORT).show();
        });

        mPumpOn.setOnClickListener(v -> {
            mPumpReference.setValue(1);
            Toast.makeText(MainActivity.this, "Pump On", Toast.LENGTH_SHORT).show();
            mPumpOn.setBackgroundColor(getResources().getColor(R.color.red));
            mPumpOff.setBackgroundColor(getResources().getColor(R.color.purple_500));
        });

        mPumpOff.setOnClickListener(v -> {
            mPumpReference.setValue(0);
            Toast.makeText(MainActivity.this, "Pump Off", Toast.LENGTH_SHORT).show();
            mPumpOff.setBackgroundColor(getResources().getColor(R.color.red));
            mPumpOn.setBackgroundColor(getResources().getColor(R.color.purple_500));
        });

        mTempReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    mTempReference.setValue(snapshot.getValue());

                    String valueToShow =
                            String.format(
                                    "Temperature: %s °C",
                                    snapshot.getValue()
                            );
                    mTvTemp.setText(valueToShow);

                    String valueTemp = snapshot.getValue() + "";
                    float floatTempChartValue;
                    floatTempChartValue = Float.parseFloat(valueTemp);
                    floatTempChartValue /= 100f;

                    float finalFloatTempChartValue = floatTempChartValue;
                    mHumReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                            if (snapshot.getValue() != null) {
                                String valueHum = snapshot.getValue() + "";
                                float floatHumChartValue;
                                floatHumChartValue = Float.parseFloat(valueHum);
                                floatHumChartValue /= 100f;
                                addEntry(finalFloatTempChartValue, floatHumChartValue);
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull @NotNull DatabaseError error) {
                            Log.e(TAG, "onCancelled: " + error);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Log.e(TAG, "onCancelled: " + error);
            }
        });

        mHumReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    mHumReference.setValue(snapshot.getValue());

                    String valueToShow =
                            String.format(
                                    "Humidity: %s %%",
                                    snapshot.getValue()
                            );
                    mTvHum.setText(valueToShow);
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Log.e(TAG, "onCancelled: " + error);
            }
        });

        mSetTemReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    String valueToShow = String.format(
                            "Set Value Temperature: %s °C",
                            snapshot.getValue().toString()
                    );
                    mTvSetTemp.setText(valueToShow);
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Log.e(TAG, "onCancelled: " + error);
            }
        });

        mSetHumReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    String valueToShow =
                            String.format(
                                    "Set Value Humidity: %s %%",
                                    snapshot.getValue().toString()
                            );
                    mTvSetHum.setText(valueToShow);
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Log.e(TAG, "onCancelled: " + error);
            }
        });

        mSumBtn.setOnClickListener(v -> {
            String setTemp = mSetTemp.getText().toString().trim();
            String setHum = mSetHum.getText().toString().trim();

            if (TextUtils.isEmpty(setTemp)) {
                mSetTemp.setError("Don't leave blank");
                return;
            }
            if (TextUtils.isEmpty(setHum)) {
                mSetHum.setError("Don't leave blank");
            } else {
                mSetTemReference.setValue(setTemp);
                mSetHumReference.setValue(setHum);
            }
//            float testValue = (float) Math.random();
//            float testValue2 = (float) Math.random();
//            addEntry(testValue, testValue2);
        });

        mFanReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    String value = snapshot.getValue() + "";
                    int convertValue = Integer.parseInt(value);
                    if (convertValue == 1) {
                        mFanOn.setBackgroundColor(getResources().getColor(R.color.red));
                    } else if (convertValue == 0) {
                        mFanOn.setBackgroundColor(getResources().getColor(R.color.purple_500));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Log.e(TAG, "onCancelled: " + error);
            }
        });

    }

    private void initView() {
        // UI
        mToolbar = findViewById(R.id.topAppBar);
        mSetTemp = findViewById(R.id.setTem);
        mSetHum = findViewById(R.id.setHum);
        mSumBtn = findViewById(R.id.summitbtn);
        mTvTemp = findViewById(R.id.Temp);
        mTvHum = findViewById(R.id.Hum);
        mTvSetTemp = findViewById(R.id.SetValueTem);
        mTvSetHum = findViewById(R.id.SetValueHum);
        mAuto = findViewById(R.id.autobtn);
        mManual = findViewById(R.id.manualbtn);
        mFanOn = findViewById(R.id.fanon);
        mFanOff = findViewById(R.id.fanoff);
        mPumpOn = findViewById(R.id.pumpon);
        mPumpOff = findViewById(R.id.pumpoff);

        // Chart
        mChart = findViewById(R.id.line_chart_main);
    }

    // Function to initialize chart
    private void initChart(LineChart chart) {
        chart.getDescription().setEnabled(false);

        chart.setTouchEnabled(false);
        chart.setDragEnabled(false);
        chart.setScaleEnabled(false);
        chart.setDrawGridBackground(false);

        chart.setPinchZoom(false);
        chart.getLegend().setEnabled(false);

        LineData data = new LineData();
        data.setValueTextColor(Color.BLACK);

        chart.setData(data);

        Legend l = chart.getLegend();
        l.setForm(Legend.LegendForm.LINE);
        l.setTextColor(Color.BLACK);

        XAxis xl = chart.getXAxis();
        xl.setDrawGridLines(false);
        xl.setEnabled(false);

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setTextColor(Color.BLACK);
        leftAxis.setAxisMaximum(100f);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setDrawGridLines(true);

        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setEnabled(false);
    }

    // add value to chart
    // note: value must be in range 0..1
    private void addEntry(float temp, float hum) {
        LineData data = mChart.getData();

        if (data != null) {
            ILineDataSet set = data.getDataSetByIndex(0);
            ILineDataSet set2 = data.getDataSetByIndex(1);

            if (set == null) {
                set = createSet();
                data.addDataSet(set);
            }

            if (set2 == null) {
                set2 = createSet2();
                data.addDataSet(set2);
            }

            data.addEntry(new Entry(set.getEntryCount(), temp * 100f), 0);
            data.addEntry(new Entry(set2.getEntryCount(), hum * 100f), 1);

            data.notifyDataChanged();
            data.setValueTextColor(Color.BLACK);
            mChart.notifyDataSetChanged();
            mChart.setVisibleXRangeMaximum(5);
            mChart.moveViewToX(data.getEntryCount());
            mChart.invalidate();
        }
    }

    // create data set for chart
    private LineDataSet createSet() {
        LineDataSet set = new LineDataSet(null, "Temp");

        set.setValueFormatter(new CustomTempValueFormatter());

        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColor(ColorTemplate.getHoloBlue());
        set.setCircleColor(Color.BLACK);
        set.setLineWidth(2f);
        set.setCircleRadius(4f);
        set.setFillAlpha(65);
        set.setValueTextColor(Color.BLACK);
        set.setValueTextSize(12f);
        set.setDrawValues(true);
        return set;
    }

    private LineDataSet createSet2() {
        LineDataSet set = new LineDataSet(null, "Hump");
        set.setValueFormatter(new CustomHumValueFormatter());
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColor(ColorTemplate.rgb("#F20000"));
        set.setCircleColor(Color.BLACK);
        set.setLineWidth(2f);
        set.setCircleRadius(4f);
        set.setFillAlpha(65);
        set.setValueTextColor(Color.BLACK);
        set.setValueTextSize(12f);
        set.setDrawValues(true);
        return set;
    }

    // Loop to test chart
    private void feedMultiple() {
        if (thread != null)
            thread.interrupt();

        final Runnable runnable = () -> {
            //addEntry();
        };

        thread = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {

                // Don't generate garbage runnable inside the loop.
                runOnUiThread(runnable);

                try {
                    Thread.sleep(25);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.logout) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getApplicationContext(), Login.class));
            finish();
        }
        return super.onOptionsItemSelected(item);

    }

}