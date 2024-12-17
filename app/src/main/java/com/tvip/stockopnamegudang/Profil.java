package com.tvip.stockopnamegudang;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Profil extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    TextView dept_text;
    TextView jabatan_text;
    Button logout;
    TextView lokasi_text;
    TextView nama_text;
    TextView nik_text;
    SharedPreferences sharedPreferences;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);
        HttpsTrustManager.allowAllSSL();
        this.nik_text = (TextView) findViewById(R.id.nik_text);
        this.nama_text = (TextView) findViewById(R.id.nama_text);
        this.jabatan_text = (TextView) findViewById(R.id.jabatan_text);
        this.dept_text = (TextView) findViewById(R.id.dept_text);
        this.lokasi_text = (TextView) findViewById(R.id.lokasi_text);
        Button button = (Button) findViewById(R.id.logout);
        this.logout = button;
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Profil.this);
                alertDialogBuilder.setTitle((CharSequence) "Apakah anda yakin ingin logout?");
                alertDialogBuilder.setCancelable(false).setPositiveButton((CharSequence) "Ya", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        SharedPreferences.Editor editor = Profil.this.sharedPreferences.edit();
                        editor.clear();
                        editor.apply();
                        Intent inetnt = new Intent(Profil.this.getBaseContext(), Login.class);
                        inetnt.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        Profil.this.startActivity(inetnt);
                    }
                }).setNegativeButton((CharSequence) "Tidak", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
                alertDialogBuilder.create().show();
            }
        });
        SharedPreferences sharedPreferences2 = getSharedPreferences("user_details", 0);
        this.sharedPreferences = sharedPreferences2;
        StringRequest stringRequest = new StringRequest(0, "https://hrd.tvip.co.id/rest_server/master/karyawan/index?nik_baru=" + sharedPreferences2.getString("nik_baru", (String) null), new Response.Listener<String>() {
            public void onResponse(String response) {
                try {
                    JSONObject obj = new JSONObject(response);
                    if (obj.getString(NotificationCompat.CATEGORY_STATUS).equals("true")) {
                        JSONArray movieArray = obj.getJSONArray("data");
                        for (int i = 0; i < movieArray.length(); i++) {
                            JSONObject movieObject = movieArray.getJSONObject(i);
                            Profil.this.nik_text.setText(movieObject.getString("nik_baru"));
                            Profil.this.nama_text.setText(movieObject.getString("nama_karyawan_struktur"));
                            Profil.this.jabatan_text.setText(movieObject.getString("jabatan_karyawan"));
                            Profil.this.dept_text.setText(movieObject.getString("dept_struktur"));
                            Profil.this.lokasi_text.setText(movieObject.getString("lokasi_struktur"));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
            }
        }) {
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("Authorization", "Basic " + Base64.encodeToString(String.format("%s:%s", new Object[]{"admin", "Databa53"}).getBytes(), 0));
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(500000, 1, 1.0f));
        Volley.newRequestQueue(this).add(stringRequest);
        BottomNavigationView bottomNavigationView2 = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        this.bottomNavigationView = bottomNavigationView2;
        bottomNavigationView2.setSelectedItemId(R.id.bottom_nav_profil);
        this.bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.bottom_nav_home:
                        Profil.this.startActivity(new Intent(Profil.this, menu_stockopname.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        return true;
                    default:
                        return true;
                }
            }
        });
    }
}
