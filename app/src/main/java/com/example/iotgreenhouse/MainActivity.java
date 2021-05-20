package com.example.iotgreenhouse;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private ImageView right, left;
    private Toolbar mToolbar2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initview2();
        setSupportActionBar(mToolbar2);

        right.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), RightSide.class)));
        left.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), LeftSide.class)));
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
    private void initview2(){
        mToolbar2 = findViewById(R.id.topAppBar2);
        right = findViewById(R.id.imageView1);
        left = findViewById(R.id.imageView2);
    }
}