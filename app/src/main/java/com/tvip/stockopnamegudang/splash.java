package com.tvip.stockopnamegudang;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.vectordrawable.graphics.drawable.PathInterpolatorCompat;

public class splash extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    private int waktu_loading = 3000;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                splash splash = splash.this;
                splash.sharedPreferences = splash.getSharedPreferences("user_details", 0);
                if (splash.this.sharedPreferences.getString("nik_baru", (String) null) == null) {
                    splash.this.startActivity(new Intent(splash.this, Login.class));
                } else {
                    splash.this.startActivity(new Intent(splash.this, menu_stockopname.class));
                }
            }
        }, (long) this.waktu_loading);
    }
}
