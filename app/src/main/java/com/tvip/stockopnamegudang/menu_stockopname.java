package com.tvip.stockopnamegudang;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class menu_stockopname extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;

    RequestQueue requestQueue3, requestQueue10;

    Chip total;

    public static String kategoriSparepart;
    RequestQueue requestQueue;
    SharedPreferences sharedPreferences;
    TextView text_nama;
    MaterialCardView update_stock, validasi_stock, bulanan_stock;

    SweetAlertDialog pDialog;
    static String warehouse, warehousechoose;

    ArrayList<String> warehouses = new ArrayList<>();
    ArrayList<String> IdWarehouses = new ArrayList<>();

    static String[] kategori = {"All", "Engine", "Power Train", "Chasis & Tools", "Electrical", "Cabin", "Tyre", "Dan Lain-lain"};

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_stockopname);
        SharedPreferences sharedPreferences2 = getSharedPreferences("user_details", 0);
        this.sharedPreferences = sharedPreferences2;
        String nama = sharedPreferences2.getString("nama", (String) null);

        total = findViewById(R.id.total);

        String nik_baru = sharedPreferences2.getString("nik_baru", (String) null);
        


        this.text_nama = (TextView) findViewById(R.id.text_nama);
        bulanan_stock = findViewById(R.id.bulanan_stock);
        validasi_stock = findViewById(R.id.validasi_stock);
        MaterialCardView materialCardView = (MaterialCardView) findViewById(R.id.update_stock);
        this.update_stock = materialCardView;
        materialCardView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                menu_stockopname.this.startActivity(new Intent(menu_stockopname.this.getApplicationContext(), list_stockopname.class));
            }
        });
        sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
        String szEmployeeId = sharedPreferences.getString("nik_baru", null);
        StringRequest stringRequest = new StringRequest(0, "https://apisec.tvip.co.id/rest_server_stock_opname/Stock/index_nik?employee_id=" + szEmployeeId, new Response.Listener<String>() {
            public void onResponse(String response) {
                try {
                    JSONObject obj = new JSONObject(response);
                    if (obj.getString(NotificationCompat.CATEGORY_STATUS).equals("true")) {
                        JSONArray movieArray = obj.getJSONArray("data");
                        for (int i = 0; i < movieArray.length(); i++) {
                            JSONObject movieObject = movieArray.getJSONObject(i);

                            warehouse = movieObject.getString("warehouse");

                            getWarehouse();

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

        bulanan_stock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cekTanggal();

            }
        });

        validasi_stock.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                menu_stockopname.this.startActivity(new Intent(menu_stockopname.this.getApplicationContext(), menu_validasi.class));
            }
        });

        if(nik_baru.equals("0100030300")){
            validasi_stock.setVisibility(View.VISIBLE);
            update_stock.setVisibility(View.GONE);
            bulanan_stock.setVisibility(View.GONE);
        } else {
            validasi_stock.setVisibility(View.GONE);
            update_stock.setVisibility(View.VISIBLE);
        }

        BottomNavigationView bottomNavigationView2 = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        this.bottomNavigationView = bottomNavigationView2;
        bottomNavigationView2.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.bottom_nav_profil:
                        menu_stockopname.this.startActivity(new Intent(menu_stockopname.this, Profil.class).addFlags(604045312));
                        return true;
                    default:
                        return true;
                }
            }
        });
        this.text_nama.setText(nama);
    }

    private void getTotalCount() {
        sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
        String szEmployeeId = sharedPreferences.getString("nik_baru", null);
        StringRequest stringRequest = new StringRequest(0, "https://apisec.tvip.co.id/rest_server_stock_opname/Stock/index_nik?employee_id=" + szEmployeeId, new Response.Listener<String>() {
            public void onResponse(String response) {
                try {
                    JSONObject obj = new JSONObject(response);
                    if (obj.getString(NotificationCompat.CATEGORY_STATUS).equals("true")) {
                        JSONArray movieArray = obj.getJSONArray("data");
                        for (int i = 0; i < movieArray.length(); i++) {
                            JSONObject movieObject = movieArray.getJSONObject(i);

                            String gudang = movieObject.getString("warehouse");

                            getCountALl(gudang);

                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            private void getCountALl(String gudang) {
                StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/rest_server_stock_opname/Stock/index_stock_bulanan_count_all"+"?warehouse=" + gudang,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                int alldata = 0;
                                int dikerjakan = 0;
                                try {
                                    JSONObject obj = new JSONObject(response);
                                    final JSONArray movieArray = obj.getJSONArray("data");

                                    for (int i = 0; i < movieArray.length(); i++) {


                                        final JSONObject movieObject = movieArray.getJSONObject(i);

                                        dikerjakan = movieObject.getInt("done");
                                        alldata = movieObject.getInt("alldata");


                                        total.setText(String.valueOf(alldata - dikerjakan) + " Item");
                                        if(dikerjakan != alldata){
                                            total.setChipStrokeColor(ColorStateList.valueOf(Color.parseColor("#FEC0C0")));
                                            total.setChipBackgroundColor(ColorStateList.valueOf(Color.parseColor("#FFF1F1")));
                                            total.setTextColor(Color.parseColor("#FB4141"));
                                        } else {
                                            total.setChipStrokeColor(ColorStateList.valueOf(Color.parseColor("#B9EED0")));
                                            total.setChipBackgroundColor(ColorStateList.valueOf(Color.parseColor("#E8FFF2")));
                                            total.setTextColor(Color.parseColor("#2ECC71"));
                                        }




                                        // adapter.notifyDataSetChanged();

                                    }
                                } catch(JSONException e){
                                    e.printStackTrace();

                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                pDialog.dismissWithAnimation();
                            }
                        })

                {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        HashMap<String, String> params = new HashMap<String, String>();
                        String creds = String.format("%s:%s", "admin", "Databa53");
                        String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.DEFAULT);
                        params.put("Authorization", auth);
                        return params;
                    }
                };

                stringRequest.setRetryPolicy(
                        new DefaultRetryPolicy(
                                15000,
                                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                        ));
                if (requestQueue3 == null) {
                    requestQueue3 = Volley.newRequestQueue(menu_stockopname.this);
                    requestQueue3.add(stringRequest);
                } else {
                    requestQueue3.add(stringRequest);
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
    }

    private void getWarehouse() {
        StringRequest rest = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/rest_server_stock_opname/Stock/index_warehouse?&warehouse=" + warehouse,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getString("status").equals("true")) {
                                JSONArray jsonArray = jsonObject.getJSONArray("data");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                    String id = jsonObject1.getString("pid");
                                    String nama = jsonObject1.getString("name");
                                    warehouses.add(nama);
                                    IdWarehouses.add(id);

                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                String creds = String.format("%s:%s", "admin", "Databa53");
                String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.DEFAULT);
                params.put("Authorization", auth);
                return params;
            }
        };
        rest.setRetryPolicy(new DefaultRetryPolicy(
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(menu_stockopname.this);
            requestQueue.getCache().clear();
            requestQueue.add(rest);
        } else {
            requestQueue.add(rest);
        }
    }

    private void cekTanggal() {
        pDialog = new SweetAlertDialog(menu_stockopname.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Harap Menunggu");
        pDialog.setCancelable(false);
        pDialog.show();

        System.out.println("LINK Tanggal = " + "https://apisec.tvip.co.id/rest_server_stock_opname/Stock/index_cek_tanggal?warehouse=" + warehouse);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/rest_server_stock_opname/Stock/index_cek_tanggal?warehouse=" + warehouse,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pDialog.dismissWithAnimation();
                        Dialog filter = new Dialog(menu_stockopname.this);
                        filter.setContentView(R.layout.pilih_gudang);
                        filter.setCancelable(false);
                        filter.show();

                        AutoCompleteTextView pilih_gudang = filter.findViewById(R.id.pilih_gudang);
                        AutoCompleteTextView pilih_kategori = filter.findViewById(R.id.pilih_kategori);

                        Chip total2 = filter.findViewById(R.id.total_gudang);

                        Button batal = filter.findViewById(R.id.batal);
                        Button lanjutkan = filter.findViewById(R.id.lanjutkan);
                        pilih_gudang.setAdapter(new ArrayAdapter<String>(menu_stockopname.this, R.layout.custom_expandable_list_item, warehouses));

                        pilih_kategori.setAdapter(new ArrayAdapter<String>(menu_stockopname.this, R.layout.custom_expandable_list_item, kategori));


                        pilih_gudang.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                warehousechoose = IdWarehouses.get(position).toString();
                                pilih_kategori.getText().clear();

                                StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/rest_server_stock_opname/Stock/index_stock_bulanan_count_all"+"?warehouse=" + warehousechoose,
                                        new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response) {
                                                int alldata = 0;
                                                int dikerjakan = 0;
                                                try {
                                                    JSONObject obj = new JSONObject(response);
                                                    final JSONArray movieArray = obj.getJSONArray("data");

                                                    for (int i = 0; i < movieArray.length(); i++) {


                                                        final JSONObject movieObject = movieArray.getJSONObject(i);

                                                        dikerjakan = movieObject.getInt("done");
                                                        alldata = movieObject.getInt("alldata");


                                                        total2.setText(String.valueOf(dikerjakan + "/" + alldata) + " Item");
                                                        if(dikerjakan != alldata){
                                                            lanjutkan.setText("Lanjutkan");

                                                            total2.setChipStrokeColor(ColorStateList.valueOf(Color.parseColor("#FEC0C0")));
                                                            total2.setChipBackgroundColor(ColorStateList.valueOf(Color.parseColor("#FFF1F1")));
                                                            total2.setTextColor(Color.parseColor("#FB4141"));

                                                            lanjutkan.setOnClickListener(new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View v) {
                                                                    if(pilih_gudang.getText().toString().length() == 0){
                                                                        pilih_gudang.setError("Wajib Di isi");
                                                                    } else if(pilih_kategori.getText().toString().length() == 0){
                                                                        pilih_kategori.setError("Wajib Di isi");
                                                                    } else {
                                                                        filter.dismiss();
                                                                        kategoriSparepart = pilih_kategori.getText().toString();
                                                                        Intent intent = new Intent(getApplicationContext(), accept.class);
                                                                        startActivity(intent);
                                                                    }

                                                                }
                                                            });

                                                        } else {
                                                            total2.setChipStrokeColor(ColorStateList.valueOf(Color.parseColor("#B9EED0")));
                                                            total2.setChipBackgroundColor(ColorStateList.valueOf(Color.parseColor("#E8FFF2")));
                                                            total2.setTextColor(Color.parseColor("#2ECC71"));
                                                            lanjutkan.setText("Selesaikan");
                                                            lanjutkan.setOnClickListener(new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View v) {
                                                                    if(pilih_gudang.getText().toString().length() == 0){
                                                                        pilih_gudang.setError("Wajib Di isi");
                                                                    } else {
                                                                        pDialog = new SweetAlertDialog(menu_stockopname.this, SweetAlertDialog.PROGRESS_TYPE);
                                                                        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                                                                        pDialog.setTitleText("Harap Menunggu");
                                                                        pDialog.setCancelable(false);
                                                                        pDialog.show();

                                                                        StringRequest stringRequest2 = new StringRequest(Request.Method.PUT, "https://apisec.tvip.co.id/rest_server_stock_opname/Stock/index_stockBulanan_status",
                                                                                new Response.Listener<String>() {

                                                                                    @Override
                                                                                    public void onResponse(String response) {
                                                                                        pDialog.dismissWithAnimation();
                                                                                        new SweetAlertDialog(menu_stockopname.this, SweetAlertDialog.SUCCESS_TYPE)
                                                                                                .setContentText("Data Sudah Diupdate")
                                                                                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                                                                    @Override
                                                                                                    public void onClick(SweetAlertDialog sDialog) {
                                                                                                        sDialog.dismissWithAnimation();
                                                                                                        filter.dismiss();
                                                                                                    }
                                                                                                })
                                                                                                .show();
                                                                                    }
                                                                                }, new Response.ErrorListener() {
                                                                            @Override
                                                                            public void onErrorResponse(VolleyError error) {
                                                                                pDialog.dismissWithAnimation();
                                                                                new SweetAlertDialog(menu_stockopname.this, SweetAlertDialog.SUCCESS_TYPE)
                                                                                        .setContentText("Gudang Ini Sudah Diselesaikan")
                                                                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                                                            @Override
                                                                                            public void onClick(SweetAlertDialog sDialog) {
                                                                                                sDialog.dismissWithAnimation();
                                                                                                filter.dismiss();
                                                                                            }
                                                                                        })
                                                                                        .show();
                                                                            }

                                                                        }) {
                                                                            @Override
                                                                            public Map<String, String> getHeaders() throws AuthFailureError {
                                                                                HashMap<String, String> params = new HashMap<String, String>();
                                                                                String creds = String.format("%s:%s", "admin", "Databa53");
                                                                                String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.DEFAULT);
                                                                                params.put("Authorization", auth);
                                                                                return params;
                                                                            }

                                                                            @Override
                                                                            protected Map<String, String> getParams() throws AuthFailureError {
                                                                                Map<String, String> params = new HashMap<String, String>();
                                                                                sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
                                                                                String nik_baru = sharedPreferences.getString("nik_baru", null);

                                                                                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
                                                                                String currentDateandTime2 = sdf2.format(new Date());

                                                                                SimpleDateFormat year = new SimpleDateFormat("yyyy");
                                                                                String years = year.format(new Date());

                                                                                SimpleDateFormat month = new SimpleDateFormat("M");
                                                                                String months = month.format(new Date());

                                                                                params.put("bulan", months);
                                                                                params.put("tahun", years);
                                                                                params.put("warehouse", menu_stockopname.warehousechoose);

                                                                                params.put("tanggal_selesai", currentDateandTime2);
                                                                                params.put("nik_pic", nik_baru);



                                                                                params.put("nik_pic", nik_baru);

                                                                                return params;
                                                                            }

                                                                        };
                                                                        stringRequest2.setRetryPolicy(
                                                                                new DefaultRetryPolicy(
                                                                                        5000,
                                                                                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                                                                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                                                                                )
                                                                        );
                                                                        RequestQueue requestQueue2 = Volley.newRequestQueue(menu_stockopname.this);
                                                                        requestQueue2.getCache().clear();
                                                                        requestQueue2.add(stringRequest2);
                                                                    }

                                                                }
                                                            });
                                                        }




                                                        // adapter.notifyDataSetChanged();

                                                    }
                                                } catch(JSONException e){
                                                    e.printStackTrace();

                                                }
                                            }
                                        },
                                        new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                pDialog.dismissWithAnimation();
                                            }
                                        })

                                {
                                    @Override
                                    public Map<String, String> getHeaders() throws AuthFailureError {
                                        HashMap<String, String> params = new HashMap<String, String>();
                                        String creds = String.format("%s:%s", "admin", "Databa53");
                                        String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.DEFAULT);
                                        params.put("Authorization", auth);
                                        return params;
                                    }
                                };

                                stringRequest.setRetryPolicy(
                                        new DefaultRetryPolicy(
                                                15000,
                                                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                                        ));
                                if (requestQueue10 == null) {
                                    requestQueue10 = Volley.newRequestQueue(menu_stockopname.this);
                                    requestQueue10.add(stringRequest);
                                } else {
                                    requestQueue10.add(stringRequest);
                                }
                            }
                        });

                        batal.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                filter.dismiss();
                            }
                        });


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pDialog.dismissWithAnimation();
                        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMdd");
                        String currentDateandTime2 = sdf2.format(new Date());

                        SimpleDateFormat year = new SimpleDateFormat("yyyy");
                        String years = year.format(new Date());

                        SimpleDateFormat month = new SimpleDateFormat("MM");
                        String months = month.format(new Date());

                        String tanggal_bulanan = years +  months + "20";

                        System.out.println("Tanggal Hari ini " + currentDateandTime2);
                        System.out.println("Tanggal Batas " + tanggal_bulanan);

                        if(Integer.parseInt(currentDateandTime2) < Integer.parseInt(tanggal_bulanan)){
                            new SweetAlertDialog(menu_stockopname.this, SweetAlertDialog.ERROR_TYPE)
                                    .setContentText("Maaf, fitur ini hanya bisa digunakan setiap tanggal 20")
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sDialog) {
                                            sDialog.dismissWithAnimation();
                                        }
                                    })
                                    .show();
                        } else {
                            Dialog filter = new Dialog(menu_stockopname.this);
                            filter.setContentView(R.layout.pilih_gudang);
                            filter.setCancelable(false);
                            filter.show();

                            AutoCompleteTextView pilih_gudang = filter.findViewById(R.id.pilih_gudang);
                            AutoCompleteTextView pilih_kategori = filter.findViewById(R.id.pilih_kategori);
                            Chip total2 = filter.findViewById(R.id.total_gudang);

                            Button batal = filter.findViewById(R.id.batal);
                            Button lanjutkan = filter.findViewById(R.id.lanjutkan);
                            pilih_gudang.setAdapter(new ArrayAdapter<String>(menu_stockopname.this, R.layout.custom_expandable_list_item, warehouses));

                            pilih_kategori.setAdapter(new ArrayAdapter<String>(menu_stockopname.this, R.layout.custom_expandable_list_item, kategori));


                            pilih_gudang.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    warehousechoose = IdWarehouses.get(position).toString();
                                    pilih_kategori.getText().clear();

                                    StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/rest_server_stock_opname/Stock/index_stock_bulanan_count_all"+"?warehouse=" + warehousechoose,
                                            new Response.Listener<String>() {
                                                @Override
                                                public void onResponse(String response) {
                                                    int alldata = 0;
                                                    int dikerjakan = 0;
                                                    try {
                                                        JSONObject obj = new JSONObject(response);
                                                        final JSONArray movieArray = obj.getJSONArray("data");

                                                        for (int i = 0; i < movieArray.length(); i++) {


                                                            final JSONObject movieObject = movieArray.getJSONObject(i);

                                                            dikerjakan = movieObject.getInt("done");
                                                            alldata = movieObject.getInt("alldata");


                                                            total2.setText(String.valueOf(dikerjakan + "/" + alldata) + " Item");
                                                            if(dikerjakan != alldata){
                                                                total2.setChipStrokeColor(ColorStateList.valueOf(Color.parseColor("#FEC0C0")));
                                                                total2.setChipBackgroundColor(ColorStateList.valueOf(Color.parseColor("#FFF1F1")));
                                                                total2.setTextColor(Color.parseColor("#FB4141"));
                                                                lanjutkan.setText("Lanjutkan");

                                                                lanjutkan.setOnClickListener(new View.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(View v) {
                                                                        if(pilih_gudang.getText().toString().length() == 0){
                                                                            pilih_gudang.setError("Wajib Di isi");
                                                                        } else if(pilih_kategori.getText().toString().length() == 0){
                                                                            pilih_kategori.setError("Wajib Di isi");
                                                                        } else {
                                                                            filter.dismiss();
                                                                            kategoriSparepart = pilih_kategori.getText().toString();
                                                                            Intent intent = new Intent(getApplicationContext(), accept.class);
                                                                            startActivity(intent);
                                                                        }

                                                                    }
                                                                });

                                                            } else {
                                                                total2.setChipStrokeColor(ColorStateList.valueOf(Color.parseColor("#B9EED0")));
                                                                total2.setChipBackgroundColor(ColorStateList.valueOf(Color.parseColor("#E8FFF2")));
                                                                total2.setTextColor(Color.parseColor("#2ECC71"));
                                                                lanjutkan.setText("Selesaikan");
                                                                lanjutkan.setOnClickListener(new View.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(View v) {
                                                                        if(pilih_gudang.getText().toString().length() == 0){
                                                                            pilih_gudang.setError("Wajib Di isi");
                                                                        }  else {
                                                                            pDialog = new SweetAlertDialog(menu_stockopname.this, SweetAlertDialog.PROGRESS_TYPE);
                                                                            pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                                                                            pDialog.setTitleText("Harap Menunggu");
                                                                            pDialog.setCancelable(false);
                                                                            pDialog.show();

                                                                            StringRequest stringRequest2 = new StringRequest(Request.Method.PUT, "https://apisec.tvip.co.id/rest_server_stock_opname/Stock/index_stockBulanan_status",
                                                                                    new Response.Listener<String>() {

                                                                                        @Override
                                                                                        public void onResponse(String response) {
                                                                                            pDialog.dismissWithAnimation();
                                                                                            new SweetAlertDialog(menu_stockopname.this, SweetAlertDialog.SUCCESS_TYPE)
                                                                                                    .setContentText("Data Sudah Diupdate")
                                                                                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                                                                        @Override
                                                                                                        public void onClick(SweetAlertDialog sDialog) {
                                                                                                            sDialog.dismissWithAnimation();
                                                                                                            filter.dismiss();
                                                                                                        }
                                                                                                    })
                                                                                                    .show();
                                                                                        }
                                                                                    }, new Response.ErrorListener() {
                                                                                @Override
                                                                                public void onErrorResponse(VolleyError error) {
                                                                                    pDialog.dismissWithAnimation();
                                                                                    new SweetAlertDialog(menu_stockopname.this, SweetAlertDialog.SUCCESS_TYPE)
                                                                                            .setContentText("Gudang Ini Sudah Diselesaikan")
                                                                                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                                                                @Override
                                                                                                public void onClick(SweetAlertDialog sDialog) {
                                                                                                    sDialog.dismissWithAnimation();
                                                                                                    filter.dismiss();
                                                                                                }
                                                                                            })
                                                                                            .show();
                                                                                }

                                                                            }) {
                                                                                @Override
                                                                                public Map<String, String> getHeaders() throws AuthFailureError {
                                                                                    HashMap<String, String> params = new HashMap<String, String>();
                                                                                    String creds = String.format("%s:%s", "admin", "Databa53");
                                                                                    String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.DEFAULT);
                                                                                    params.put("Authorization", auth);
                                                                                    return params;
                                                                                }

                                                                                @Override
                                                                                protected Map<String, String> getParams() throws AuthFailureError {
                                                                                    Map<String, String> params = new HashMap<String, String>();
                                                                                    sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
                                                                                    String nik_baru = sharedPreferences.getString("nik_baru", null);

                                                                                    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
                                                                                    String currentDateandTime2 = sdf2.format(new Date());

                                                                                    SimpleDateFormat year = new SimpleDateFormat("yyyy");
                                                                                    String years = year.format(new Date());

                                                                                    SimpleDateFormat month = new SimpleDateFormat("M");
                                                                                    String months = month.format(new Date());

                                                                                    params.put("bulan", months);
                                                                                    params.put("tahun", years);
                                                                                    params.put("warehouse", menu_stockopname.warehousechoose);

                                                                                    params.put("tanggal_selesai", currentDateandTime2);
                                                                                    params.put("nik_pic", nik_baru);



                                                                                    params.put("nik_pic", nik_baru);

                                                                                    return params;
                                                                                }

                                                                            };
                                                                            stringRequest2.setRetryPolicy(
                                                                                    new DefaultRetryPolicy(
                                                                                            5000,
                                                                                            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                                                                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                                                                                    )
                                                                            );
                                                                            RequestQueue requestQueue2 = Volley.newRequestQueue(menu_stockopname.this);
                                                                            requestQueue2.getCache().clear();
                                                                            requestQueue2.add(stringRequest2);
                                                                        }

                                                                    }
                                                                });
                                                            }




                                                            // adapter.notifyDataSetChanged();

                                                        }
                                                    } catch(JSONException e){
                                                        e.printStackTrace();

                                                    }
                                                }
                                            },
                                            new Response.ErrorListener() {
                                                @Override
                                                public void onErrorResponse(VolleyError error) {
                                                    pDialog.dismissWithAnimation();
                                                }
                                            })

                                    {
                                        @Override
                                        public Map<String, String> getHeaders() throws AuthFailureError {
                                            HashMap<String, String> params = new HashMap<String, String>();
                                            String creds = String.format("%s:%s", "admin", "Databa53");
                                            String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.DEFAULT);
                                            params.put("Authorization", auth);
                                            return params;
                                        }
                                    };

                                    stringRequest.setRetryPolicy(
                                            new DefaultRetryPolicy(
                                                    15000,
                                                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                                            ));
                                    if (requestQueue10 == null) {
                                        requestQueue10 = Volley.newRequestQueue(menu_stockopname.this);
                                        requestQueue10.add(stringRequest);
                                    } else {
                                        requestQueue10.add(stringRequest);
                                    }
                                }
                            });

                            batal.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    filter.dismiss();
                                }
                            });

                        }

                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                String creds = String.format("%s:%s", "admin", "Databa53");
                String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.DEFAULT);
                params.put("Authorization", auth);
                return params;
            }
        };

        stringRequest.setRetryPolicy(
                new DefaultRetryPolicy(
                        500000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                ));
        RequestQueue requestQueue = Volley.newRequestQueue(menu_stockopname.this);
        requestQueue.add(stringRequest);
    }

    public void onResume() {
        super.onResume();
        this.bottomNavigationView.setSelectedItemId(R.id.bottom_nav_home);
        getTotalCount();
    }

    public void onBackPressed() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle((CharSequence) "Apakah anda yakin ingin keluar?");
        alertDialogBuilder.setCancelable(false).setPositiveButton((CharSequence) "Ya", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                menu_stockopname.this.finishAffinity();
            }
        }).setNegativeButton((CharSequence) "Tidak", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        alertDialogBuilder.create().show();
    }
}
