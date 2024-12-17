package com.tvip.stockopnamegudang;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.card.MaterialCardView;

public class menu_validasi extends AppCompatActivity {
    MaterialCardView layout_harian, layout_bulanan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_validasi);
        layout_harian = findViewById(R.id.layout_harian);
        layout_bulanan = findViewById(R.id.layout_bulanan);

        layout_harian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), validasi_stockopname.class);
                startActivity(intent);
            }
        });

        layout_bulanan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), validasi_bulanan.class);
                startActivity(intent);
            }
        });
    }
}