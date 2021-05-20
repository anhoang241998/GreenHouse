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
import com.google.firebase.database.annotations.NotNull;


public class LeftSide extends AppCompatActivity {

    // log
    private final static String TAG = "LOI";
    Button mSumBtn1, mAuto1, mManual1, mPump1On, mPump1Off, mPump2On, mPump2Off,mPump3On,mPump3Off;
    // Firebase instance
    private FirebaseDatabase firebaseDatabase1 = FirebaseDatabase.getInstance();
    private DatabaseReference mRootReference1 = firebaseDatabase1.getReference();
    private DatabaseReference mpHReference = mRootReference1.child("Left").child("phValue");
    private DatabaseReference mTDSReference = mRootReference1.child("Left").child("tdsValue");
    private DatabaseReference mTempWReference = mRootReference1.child("Left").child("WaterTemp");
    private DatabaseReference mSetTDSReference = mRootReference1.child("Left").child("Control").child("SetValueTDS");
    private DatabaseReference mAuto1Reference = mRootReference1.child("Left").child("Control").child("Auto");
    private DatabaseReference mManual1Reference = mRootReference1.child("Left").child("Control").child("Manual");
    private DatabaseReference mPump1Reference = mRootReference1.child("Left").child("Control").child("Pump1");
    private DatabaseReference mPump2Reference = mRootReference1.child("Left").child("Control").child("Pump2");
    private DatabaseReference mPump3Reference = mRootReference1.child("Left").child("Control").child("Pump3");
    // UI
    private TextView mTvph, mTvtds, mTvtempW, mTvSetTDS;
    private EditText mSetTDS;
    private LineChart mChart1, mChart2;
    private Thread thread1;
    private Toolbar mToolbar1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_left_side);

        // Function to set id to view
        initView();
        initChart1(mChart1);
        initChart2(mChart2);
        setSupportActionBar(mToolbar1);
        mToolbar1.setNavigationOnClickListener(v -> {
            onBackPressed();
        });

        mAuto1.setOnClickListener(v -> {
            mAuto1Reference.setValue(1);
            mManual1Reference.setValue(0);

            Toast.makeText(LeftSide.this, "Auto", Toast.LENGTH_SHORT).show();
            mAuto1.setBackgroundColor(getResources().getColor(R.color.green));
            mManual1.setBackgroundColor(getResources().getColor(R.color.purple_500));

            mPump1On.setClickable(false);
            mPump1On.setFocusable(false);
            mPump1On.setFocusableInTouchMode(false);

            mPump1Off.setClickable(false);
            mPump1Off.setFocusable(false);
            mPump1Off.setFocusableInTouchMode(false);

            mPump2On.setClickable(false);
            mPump2On.setFocusable(false);
            mPump2On.setFocusableInTouchMode(false);

            mPump2Off.setClickable(false);
            mPump2Off.setFocusable(false);
            mPump2Off.setFocusableInTouchMode(false);

            mPump3On.setClickable(false);
            mPump3On.setFocusable(false);
            mPump3On.setFocusableInTouchMode(false);

            mPump3Off.setClickable(false);
            mPump3Off.setFocusable(false);
            mPump3Off.setFocusableInTouchMode(false);

        });

        mManual1.setOnClickListener(v -> {
            mManual1Reference.setValue(1);
            mAuto1Reference.setValue(0);

            Toast.makeText(LeftSide.this, "Manual", Toast.LENGTH_SHORT).show();
            mManual1.setBackgroundColor(getResources().getColor(R.color.green));
            mAuto1.setBackgroundColor(getResources().getColor(R.color.purple_500));

            mPump1On.setClickable(true);
            mPump1On.setFocusable(true);
            mPump1On.setFocusableInTouchMode(true);

            mPump1Off.setClickable(true);
            mPump1Off.setFocusable(true);
            mPump1Off.setFocusableInTouchMode(true);

            mPump2On.setClickable(true);
            mPump2On.setFocusable(true);
            mPump2On.setFocusableInTouchMode(true);

            mPump2Off.setClickable(true);
            mPump2Off.setFocusable(true);
            mPump2Off.setFocusableInTouchMode(true);

            mPump3On.setClickable(true);
            mPump3On.setFocusable(true);
            mPump3On.setFocusableInTouchMode(true);

            mPump3Off.setClickable(true);
            mPump3Off.setFocusable(true);
            mPump3Off.setFocusableInTouchMode(true);
        });

        mPump1On.setOnClickListener(v -> {
            mPump1Reference.setValue(1);
            Toast.makeText(LeftSide.this, "Fan On", Toast.LENGTH_SHORT).show();
            mPump1On.setBackgroundColor(getResources().getColor(R.color.green));
            mPump1Off.setBackgroundColor(getResources().getColor(R.color.purple_500));
        });

        mPump1Off.setOnClickListener(v -> {
            mPump1Reference.setValue(0);
            Toast.makeText(LeftSide.this, "Fan Off", Toast.LENGTH_SHORT).show();
            mPump1Off.setBackgroundColor(getResources().getColor(R.color.green));
            mPump1On.setBackgroundColor(getResources().getColor(R.color.purple_500));
        });

        mPump2On.setOnClickListener(v -> {
            mPump2Reference.setValue(1);
            Toast.makeText(LeftSide.this, "Pump On", Toast.LENGTH_SHORT).show();
            mPump2On.setBackgroundColor(getResources().getColor(R.color.green));
            mPump2Off.setBackgroundColor(getResources().getColor(R.color.purple_500));
        });

        mPump2Off.setOnClickListener(v -> {
            mPump2Reference.setValue(0);
            Toast.makeText(LeftSide.this, "Pump Off", Toast.LENGTH_SHORT).show();
            mPump2Off.setBackgroundColor(getResources().getColor(R.color.green));
            mPump2On.setBackgroundColor(getResources().getColor(R.color.purple_500));
        });

        mPump3On.setOnClickListener(v -> {
            mPump3Reference.setValue(1);
            Toast.makeText(LeftSide.this, "Pump On", Toast.LENGTH_SHORT).show();
            mPump3On.setBackgroundColor(getResources().getColor(R.color.green));
            mPump3Off.setBackgroundColor(getResources().getColor(R.color.purple_500));
        });

        mPump3Off.setOnClickListener(v -> {
            mPump3Reference.setValue(0);
            Toast.makeText(LeftSide.this, "Pump Off", Toast.LENGTH_SHORT).show();
            mPump3Off.setBackgroundColor(getResources().getColor(R.color.green));
            mPump3On.setBackgroundColor(getResources().getColor(R.color.purple_500));
        });

        mAuto1Reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    String value = snapshot.getValue() + "";
                    int convertValue = Integer.parseInt(value);
                    if (convertValue == 1) {
                        mAuto1.setBackgroundColor(getResources().getColor(R.color.green));
                    } else if (convertValue == 0) {
                        mAuto1.setBackgroundColor(getResources().getColor(R.color.purple_500));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Log.e(TAG, "onCancelled: " + error);
            }
        });

        mManual1Reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    String value = snapshot.getValue() + "";
                    int convertValue = Integer.parseInt(value);
                    if (convertValue == 1) {
                        mManual1.setBackgroundColor(getResources().getColor(R.color.green));
                    } else if (convertValue == 0) {
                        mAuto1.setBackgroundColor(getResources().getColor(R.color.green));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Log.e(TAG, "onCancelled: " + error);
            }
        });

        mPump1Reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    String value = snapshot.getValue() + "";
                    int convertValue = Integer.parseInt(value);
                    if (convertValue == 1) {
                        mPump1On.setBackgroundColor(getResources().getColor(R.color.green));
                        mPump1Off.setBackgroundColor(getResources().getColor(R.color.purple_500));
                    } else if (convertValue == 0) {
                        mPump1Off.setBackgroundColor(getResources().getColor(R.color.green));
                        mPump1On.setBackgroundColor(getResources().getColor(R.color.purple_500));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Log.e(TAG, "onCancelled: " + error);
            }
        });

        mPump2Reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    String value = snapshot.getValue() + "";
                    int convertValue = Integer.parseInt(value);
                    if (convertValue == 1) {
                        mPump2On.setBackgroundColor(getResources().getColor(R.color.green));
                        mPump2Off.setBackgroundColor(getResources().getColor(R.color.purple_500));
                    } else if (convertValue == 0) {
                        mPump2Off.setBackgroundColor(getResources().getColor(R.color.green));
                        mPump2On.setBackgroundColor(getResources().getColor(R.color.purple_500));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Log.e(TAG, "onCancelled: " + error);
            }
        });

        mPump3Reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    String value = snapshot.getValue() + "";
                    int convertValue = Integer.parseInt(value);
                    if (convertValue == 1) {
                        mPump3On.setBackgroundColor(getResources().getColor(R.color.green));
                        mPump3Off.setBackgroundColor(getResources().getColor(R.color.purple_500));
                    } else if (convertValue == 0) {
                        mPump3Off.setBackgroundColor(getResources().getColor(R.color.green));
                        mPump3On.setBackgroundColor(getResources().getColor(R.color.purple_500));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Log.e(TAG, "onCancelled: " + error);
            }
        });

        mpHReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    mpHReference.setValue(snapshot.getValue());

                    String valueToShow =
                            String.format(
                                    "pH Value = %s ",
                                    snapshot.getValue()
                            );
                    mTvph.setText(valueToShow);

                    String valuepH = snapshot.getValue() + "";
                    float floatpHChartValue;
                    floatpHChartValue = Float.parseFloat(valuepH);
                    floatpHChartValue /= 100f;

                    float finalFloatpHChartValue = floatpHChartValue;
                                mTempWReference.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                        if (snapshot.getValue() != null) {
                                            String valueTempW = snapshot.getValue() + "";
                                            float floatTempWChartValue;
                                            floatTempWChartValue = Float.parseFloat(valueTempW);
                                            floatTempWChartValue /= 100f;

                                            addEntry1(finalFloatpHChartValue, floatTempWChartValue);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                                    }
                                });

                            }

                        }

                        @Override
                        public void onCancelled(@NonNull @NotNull DatabaseError error) {
                            Log.e(TAG, "onCancelled: " + error);
                        }
                    });


        mTDSReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    mTDSReference.setValue(snapshot.getValue());

                    String valueToShow =
                            String.format(
                                    "TDS value = %s ppm",
                                    snapshot.getValue()
                            );
                    mTvtds.setText(valueToShow);

                    String valueTDS = snapshot.getValue() + "";
                    float floatTDSChartValue;
                    floatTDSChartValue = Float.parseFloat(valueTDS);
                    floatTDSChartValue /= 1000f;
                    addEntry2(floatTDSChartValue);
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Log.e(TAG, "onCancelled: " + error);
            }
        });

        mTempWReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    mTempWReference.setValue(snapshot.getValue());

                    String valueToShow =
                            String.format(
                                    "Temperature Water: %s °C",
                                    snapshot.getValue()
                            );
                    mTvtempW.setText(valueToShow);
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Log.e(TAG, "onCancelled: " + error);
            }
        });

        mSetTDSReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    String valueToShow = String.format(
                            "Set TDS Value = %s ppm",
                            snapshot.getValue().toString()
                    );
                    mTvSetTDS.setText(valueToShow);
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Log.e(TAG, "onCancelled: " + error);
            }
        });


        mSumBtn1.setOnClickListener(v -> {
            String setTDS = mSetTDS.getText().toString().trim();

            if (TextUtils.isEmpty(setTDS)) {
                mSetTDS.setError("Don't leave blank");
                return;
            } else {
                mSetTDSReference.setValue(setTDS);
            }
//            float testValue = (float) Math.random();
//            float testValue2 = (float) Math.random();
//            addEntry(testValue, testValue2);
        });

    }

    private void initView() {
        // UI
        mToolbar1 = findViewById(R.id.topAppBar1);
        mSetTDS = findViewById(R.id.setTDS);
        mSumBtn1 = findViewById(R.id.summitbtn2);
        mTvph = findViewById(R.id.Ph);
        mTvtds = findViewById(R.id.TDS);
        mTvtempW = findViewById(R.id.TempW);
        mTvSetTDS = findViewById(R.id.SetValueTDS);
        mAuto1 = findViewById(R.id.autobtn1);
        mManual1 = findViewById(R.id.manualbtn1);
        mPump1On = findViewById(R.id.pump1on);
        mPump1Off = findViewById(R.id.pump1off);
        mPump2On = findViewById(R.id.pump2on);
        mPump2Off = findViewById(R.id.pump2off);
        mPump3On = findViewById(R.id.pump3on);
        mPump3Off = findViewById(R.id.pump3off);
        // Chart
        mChart1 = findViewById(R.id.line_chart_main1);
        mChart2 = findViewById(R.id.line_chart_main2);
    }

    // Function to initialize chart
    private void initChart1(LineChart chart) {
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

    private void initChart2(LineChart chart2) {
        chart2.getDescription().setEnabled(false);

        chart2.setTouchEnabled(false);
        chart2.setDragEnabled(false);
        chart2.setScaleEnabled(false);
        chart2.setDrawGridBackground(false);

        chart2.setPinchZoom(false);
        chart2.getLegend().setEnabled(false);

        LineData data = new LineData();
        data.setValueTextColor(Color.BLACK);

        chart2.setData(data);

        Legend l = chart2.getLegend();
        l.setForm(Legend.LegendForm.LINE);
        l.setTextColor(Color.BLACK);

        XAxis xl = chart2.getXAxis();
        xl.setDrawGridLines(false);
        xl.setEnabled(false);

        YAxis leftAxis = chart2.getAxisLeft();
        leftAxis.setTextColor(Color.BLACK);
        leftAxis.setAxisMaximum(2000f);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setDrawGridLines(true);

        YAxis rightAxis = chart2.getAxisRight();
        rightAxis.setEnabled(false);
    }

    // add value to chart
    // note: value must be in range 0..1
    private void addEntry1(float pH, float TempW) {
        LineData data = mChart1.getData();

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

            data.addEntry(new Entry(set.getEntryCount(), pH * 100f), 0);
            data.addEntry(new Entry(set2.getEntryCount(), TempW * 100f), 1);

            data.notifyDataChanged();
            data.setValueTextColor(Color.BLACK);
            mChart1.notifyDataSetChanged();
            mChart1.setVisibleXRangeMaximum(5);
            mChart1.moveViewToX(data.getEntryCount());
            mChart1.invalidate();
        }
    }

    private void addEntry2(float TDS) {
        LineData data = mChart2.getData();

        if (data != null) {
            ILineDataSet set3 = data.getDataSetByIndex(0);

            if (set3 == null) {
                set3 = createSet3();
                data.addDataSet(set3);
            }

            data.addEntry(new Entry(set3.getEntryCount(), TDS * 1000f), 0);

            data.notifyDataChanged();
            data.setValueTextColor(Color.BLACK);
            mChart2.notifyDataSetChanged();
            mChart2.setVisibleXRangeMaximum(5);
            mChart2.moveViewToX(data.getEntryCount());
            mChart2.invalidate();
        }
    }

    // create data set for chart
    private LineDataSet createSet() {
        LineDataSet set = new LineDataSet(null, "pH");

        set.setValueFormatter(new CustompHValueFormatter());

        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColor(ColorTemplate.rgb("#00FF00"));
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
        LineDataSet set = new LineDataSet(null, "TempW");
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

    private LineDataSet createSet3() {
        LineDataSet set = new LineDataSet(null, "TDS");
        set.setValueFormatter(new CustomTDSValueFormatter());
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
        if (thread1 != null)
            thread1.interrupt();

        final Runnable runnable = () -> {
            //addEntry();
        };

        thread1 = new Thread(() -> {
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
        thread1.start();
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