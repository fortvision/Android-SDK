package com.fortvision.minisitesdemo;

import android.Manifest;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.fortvision.minisites.MiniSites;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        if (savedInstanceState != null)
            onRestoreInstanceState(savedInstanceState);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) findViewById(R.id.button).getLayoutParams();
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Editable publisherId = ((EditText) findViewById(R.id.publisherId)).getText();
                Editable categoryId = ((EditText) findViewById(R.id.categoryId)).getText();
                showBuble(publisherId, categoryId);
            }
        });
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 566);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        Editable publisherId = ((EditText) findViewById(R.id.publisherId)).getText();
        Editable categoryId = ((EditText) findViewById(R.id.categoryId)).getText();
        if (!TextUtils.isEmpty(publisherId))
            showBuble(publisherId, categoryId);
    }

    private void showBuble(Editable publisherId, Editable categoryId) {
        if (TextUtils.isEmpty(categoryId))
            MiniSites.trigger(this, publisherId.toString());
        else
            MiniSites.trigger(this, publisherId.toString(), categoryId.toString());
    }

}
