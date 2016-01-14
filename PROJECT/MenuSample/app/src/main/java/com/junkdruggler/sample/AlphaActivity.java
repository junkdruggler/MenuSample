package com.junkdruggler.sample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.jahirfiquitiva.paperboard.activities.MainActivity;


public class AlphaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(AlphaActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }



}
