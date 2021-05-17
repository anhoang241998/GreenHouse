package com.example.iotgreenhouse;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
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

    TextView texTemp, texHum, texsetTemp, texsetHum;
    EditText settem, sethum;
    Button sumbtn, mAuto, mManual, mfanon, mfanoff, mpumpon, mpumpoff;

    private LineChart mChart;

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

    private Thread thread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        settem = findViewById(R.id.setTem);
        sethum = findViewById(R.id.setHum);
        sumbtn = findViewById(R.id.summitbtn);
        texTemp = findViewById(R.id.Temp);
        texHum = findViewById(R.id.Hum);
        texsetTemp = findViewById(R.id.SetValueTem);
        texsetHum = findViewById(R.id.SetValueHum);
        mAuto = findViewById(R.id.autobtn);
        mManual = findViewById(R.id.manualbtn);
        mfanon = findViewById(R.id.fanon);
        mfanoff = findViewById(R.id.fanoff);
        mpumpon = findViewById(R.id.pumpon);
        mpumpoff = findViewById(R.id.pumpoff);

        mChart = findViewById(R.id.line_chart_main);

        mAuto.setOnClickListener(v -> {
            mAutoReference.setValue(1);
            mManualReference.setValue(0);
        });

        mManual.setOnClickListener(v -> {
            mManualReference.setValue(1);
            mAutoReference.setValue(0);
        });

        mfanon.setOnClickListener(v -> {
            mFanReference.setValue(1);
            Toast.makeText(MainActivity.this, "Fan On", Toast.LENGTH_SHORT).show();
        });

        mfanoff.setOnClickListener(v -> {
            mFanReference.setValue(0);
            Toast.makeText(MainActivity.this, "Fan Off", Toast.LENGTH_SHORT).show();
        });

        mpumpon.setOnClickListener(v -> {
            mPumpReference.setValue(1);
            Toast.makeText(MainActivity.this, "Pump On", Toast.LENGTH_SHORT).show();
        });

        mpumpoff.setOnClickListener(v -> {
            mPumpReference.setValue(0);
            Toast.makeText(MainActivity.this, "Pump Off", Toast.LENGTH_SHORT).show();
        });

        mTempReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    mTempReference.setValue(snapshot.getValue());
                    texTemp.setText("Temperature: " + snapshot.getValue() + " °C");
                    String convertedToString = snapshot.getValue() + "";
                    float convertValue;
                    convertValue = Float.parseFloat(convertedToString);
                    convertValue /= 100f;
                    addEntry(convertValue);
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        mHumReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                mHumReference.setValue(snapshot.getValue());
                texHum.setText("Hummidity: " + snapshot.getValue() + " %");
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        mSetTemReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                texsetTemp.setText("Set Value Temperature: " + snapshot.getValue().toString() + " °C");
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        mSetHumReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                texsetHum.setText("Set Value Hummidity: " + snapshot.getValue().toString() + " %");
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        sumbtn.setOnClickListener(v -> {
            String setTemp = settem.getText().toString().trim();
            String setHum = sethum.getText().toString().trim();
            if (TextUtils.isEmpty(setTemp)) {
                settem.setError("Don't leave blank");
                return;
            }
            if (TextUtils.isEmpty(setHum)) {
                sethum.setError("Don't leave blank");
                return;
            } else {
                mSetTemReference.setValue(setTemp);
                mSetHumReference.setValue(setHum);
            }
//            float testValue = (float) Math.random();
//            addEntry(testValue);
        });

        Toolbar toolbar = findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);

        initChart(mChart);
//        addEntry(31f);
//        feedMultiple();

    }

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

    private void addEntry(float temp) {
        LineData data = mChart.getData();

        if (data != null) {
            ILineDataSet set = data.getDataSetByIndex(0);

            if (set == null) {
                set = createSet();
                data.addDataSet(set);
            }

            data.addEntry(new Entry(set.getEntryCount(), temp * 100f), 0);

            data.notifyDataChanged();
            data.setValueFormatter(new CustomValueFormatter());
            data.setValueTextColor(Color.BLACK);
            mChart.notifyDataSetChanged();
            mChart.setVisibleXRangeMaximum(5);
            mChart.moveViewToX(data.getEntryCount());
            mChart.invalidate();
        }
    }

    private LineDataSet createSet() {
        LineDataSet set = new LineDataSet(null, "Temp");
        set.setValueFormatter(new CustomValueFormatter());
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColor(ColorTemplate.getHoloBlue());
        set.setCircleColor(Color.BLACK);
        set.setLineWidth(2f);
        set.setCircleRadius(4f);
        set.setFillAlpha(65);
        set.setFillColor(ColorTemplate.getHoloBlue());
        set.setHighLightColor(Color.rgb(244, 117, 117));
        set.setValueTextColor(Color.BLACK);
        set.setValueTextSize(16f);
        set.setDrawValues(true);
        return set;
    }

    private void feedMultiple() {
        if (thread != null)
            thread.interrupt();

        final Runnable runnable = () -> {

        };//addEntry();

        thread = new Thread(new Runnable() {

            @Override
            public void run() {
                for (int i = 0; i < 1000; i++) {

                    // Don't generate garbage runnables inside the loop.
                    runOnUiThread(runnable);

                    try {
                        Thread.sleep(25);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        thread.start();
    }

//    private LineDataSet createSet2() {
//        set2 = new LineDataSet(null, "Humidity");
//        set2.setAxisDependency(YAxis.AxisDependency.RIGHT);
//
//        set2.setMode(LineDataSet.Mode.CUBIC_BEZIER);
//        set2.setCubicIntensity(0.2f);
//
//        set2.setColor(Color.BLACK);
//        set2.setCircleColor(Color.BLACK);
//        set2.setDrawFilled(false);
//        set2.setDrawCircles(false);
//        set2.setLineWidth(2f);
//        set2.setFillAlpha(65);
//        set2.setFillColor(ColorTemplate.getHoloBlue());
//        set2.setHighLightColor(Color.rgb(244, 117, 117));
//        set2.setValueTextColor(Color.WHITE);
//        set2.setValueTextSize(9f);
//        set2.setDrawValues(false);
//
//        set2.setLabel("");
//        return set2;
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(), Login.class));
                finish();
            default:
                return super.onOptionsItemSelected(item);
        }

    }

}