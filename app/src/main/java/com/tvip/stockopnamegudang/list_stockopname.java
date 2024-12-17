package com.tvip.stockopnamegudang;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.tabs.TabLayout;
import com.tvip.stockopnamegudang.pojos.sparepart_pojos;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class list_stockopname extends AppCompatActivity {
    ListViewAdapterStockOpname adapter;

    MaterialCardView card_layout_simpan;

    private CountDownTimer countDownTimer;

    private int click_duble = 1;
    int layout;
    RelativeLayout linear_layout_simpan;
    ListView list_stock;
    String lokasiHrd;
    SweetAlertDialog pDialog;
    MaterialButton reset;
    EditText searchview;
    SharedPreferences sharedPreferences;
    MaterialButton simpan;
    String sort;

    RequestQueue requestQueue32;
    ArrayList<sparepart_pojos> sparepart_pojosList = new ArrayList();
    SweetAlertDialog success;
    TabLayout tablayout;
    TextView tanggal;
    MaterialToolbar topAppBar;
    static Chip total;

    MaterialCardView warning, refresh;

    int number;

    SwipeRefreshLayout swipeRefreshLayout;

    /* access modifiers changed from: protected */
    @SuppressLint("MissingInflatedId")
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_stockopname);
        HttpsTrustManager.allowAllSSL();
        this.tanggal = (TextView) findViewById(R.id.tanggal);
        this.list_stock = (ListView) findViewById(R.id.list_stock);
        this.total = (Chip) findViewById(R.id.total);
        this.searchview = (EditText) findViewById(R.id.searchview);
        this.tablayout = (TabLayout) findViewById(R.id.tablayout);
        this.linear_layout_simpan = (RelativeLayout) findViewById(R.id.linear_layout_simpan);
        this.reset = (MaterialButton) findViewById(R.id.reset);
        this.simpan = (MaterialButton) findViewById(R.id.simpan);
        this.topAppBar = (MaterialToolbar) findViewById(R.id.AppToolBar);

        card_layout_simpan = (MaterialCardView) findViewById(R.id.card_layout_simpan);


        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);

        warning = findViewById(R.id.warning);
        refresh = findViewById(R.id.refresh);



        setSupportActionBar(topAppBar);

        this.tanggal.setText("Update • " + new SimpleDateFormat("dd MMMM yyyy").format(new Date()));
        this.total.setChipStrokeColor(ColorStateList.valueOf(Color.parseColor("#FEC0C0")));
        this.total.setChipBackgroundColor(ColorStateList.valueOf(Color.parseColor("#FFF1F1")));
        this.total.setTextColor(Color.parseColor("#FB4141"));
        SharedPreferences sharedPreferences2 = getSharedPreferences("user_details", 0);
        this.sharedPreferences = sharedPreferences2;
        String location = sharedPreferences2.getString("lokasi", (String) null);

        getLokasi();

        this.layout = 0;

        String currentDateandTime2 = new SimpleDateFormat("HHmm").format(new Date());
        if(Integer.parseInt(currentDateandTime2) >= 1630){
            if(sharedPreferences2.getString("nik_baru", (String) null).equals("0100048300")) {
                simpan.setVisibility(View.VISIBLE);
                reset.setVisibility(View.GONE);
                warning.setVisibility(View.GONE);
                refresh.setVisibility(View.VISIBLE);
            } else {
                simpan.setVisibility(View.GONE);
                reset.setVisibility(View.GONE);
                warning.setVisibility(View.VISIBLE);
                refresh.setVisibility(View.GONE);
                linear_layout_simpan.setVisibility(View.GONE);
            }

        } else {
            simpan.setVisibility(View.VISIBLE);
            reset.setVisibility(View.GONE);
            warning.setVisibility(View.GONE);
            refresh.setVisibility(View.VISIBLE);


                swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        searchview.getText().clear();
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if(lokasiHrd != null){
                                    if(swipeRefreshLayout.isRefreshing()){
                                        swipeRefreshLayout.setRefreshing(false);
                                        getData("0", "https://apisec.tvip.co.id/rest_server_stock_opname/Stock/index_Stock?warehouse=");
                                    }
                                } else {
                                    if(swipeRefreshLayout.isRefreshing()){
                                        swipeRefreshLayout.setRefreshing(false);
                                    }
                                }

                            }
                        }, 3000);


                    }
                });


        }

        list_stock.setOnScrollListener(new AbsListView.OnScrollListener() {
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }

            public void onScrollStateChanged(AbsListView listView, int scrollState) {
            }
        });



        topAppBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                searchview.getText().clear();
                switch (item.getItemId()) {
                    case R.id.page_1:
                        String currentDateandTime2 = new SimpleDateFormat("HH").format(new Date());
                        if(Integer.parseInt(currentDateandTime2) >= 17){
                            if(sharedPreferences2.getString("nik_baru", (String) null).equals("0100048300")){
                                simpan.setVisibility(View.VISIBLE);
                                reset.setVisibility(View.GONE);
                                linear_layout_simpan.setVisibility(View.VISIBLE);
                            } else {
                                simpan.setVisibility(View.GONE);
                                reset.setVisibility(View.GONE);
                                linear_layout_simpan.setVisibility(View.GONE);
                            }
                        } else {
                            simpan.setVisibility(View.VISIBLE);
                            reset.setVisibility(View.GONE);
                            linear_layout_simpan.setVisibility(View.VISIBLE);
                        }
                        sort = "all";
                        getData("0", "https://apisec.tvip.co.id/rest_server_stock_opname/Stock/index_Stock?warehouse=");
                        return true;
                    case R.id.page_2:
                        sort = "pass";
                        getData("0", "https://apisec.tvip.co.id/rest_server_stock_opname/Stock/index_Stock?warehouse=");
                        simpan.setVisibility(View.GONE);
                        reset.setVisibility(View.GONE);
                        return true;
                    case R.id.page_3:
                        sort = "Invalid";
                        getData("0", "https://apisec.tvip.co.id/rest_server_stock_opname/Stock/index_Stock?warehouse=");
                        simpan.setVisibility(View.VISIBLE);
                        reset.setVisibility(View.GONE);
                        return true;
                    case R.id.page_4:
                        String currentDateandTime3 = new SimpleDateFormat("HH").format(new Date());
                        if(Integer.parseInt(currentDateandTime3) >= 17){
                            simpan.setVisibility(View.GONE);
                            reset.setVisibility(View.GONE);
                            linear_layout_simpan.setVisibility(View.GONE);
                        } else {
                            simpan.setVisibility(View.VISIBLE);
                            reset.setVisibility(View.GONE);
                            linear_layout_simpan.setVisibility(View.VISIBLE);
                        }
                        sort = "Outstanding";
                        getData("0", "https://apisec.tvip.co.id/rest_server_stock_opname/Stock/index_Kosongan?warehouse=");
                        return true;

                }
                return false;
            }
        });

        this.reset.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
              //  total.setText("0/" + String.valueOf(sparepart_pojosList.size()) + " Item");
                getData("0", "https://apisec.tvip.co.id/rest_server_stock_opname/Stock/index_Stock?warehouse=");
            }
        });
        this.simpan.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                searchview.getText().clear();
                    simpan.setEnabled(false);

                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        String currentDateandTime2 = new SimpleDateFormat("HHmm").format(new Date());

                        if(Integer.parseInt(currentDateandTime2) >= 1630){
                            new SweetAlertDialog(list_stockopname.this, SweetAlertDialog.WARNING_TYPE)
                                    .setTitleText("Maaf, waktu Penginputan sudah habis")
                                    .setConfirmText("OK")
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sDialog) {
                                            sDialog.dismissWithAnimation();
                                            simpan.setEnabled(true);

                                            finish();
                                        }
                                    })
                                    .show();
                        } else if(adapter.getCount() == 0){
                            new SweetAlertDialog(list_stockopname.this, 1).setContentText("Data Masih Ada Yang Kosong").setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.dismissWithAnimation();
                                    simpan.setEnabled(true);

                                }
                            }).show();
                        } else {
                            for (int e = 0; e <= sparepart_pojosList.size() - 1; e++) {
                                if (adapter.getItem(e).getStock_fisik() == null) {
                                    simpandata();
                                    Toast.makeText(list_stockopname.this, "OK1", Toast.LENGTH_SHORT).show();
                                    break;
                                } else if (adapter.getItem(e).getStock_fisik() != null) {
                                    Toast.makeText(list_stockopname.this, "OK2", Toast.LENGTH_SHORT).show();
                                    simpandata();
                                    break;
                                } else if (e == sparepart_pojosList.size() - 1) {
                                    new SweetAlertDialog(list_stockopname.this, 1).setContentText("Data Masih Ada Yang Kosong").setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        public void onClick(SweetAlertDialog sDialog) {
                                            sDialog.dismissWithAnimation();
                                            simpan.setEnabled(true);

                                        }
                                    }).show();
                                }
                            }
                        }

                    }
                }, 1000);


            }
        });
    }

    private void getLokasi() {
        SharedPreferences sharedPreferences2 = getSharedPreferences("user_details", 0);
        this.sharedPreferences = sharedPreferences2;
        StringRequest stringRequest = new StringRequest(0, "https://apisec.tvip.co.id/rest_server_stock_opname/Stock/index_nik?employee_id=" + sharedPreferences2.getString("nik_baru", (String) null), new Response.Listener<String>() {
            public void onResponse(String response) {
                try {
                    JSONObject obj = new JSONObject(response);
                    if (obj.getString(NotificationCompat.CATEGORY_STATUS).equals("true")) {
                        JSONArray movieArray = obj.getJSONArray("data");
                        for (int i = 0; i < movieArray.length(); i++) {
                            JSONObject movieObject = movieArray.getJSONObject(i);
                            lokasiHrd = movieObject.getString("warehouse");
                            sort = "all";
                            getData("0", "https://apisec.tvip.co.id/rest_server_stock_opname/Stock/index_Stock?warehouse=");

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
    }

    /* access modifiers changed from: private */
    public void simpandata() {
        String currentDateandTime2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(this, 5);
        this.pDialog = sweetAlertDialog;
        sweetAlertDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        this.pDialog.setTitleText("Harap Menunggu");
        this.pDialog.setCancelable(false);
        this.pDialog.show();
        for (int i = 0; i <= this.sparepart_pojosList.size() - 1; i++) {
            int finalI = i;


            final String str = currentDateandTime2;
            final int i2 = finalI;

            if(adapter.getItem(finalI).getStock_fisik() == null){
                if (finalI == sparepart_pojosList.size() - 1) {
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            success = new SweetAlertDialog(list_stockopname.this, 2);
                            success.setContentText("Data Berhasil Disimpan");
                            success.setCancelable(false);
                            success.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.dismissWithAnimation();
                                    pDialog.dismissWithAnimation();
                                    searchview.getText().clear();
                                    simpan.setEnabled(true);
                                    getData("0", "https://apisec.tvip.co.id/rest_server_stock_opname/Stock/index_Stock?warehouse=");
                                }
                            });
                            success.show();
                        }
                    }, 7000);
                }
            } else if(!adapter.getItem(finalI).getStock_fisik().equals(adapter.getItem(finalI).getStock_system())){
                JSONObject jsonObject = new JSONObject();
                try {
                    // user_id, comment_id,status
                    list_stockopname list_stockopname = list_stockopname.this;
                        list_stockopname.sharedPreferences = list_stockopname.getSharedPreferences("user_details", 0);
                        String nik_baru = sharedPreferences.getString("nik_baru", (String) null);
                    jsonObject.put("submit_date", str);
                    jsonObject.put("tgl_closing", adapter.getItem(i2).getTgl_closing());
                    jsonObject.put("warehouse", adapter.getItem(i2).getWarehouse());
                    jsonObject.put("order_id", adapter.getItem(i2).getOrder_id());
                    jsonObject.put("stok_awal", adapter.getItem(i2).getStok_awal());
                    jsonObject.put("in", adapter.getItem(i2).getIn());
                    jsonObject.put("out", adapter.getItem(i2).getOut());
                    jsonObject.put("stok_akhir", adapter.getItem(i2).getStok_akhir());
                    jsonObject.put("stok_fisik", adapter.getItem(i2).getStock_fisik());
                    jsonObject.put("stok_system", adapter.getItem(i2).getStock_system());
                    jsonObject.put("last_update", str);
                    jsonObject.put("nik_update", nik_baru);
                    System.out.println("Hasil Println = " + adapter.getItem(i2).getTgl_closing());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                        "https://apisec.tvip.co.id/rest_server_stock_opname/Stock/index_Stock", jsonObject,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {

                                if (finalI == sparepart_pojosList.size() - 1) {
                            new Handler().postDelayed(new Runnable() {
                                public void run() {
                                    success = new SweetAlertDialog(list_stockopname.this, 2);
                                    success.setContentText("Data Berhasil Disimpan");
                                    success.setCancelable(false);
                                    success.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        public void onClick(SweetAlertDialog sDialog) {
                                            sDialog.dismissWithAnimation();
                                            pDialog.dismissWithAnimation();
                                            searchview.getText().clear();
                                            simpan.setEnabled(true);
                                            getData("0", "https://apisec.tvip.co.id/rest_server_stock_opname/Stock/index_Stock?warehouse=");
                                        }
                                    });
                                    success.show();
                                }
                            }, 7000);
                        }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (finalI == sparepart_pojosList.size() - 1) {
                            new Handler().postDelayed(new Runnable() {
                                public void run() {
                                    success = new SweetAlertDialog(list_stockopname.this, 2);
                                    success.setContentText("Data Berhasil Disimpan");
                                    success.setCancelable(false);
                                    success.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        public void onClick(SweetAlertDialog sDialog) {
                                            sDialog.dismissWithAnimation();
                                            pDialog.dismissWithAnimation();
                                            searchview.getText().clear();
                                            simpan.setEnabled(true);
                                            getData("0", "https://apisec.tvip.co.id/rest_server_stock_opname/Stock/index_Stock?warehouse=");
                                        }
                                    });
                                    success.show();
                                }
                            }, 7000);
                        }

                    }
                })
                {
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        HashMap<String, String> params = new HashMap<>();
                        params.put("Authorization", "Basic " + Base64.encodeToString(String.format("%s:%s", new Object[]{"admin", "Databa53"}).getBytes(), 0));
                        return params;
                    }
                };



                if (requestQueue32 == null) {
                    requestQueue32 = Volley.newRequestQueue(list_stockopname.this);
                    requestQueue32.add(jsonObjReq);
                } else {
                    requestQueue32.add(jsonObjReq);
                }
            } else {
                if (finalI == this.sparepart_pojosList.size() - 1) {
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            success = new SweetAlertDialog(list_stockopname.this, 2);
                            success.setContentText("Data Berhasil Disimpan");
                            success.setCancelable(false);
                            success.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                public void onClick(SweetAlertDialog sDialog) {
                                    simpan.setEnabled(true);

                                    sDialog.dismissWithAnimation();
                                    pDialog.dismissWithAnimation();
                                    searchview.getText().clear();
                                    getData("0", "https://apisec.tvip.co.id/rest_server_stock_opname/Stock/index_Stock?warehouse=");
                                }
                            });
                            success.show();
                        }
                    }, 7000);
                }
            }
        }
    }

    private void updateStocks(String in, String out, String stok_akhir, String stock_fisik, String tgl_closing, String nik_baru, String order_id, String stok_awal) {
        StringRequest r1 = new StringRequest(2, "https://apisec.tvip.co.id/rest_server_stock_opname/Stock/index_stock_update", new Response.Listener<String>() {
            public void onResponse(String response) {
                postHistoryNew(tgl_closing, order_id, stok_awal, stok_akhir);
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

            /* access modifiers changed from: protected */
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                String currentDateandTime2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                sharedPreferences = getSharedPreferences("user_details", 0);
                String nik_baru = sharedPreferences.getString("nik_baru", (String) null);
                params.put("tgl_closing", tgl_closing);
                params.put("warehouse", lokasiHrd);
                params.put("order_id", order_id);
                params.put("in", in);
                params.put("out", out);
                params.put("stok_akhir", stok_akhir);
                params.put("stok_fisik", stock_fisik);
                params.put("last_update", currentDateandTime2);
                params.put("nik_update", nik_baru);
                return params;
            }
        };
        r1.setRetryPolicy(new DefaultRetryPolicy(5000, 1, 1.0f));
        RequestQueue requestQueue2 = Volley.newRequestQueue(list_stockopname.this);
        requestQueue2.getCache().clear();
        requestQueue2.add(r1);
    }

    private void postHistoryNew(String tgl_closing, String order_id, String stok_awal, String stok_akhir) {
        StringRequest stringRequest2 = new StringRequest(1, "https://apisec.tvip.co.id/rest_server_stock_opname/Stock/index_stockHistory", new Response.Listener<String>() {
            public void onResponse(String response) {

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

            /* access modifiers changed from: protected */
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                list_stockopname list_stockopname = list_stockopname.this;
                list_stockopname.sharedPreferences = list_stockopname.getSharedPreferences("user_details", 0);
                String nik_baru = sharedPreferences.getString("nik_baru", (String) null);
                params.put("tgl_closing", tgl_closing);
                params.put("warehouse", lokasiHrd);
                params.put("order_id", order_id);
                params.put("stok_awal", stok_awal);
                params.put("stok_akhir", stok_akhir);
                params.put("nik_update", nik_baru);
                return params;
            }
        };
        stringRequest2.setRetryPolicy(new DefaultRetryPolicy(120000, 0, 1.0f));
        RequestQueue requestQueue2 = Volley.newRequestQueue(this);
        requestQueue2.getCache().clear();
        requestQueue2.add(stringRequest2);
    }

    /* access modifiers changed from: private */
    public void getData(final String s, String Link) {
        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(this, 5);
        this.pDialog = sweetAlertDialog;
        sweetAlertDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        this.pDialog.setTitleText("Harap Menunggu");
        this.pDialog.setCancelable(false);
        this.pDialog.show();

        System.out.println(Link + this.lokasiHrd);
        System.out.println("Hasil " + sort);

        list_stock.setAdapter(null);
        this.adapter = new ListViewAdapterStockOpname(this.sparepart_pojosList, getApplicationContext());
        this.sparepart_pojosList.clear();
        this.adapter.notifyDataSetChanged();

        number = 0;

        StringRequest r1 = new StringRequest(0, Link + this.lokasiHrd, new Response.Listener<String>() {
            public void onResponse(String response) {
                pDialog.dismissWithAnimation();
                swipeRefreshLayout.setRefreshing(false);
                try {
                    int number3 = 0;
                    int kosong = 0;

                    JSONObject obj = new JSONObject(response);
                    if (obj.getString(NotificationCompat.CATEGORY_STATUS).equals("true")) {
                        JSONArray movieArray = new JSONObject(response).getJSONArray("data");
                        for (int i = 0; i < movieArray.length(); i++) {
                            JSONObject movieObject = movieArray.getJSONObject(i);

                            String fisik;
                            if(movieObject.getString("stok_fisik").equals("null")){
                                fisik = null;
                            } else {
                                fisik = movieObject.getString("stok_fisik");
                            }

                            sparepart_pojos movieItem = new sparepart_pojos(
                                    i,
                                    movieObject.getString("tgl_closing"),
                                    movieObject.getString("name"),
                                    movieObject.getString("warehouse"),
                                    movieObject.getString("order_id"),
                                    movieObject.getString("order_name"),
                                    movieObject.getString("stok_awal"),
                                    movieObject.getString("in"),
                                    movieObject.getString("out"),
                                    movieObject.getString("stok_akhir"),
                                    fisik,
                                    movieObject.getString("condition"),
                                    movieObject.getString("stok_fisik"));

                            sparepart_pojosList.add(movieItem);
                            list_stock.setAdapter(adapter);
                            adapter.notifyDataSetChanged();


                            if(!movieObject.getString("stok_fisik").equals("null")){
                                number3++;
                            }

                           if (sort.equals("all")){
                                number++;
                                total.setText(String.valueOf(number3) +"/" + String.valueOf(number) + " Item");
                                linear_layout_simpan.setVisibility(View.VISIBLE);
                            } else if (sort.equals("Outstanding")){
                               number++;
                               total.setText(String.valueOf(number3) +"/" + String.valueOf(number) + " Item");
                               linear_layout_simpan.setVisibility(View.VISIBLE);
                               simpan.setVisibility(View.VISIBLE);
                           } else if (sort.equals("pass")){
                                if(movieObject.getString("stok_fisik").equals("null")){
                                    sparepart_pojosList.remove(movieItem);
                                    adapter.notifyDataSetChanged();
                                } else if (!movieObject.getString("stok_fisik").equals(movieObject.getString("stok_akhir"))){
                                    sparepart_pojosList.remove(movieItem);
                                    adapter.notifyDataSetChanged();
                                }
                               linear_layout_simpan.setVisibility(View.VISIBLE);
                               simpan.setVisibility(View.GONE);
                                if(!movieObject.getString("stok_fisik").equals("null")){
                                    if (movieObject.getString("stok_fisik").equals(movieObject.getString("stok_akhir"))){
                                        number++;
                                    }
                                    total.setText(String.valueOf(number) + " Item");
                                }
                            } else if (sort.equals("Invalid")){
                               linear_layout_simpan.setVisibility(View.VISIBLE);

                               if(movieObject.getString("stok_fisik").equals("null")){
                                    sparepart_pojosList.remove(movieItem);
                                    adapter.notifyDataSetChanged();
                                } else if (movieObject.getString("stok_fisik").equals(movieObject.getString("stok_akhir"))){
                                    sparepart_pojosList.remove(movieItem);
                                    adapter.notifyDataSetChanged();
                                }

                                if(!movieObject.getString("stok_fisik").equals("null")){
                                    if (!movieObject.getString("stok_fisik").equals(movieObject.getString("stok_akhir"))){
                                        number++;
                                    }
                                    total.setText(String.valueOf(number) + " Item");
                                }
                            }

                            searchview.addTextChangedListener(new TextWatcher() {
                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                }

                                public void onTextChanged(CharSequence s, int start, int before, int count) {
                                    if (s.length() == 0) {
                                        adapter.updateData();
                                    } else {
                                        adapter.getFilter().filter(s.toString());
                                    }
                                }

                                public void afterTextChanged(Editable s) {

                                }
                            });



                        }


                    } else {
                        linear_layout_simpan.setVisibility(View.GONE);
                    }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                searchview.getText().clear();
                pDialog.dismissWithAnimation();
                linear_layout_simpan.setVisibility(View.GONE);
                Toast.makeText(list_stockopname.this, String.valueOf(error), Toast.LENGTH_SHORT).show();

                total.setText(String.valueOf("0 Item"));

            }
        }) {
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("Authorization", "Basic " + Base64.encodeToString(String.format("%s:%s", new Object[]{"admin", "Databa53"}).getBytes(), 0));
                return params;
            }
        };
        r1.setRetryPolicy(new DefaultRetryPolicy(500000, 1, 1.0f));
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.getCache().clear();
        requestQueue.add(r1);
    }

    public class ListViewAdapterStockOpname extends BaseAdapter implements Filterable {

        private Context context;
        private ArrayList<sparepart_pojos> sparepart_pojosList;
        private ArrayList<sparepart_pojos> sparepart_pojosList_filtered;
        private LayoutInflater inflater;
        private ListViewAdapterStockOpname adapter;

        private ItemFilter itemFilter;


        private static class ViewHolder {
            int i;
            EditText Updatestock_fisik;
            MaterialCardView add;
            MaterialCardView addUpdate;
            TextView akhir;
            TextView awal;
            MaterialCardView cardview;
            int count;
            TextView fisik;

            TextView in, positions;
            RelativeLayout layout_fisik;
            MaterialCardView minus;
            MaterialCardView minusUpdate;
            TextView nama_produk;
            TextView orderid;
            TextView out;
            RelativeLayout postStockFisik;
            MaterialButton simpan;
            Chip status;
            EditText stock_fisik;
            MaterialCardView undo;
            RelativeLayout updateStockFisik;
            TextWatcher textWatcher;

            TextView nama_warehouse;

            private ViewHolder() {
            }
        }

        public ListViewAdapterStockOpname(ArrayList<sparepart_pojos> item, Context context2) {
            this.sparepart_pojosList = item;
            this.sparepart_pojosList_filtered = item;
            this.context = context2;
            this.inflater = LayoutInflater.from(context2);
            this.adapter = this;
            getFilter();

        }




        private void resetEditTextValues() {
            int firstVisiblePosition = list_stock.getFirstVisiblePosition();
            int lastVisiblePosition = list_stock.getLastVisiblePosition();

            Log.d("ResetEditTextValues", "First Visible Position: " + firstVisiblePosition);
            Log.d("ResetEditTextValues", "Last Visible Position: " + lastVisiblePosition);

            for (int i = firstVisiblePosition; i <= lastVisiblePosition; i++) {
                View view = list_stock.getChildAt(i - firstVisiblePosition);
                if (view != null) {
                    ViewHolder viewHolder = (ViewHolder) view.getTag();
                    if (viewHolder != null) {
                        Log.d("ResetEditTextValues", "ViewHolder found for position: " + i);

                        // Detach TextWatcher to prevent triggering it while updating
                        if (viewHolder.stock_fisik.getTag() instanceof TextWatcher) {
                            viewHolder.stock_fisik.removeTextChangedListener((TextWatcher) viewHolder.stock_fisik.getTag());
                        }

                        // Get the item associated with the current position
                        sparepart_pojos item = getItem(i);
                        Log.d("ResetEditTextValues", "Item for position " + i + ": " + item);

                        // Set the EditText value with the correct data from the item
                        if (item != null) {
                            viewHolder.stock_fisik.setText(item.getStock_fisik());
                            Log.d("ResetEditTextValues", "EditText value set for position " + i + ": " + item.getStock_fisik());
                        }

                        // Re-attach TextWatcher
                        TextWatcher watcher = new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                item.setStock_fisik(s.toString());
                            }

                            @Override
                            public void afterTextChanged(Editable s) {
                            }
                        };
                        viewHolder.stock_fisik.addTextChangedListener(watcher);
                        viewHolder.stock_fisik.setTag(watcher);
                    } else {
                        Log.d("ResetEditTextValues", "ViewHolder is null for position: " + i);
                    }
                } else {
                    Log.d("ResetEditTextValues", "View is null for position: " + i);
                }
            }
        }


        public void updateData() {
            sparepart_pojosList_filtered = sparepart_pojosList;
            notifyDataSetChanged();
        }






        @Override
        public int getCount() {
            return this.sparepart_pojosList_filtered.size();
        }

        @Override
        public sparepart_pojos getItem(int position) {
            return sparepart_pojosList_filtered.get(position); // Line 54.
        }

        @Override
        public int getViewTypeCount() {
            return (sparepart_pojosList_filtered.size() > 0) ? getCount() : 1;
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View convertView2;
            final ViewHolder viewHolder;
            final sparepart_pojos movieItem = getItem(position);
            final int pos = position;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView2 = inflater.inflate(R.layout.listitem_stock_opname, null, true);
                viewHolder.nama_produk = convertView2.findViewById(R.id.nama_produk);
                viewHolder.in = convertView2.findViewById(R.id.in);
                viewHolder.out = convertView2.findViewById(R.id.out);
                viewHolder.awal = convertView2.findViewById(R.id.awal);
                viewHolder.akhir = convertView2.findViewById(R.id.akhir);
                viewHolder.fisik = convertView2.findViewById(R.id.fisik);
                viewHolder.orderid = convertView2.findViewById(R.id.orderid);
                viewHolder.positions = convertView2.findViewById(R.id.positions);
                viewHolder.nama_warehouse = convertView2.findViewById(R.id.nama_warehouse);

                viewHolder.undo = convertView2.findViewById(R.id.undo);
                viewHolder.minus = convertView2.findViewById(R.id.minus);
                viewHolder.stock_fisik = convertView2.findViewById(R.id.stock_fisik);
                viewHolder.add = convertView2.findViewById(R.id.add);
                viewHolder.postStockFisik = convertView2.findViewById(R.id.postStockFisik);
                viewHolder.updateStockFisik = convertView2.findViewById(R.id.updateStockFisik);
                viewHolder.simpan = convertView2.findViewById(R.id.simpan);
                viewHolder.minusUpdate = convertView2.findViewById(R.id.minusUpdate);
                viewHolder.addUpdate = convertView2.findViewById(R.id.addUpdate);
                viewHolder.Updatestock_fisik = convertView2.findViewById(R.id.Updatestock_fisik);
                viewHolder.status = convertView2.findViewById(R.id.status);
                viewHolder.cardview = convertView2.findViewById(R.id.cardview);
                viewHolder.layout_fisik = convertView2.findViewById(R.id.layout_fisik);

                viewHolder.stock_fisik.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        // Use the stored position
                        sparepart_pojos item = getItem(pos);
                        if (item != null) {
                            item.setStock_fisik(s.toString());
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                    }
                });

                viewHolder.Updatestock_fisik.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        // Use the stored position
                        sparepart_pojos item = getItem(pos);
                        if (item != null) {
                            if (s == null || s.length() == 0) {
                                item.setStock_fisik(null);
                            } else {
                                try {
                                    int newValue = Integer.parseInt(s.toString());
                                    viewHolder.count = newValue;
                                    item.setStock_fisik(String.valueOf(newValue));
                                } catch (NumberFormatException e) {
                                    // Handle the case where the input is not a valid number
                                    e.printStackTrace();
                                }
                            }
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                    }
                });




                convertView2.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
                convertView2 = convertView;
            }


            viewHolder.positions.setTag(movieItem.getId());
            viewHolder.nama_produk.setTag(movieItem.getId());
            viewHolder.in.setTag(movieItem.getId());
            viewHolder.out.setTag(movieItem.getId());
            viewHolder.awal.setTag(movieItem.getId());
            viewHolder.akhir.setTag(movieItem.getId());
            viewHolder.fisik.setTag(movieItem.getId());
            viewHolder.orderid.setTag(movieItem.getId());
            viewHolder.undo.setTag(movieItem.getId());
            viewHolder.minus.setTag(movieItem.getId());
            viewHolder.stock_fisik.setTag(movieItem.getId());
            viewHolder.add.setTag(movieItem.getId());
            viewHolder.postStockFisik.setTag(movieItem.getId());
            viewHolder.updateStockFisik.setTag(movieItem.getId());
            viewHolder.simpan.setTag(movieItem.getId());
            viewHolder.minusUpdate.setTag(movieItem.getId());
            viewHolder.addUpdate.setTag(movieItem.getId());
            viewHolder.Updatestock_fisik.setTag(movieItem.getId());
            viewHolder.status.setTag(movieItem.getId());
            viewHolder.cardview.setTag(movieItem.getId());
            viewHolder.layout_fisik.setTag(movieItem.getId());
            viewHolder.nama_warehouse.setText(movieItem.getName());

            if (movieItem.getCondition().equals("None")) {
                viewHolder.updateStockFisik.setVisibility(View.GONE);
                viewHolder.postStockFisik.setVisibility(View.VISIBLE);
                viewHolder.status.setVisibility(View.GONE);
                viewHolder.layout_fisik.setVisibility(View.GONE);
                viewHolder.cardview.setBackgroundColor(Color.parseColor("#ffffff"));

                viewHolder.count = 0;
                viewHolder.postStockFisik.setVisibility(View.VISIBLE);
                viewHolder.updateStockFisik.setVisibility(View.GONE);
                viewHolder.status.setVisibility(View.GONE);
                viewHolder.layout_fisik.setVisibility(View.GONE);



            } else if (movieItem.getCondition().equals("Pass")) {
                viewHolder.updateStockFisik.setVisibility(View.GONE);
                viewHolder.postStockFisik.setVisibility(View.GONE);
                viewHolder.status.setVisibility(View.VISIBLE);
                viewHolder.layout_fisik.setVisibility(View.VISIBLE);
                viewHolder.cardview.setBackgroundColor(Color.parseColor("#D3D3D3"));

                viewHolder.status.setText("Pass");
                viewHolder.status.setChipStrokeColor(ColorStateList.valueOf(Color.parseColor("#B9EED0")));
                viewHolder.status.setChipBackgroundColor(ColorStateList.valueOf(Color.parseColor("#E8FFF2")));
                viewHolder.status.setTextColor(Color.parseColor("#2ECC71"));

                viewHolder.fisik.setText(movieItem.getStock_fisik() + " Pcs");
            } else if (movieItem.getCondition().equals("Invalid")) {
                viewHolder.updateStockFisik.setVisibility(View.VISIBLE);
                viewHolder.postStockFisik.setVisibility(View.GONE);
                viewHolder.status.setVisibility(View.VISIBLE);
                viewHolder.layout_fisik.setVisibility(View.GONE);
                viewHolder.cardview.setBackgroundColor(Color.parseColor("#ffffff"));

                viewHolder.status.setText("Invalid");
                viewHolder.status.setChipStrokeColor(ColorStateList.valueOf(Color.parseColor("#FEC0C0")));
                viewHolder.status.setChipBackgroundColor(ColorStateList.valueOf(Color.parseColor("#FFF1F1")));
                viewHolder.status.setTextColor(Color.parseColor("#FB4141"));

                viewHolder.Updatestock_fisik.setText(movieItem.getStock_fisik());
            }

            viewHolder.positions.setText(movieItem.getId() + "");
            viewHolder.nama_produk.setText(movieItem.getOrder_name());
            viewHolder.in.setText(movieItem.getIn() + " Pcs");
            viewHolder.out.setText(movieItem.getOut() + " Pcs");
            viewHolder.awal.setText(movieItem.getStok_awal() + " Pcs");
            viewHolder.akhir.setText(movieItem.getStok_akhir() + " Pcs");
            viewHolder.orderid.setText(movieItem.getOrder_id());
            viewHolder.stock_fisik.setText(movieItem.getStock_fisik());

            viewHolder.undo.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    viewHolder.count = 0;
                    movieItem.setStock_fisik((String) null);
                    viewHolder.stock_fisik.setText("");
                    total.setText("0/" + String.valueOf(number) + " Item");
                    int counting = 0;
                    for (int e = 0; e <= ListViewAdapterStockOpname.this.sparepart_pojosList.size() - 1; e++) {
                        if (adapter.getItem(e).getStock_fisik() != null) {
                            counting++;
                            total.setText(String.valueOf(counting) + "/" + String.valueOf(number) + " Item");
                        }
                    }
                    list_stockopname.total.setChipStrokeColor(ColorStateList.valueOf(Color.parseColor("#FEC0C0")));
                    list_stockopname.total.setChipBackgroundColor(ColorStateList.valueOf(Color.parseColor("#FFF1F1")));
                    list_stockopname.total.setTextColor(Color.parseColor("#FB4141"));
                    // adapter.notifyDataSetChanged();
                }
            });
            viewHolder.add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    viewHolder.count++;
                    movieItem.setStock_fisik(String.valueOf(viewHolder.count));
                    viewHolder.stock_fisik.setText(String.valueOf(viewHolder.count));
                }
            });

            viewHolder.minus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (viewHolder.count > 0) {
                        viewHolder.count--;
                        movieItem.setStock_fisik(String.valueOf(viewHolder.count));
                        viewHolder.stock_fisik.setText(String.valueOf(viewHolder.count));
                    }
                }
            });

            viewHolder.addUpdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    viewHolder.count++;
                    movieItem.setStock_fisik(String.valueOf(viewHolder.count));
                    viewHolder.Updatestock_fisik.setText(String.valueOf(viewHolder.count));
                }
            });

            viewHolder.minusUpdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (viewHolder.count > 0) {
                        viewHolder.count--;
                        movieItem.setStock_fisik(String.valueOf(viewHolder.count));
                        viewHolder.Updatestock_fisik.setText(String.valueOf(viewHolder.count));
                    }
                }
            });


            return convertView2;
        }



        @Override
        public Filter getFilter() {
            if (itemFilter == null) {
                itemFilter = new ItemFilter();
            }
            return itemFilter;
        }

        private class ItemFilter extends Filter {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                ArrayList<sparepart_pojos> arrayListFilter = new ArrayList<>();

                if (constraint == null || constraint.length() == 0) {
                    results.count = sparepart_pojosList.size();
                    results.values = sparepart_pojosList;
                } else {
                    for (sparepart_pojos modelData : sparepart_pojosList) {
                        if (modelData.getOrder_name().toLowerCase().contains(constraint.toString().toLowerCase()) ||
                                modelData.getOrder_id().toLowerCase().contains(constraint.toString().toLowerCase())) {
                            arrayListFilter.add(modelData);
                        }
                    }
                    results.count = arrayListFilter.size();
                    results.values = arrayListFilter;
                }
                return results;
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            protected void publishResults(CharSequence constraint, Filter.FilterResults results) {
                sparepart_pojosList_filtered = (ArrayList<sparepart_pojos>) results.values;
                adapter.notifyDataSetChanged();
            }
        }
    }


    private void postHistory(String tgl_closing, String order_id, String stok_awal, String stok_akhir) {

        StringRequest stringRequest2 = new StringRequest(1, "https://apisec.tvip.co.id/rest_server_stock_opname/Stock/index_stockHistory", new Response.Listener<String>() {
            public void onResponse(String response) {
                pDialog.dismissWithAnimation();
                success = new SweetAlertDialog(list_stockopname.this, 2);
                success.setContentText("Data Berhasil Disimpan");
                success.setCancelable(false);
                success.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                        getData("0", "https://apisec.tvip.co.id/rest_server_stock_opname/Stock/index_Stock?warehouse=");
                    }
                });
                success.show();
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

            /* access modifiers changed from: protected */
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                list_stockopname list_stockopname = list_stockopname.this;
                list_stockopname.sharedPreferences = list_stockopname.getSharedPreferences("user_details", 0);
                String nik_baru = sharedPreferences.getString("nik_baru", (String) null);
                params.put("tgl_closing", tgl_closing);
                params.put("warehouse", lokasiHrd);
                params.put("order_id", order_id);
                params.put("stok_awal", stok_awal);
                params.put("stok_akhir", stok_akhir);
                params.put("nik_update", nik_baru);
                return params;
            }
        };
        stringRequest2.setRetryPolicy(new DefaultRetryPolicy(120000, 0, 1.0f));
        RequestQueue requestQueue2 = Volley.newRequestQueue(this);
        requestQueue2.getCache().clear();
        requestQueue2.add(stringRequest2);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_navigation_menu, menu);
        return true;
    }

}
