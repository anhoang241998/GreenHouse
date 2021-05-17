package com.example.iotgreenhouse;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;;

import org.jetbrains.annotations.NotNull;


public class MainActivity extends AppCompatActivity {

    TextView textView, texTemp, texHum, texsetTemp, texsetHum;
    EditText settem, sethum;
    Button sumbtn, mAuto, mManual, mfanon, mfanoff, mpumpon, mpumpoff;

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

        mAuto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAutoReference.setValue(1);
                mManualReference.setValue(0);
            }
        });

        mManual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mManualReference.setValue(1);
                mAutoReference.setValue(0);
            }
        });

        mfanon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFanReference.setValue(1);
                Toast.makeText(MainActivity.this, "Fan On", Toast.LENGTH_SHORT).show();
            }
        });

        mfanoff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFanReference.setValue(0);
                Toast.makeText(MainActivity.this, "Fan Off", Toast.LENGTH_SHORT).show();
            }
        });

        mpumpon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPumpReference.setValue(1);
                Toast.makeText(MainActivity.this, "Pump On", Toast.LENGTH_SHORT).show();
            }
        });

        mpumpoff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPumpReference.setValue(0);
                Toast.makeText(MainActivity.this, "Pump Off", Toast.LENGTH_SHORT).show();
            }
        });

        mTempReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                mTempReference.setValue(snapshot.getValue());
                texTemp.setText("Temperature: " + snapshot.getValue() + " °C");
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
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

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