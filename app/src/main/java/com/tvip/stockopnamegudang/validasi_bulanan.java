package com.tvip.stockopnamegudang;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.tvip.stockopnamegudang.menu_stockopname.kategoriSparepart;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.tabs.TabLayout;
import com.omadahealth.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.omadahealth.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;
import com.tvip.stockopnamegudang.pojos.sparepartBulanan_pojos;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class validasi_bulanan extends AppCompatActivity {
    ListView list_stock_bulanan;

    private Handler handler = new Handler();
    private Runnable typingRunnable;
    private static final long TYPING_DELAY = 500;

    private TextWatcher textWatcher;

    private static final long CLICK_TIME_INTERVAL = 500; // Interval in milliseconds
    private long lastClickTime = 0;

    ImageButton filter;
    RequestQueue requestQueue1, requestQueue2, requestQueue3, requestQueue4, requestQueue6;

    String gudang;
    Chip total;
    ArrayList<sparepartBulanan_pojos> sparepartBulanan_pojosList = new ArrayList<>();
    ListViewAdapterBulanan adapter;
    SweetAlertDialog pDialog, success;
    SharedPreferences sharedPreferences;

    int counter;

    String condition;

    TabLayout tablayout;

    EditText searchview;

    SwipyRefreshLayout swipeRefreshLayout;

    String ket;

    MaterialButton simpan, load_more;

    int loadmore;

    String link;

    String adding;

    String done, counting;
    boolean already;
    LinearLayout refresh_layout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validasi_bulanan);
        list_stock_bulanan = findViewById(R.id.list_stock_bulanan);
        tablayout = findViewById(R.id.tablayout);
        total = findViewById(R.id.total);
        searchview = findViewById(R.id.searchview);
        simpan = findViewById(R.id.simpan);
        filter = findViewById(R.id.filter);
        refresh_layout = findViewById(R.id.refresh_layout);
        simpan.setVisibility(View.GONE);
        list_stock_bulanan.setScrollingCacheEnabled(false);
        load_more = findViewById(R.id.load_more);

        already = false;

        showDialogFilter();
        refresh_layout.setVisibility(View.GONE);
        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filter.setEnabled(false);

                showDialogFilter();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        filter.setEnabled(true);
                    }
                }, 2000);
            }
        });



        loadmore = 15;

        simpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!done.equals(counting)){
                    new SweetAlertDialog(validasi_bulanan.this, SweetAlertDialog.ERROR_TYPE)
                            .setContentText("Stock harus semua di isi")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.dismissWithAnimation();
                                }
                            })
                            .show();
                } else {
                    pDialog = new SweetAlertDialog(validasi_bulanan.this, SweetAlertDialog.PROGRESS_TYPE);
                    pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                    pDialog.setTitleText("Harap Menunggu");
                    pDialog.setCancelable(false);
                    pDialog.show();

                    getCekStatus();
                }
            }
        });



        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);

        load_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adding = "1";
                getLocationWarehouse();
            }
        });

        textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Remove any pending callbacks for the typingRunnable
                handler.removeCallbacks(typingRunnable);

                // Create a new Runnable to be executed after a delay
                typingRunnable = new Runnable() {
                    @Override
                    public void run() {
                        if (!s.toString().trim().isEmpty()) {
                            System.out.println("Link Jenis = " + link);
                            if (link.equals("index_stock_bulanan")) {
                                pDialog = new SweetAlertDialog(validasi_bulanan.this, SweetAlertDialog.PROGRESS_TYPE);
                                pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                                pDialog.setTitleText("Harap Menunggu");
                                pDialog.setCancelable(false);
                                pDialog.show();

                                searchOutstanding(s.toString());
                            } else {
                                pDialog = new SweetAlertDialog(validasi_bulanan.this, SweetAlertDialog.PROGRESS_TYPE);
                                pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                                pDialog.setTitleText("Harap Menunggu");
                                pDialog.setCancelable(false);
                                pDialog.show();

                                searchFinished(s.toString());
                            }
                        } else {
                            getLocationWarehouse();
                        }
                    }
                };

                // Execute the typingRunnable after a delay
                handler.postDelayed(typingRunnable, TYPING_DELAY);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };

        // Attach the TextWatcher initially
        searchview.addTextChangedListener(textWatcher);

        tablayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                if (SystemClock.elapsedRealtime() - lastClickTime < CLICK_TIME_INTERVAL) {
                    return;
                }

                // Update the last click time
                lastClickTime = SystemClock.elapsedRealtime();
                int position = tab.getPosition();
                if(position == 0){
                    setSearchViewEnabled(false);
                    searchview.getText().clear();
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            setSearchViewEnabled(true);
                        }
                    }, 2000);

                    link = "index_stock_bulanan";
                    adding = "0";
                    getLocationWarehouse();
                    condition = "outstanding";
                    refresh_layout.setVisibility(View.GONE);
                    searchview.getText().clear();
                } else if (position == 1){
                    setSearchViewEnabled(false);
                    searchview.getText().clear();
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            setSearchViewEnabled(true);
                        }
                    }, 2000);
                    link = "index_stock_bulanan_validasi_complete";
                    adding = "0";
                    getLocationWarehouse();
                    condition = "bergerak";
                    refresh_layout.setVisibility(View.GONE);
                    searchview.getText().clear();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

                if (SystemClock.elapsedRealtime() - lastClickTime < CLICK_TIME_INTERVAL) {
                    return;
                }

                // Update the last click time
                lastClickTime = SystemClock.elapsedRealtime();
                int position = tab.getPosition();
                if(position == 0){
                    setSearchViewEnabled(false);
                    searchview.getText().clear();
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            setSearchViewEnabled(true);
                        }
                    }, 2000);
                    link = "index_stock_bulanan";
                    adding = "0";
                    getLocationWarehouse();
                    condition = "outstanding";
                    refresh_layout.setVisibility(View.GONE);
                    searchview.getText().clear();
                } else if (position == 1){
                    setSearchViewEnabled(false);
                    searchview.getText().clear();
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            setSearchViewEnabled(true);
                        }
                    }, 2000);
                    link = "index_stock_bulanan_validasi_complete";
                    adding = "0";
                    getLocationWarehouse();
                    condition = "bergerak";
                    refresh_layout.setVisibility(View.GONE);
                    searchview.getText().clear();
                }
            }
        });

        swipeRefreshLayout.setDirection(SwipyRefreshLayoutDirection.BOTH);
        swipeRefreshLayout.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(SwipyRefreshLayoutDirection direction) {
                if(direction == SwipyRefreshLayoutDirection.TOP){
                    searchview.getText().clear();
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if(swipeRefreshLayout.isRefreshing()){
                                swipeRefreshLayout.setRefreshing(false);
                                adding = "0";
                                getLocationWarehouse();
                            }

                        }
                    }, 3000);
                } else {
                    searchview.getText().clear();
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if(swipeRefreshLayout.isRefreshing()){
                                swipeRefreshLayout.setRefreshing(false);
                                adding = "1";
                                getLocationWarehouse();
                            }

                        }
                    }, 3000);
                }
            }
        });



    }

    private void showDialogFilter() {

        Dialog filter = new Dialog(validasi_bulanan.this);
        filter.setContentView(R.layout.pilih_gudang_validasi);
        filter.setCancelable(false);

        AutoCompleteTextView pilih_gudang = filter.findViewById(R.id.pilih_gudang);
        AutoCompleteTextView pilih_kategori = filter.findViewById(R.id.pilih_kategori);
        RadioButton semua = filter.findViewById(R.id.semua);
        RadioButton pass = filter.findViewById(R.id.pass);
        RadioButton invalid = filter.findViewById(R.id.invalid);
        RadioGroup filtering_group = filter.findViewById(R.id.filtering_group);

        Button batal = filter.findViewById(R.id.batal);
        Button lanjutkan = filter.findViewById(R.id.lanjutkan);


        String[] array={"Pusat", "Cirendeu", "Rancamaya", "Cikuda"};
        ArrayAdapter<String> adapter;

        adapter = new ArrayAdapter<String>(validasi_bulanan.this,
                android.R.layout.simple_list_item_1, array);

        pilih_gudang.setAdapter(adapter);
        pilih_kategori.setAdapter(new ArrayAdapter<String>(validasi_bulanan.this, android.R.layout.simple_expandable_list_item_1, menu_stockopname.kategori));

        batal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(already == false){
                    finish();
                } else {
                    filter.dismiss();
                }

            }
        });

        lanjutkan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pilih_gudang.getText().toString().length() == 0){
                    pilih_gudang.setError("Pilih Gudang");
                } else if(pilih_kategori.getText().toString().length() == 0){
                    pilih_kategori.setError("Pilih Kategori Sparepart");
                } else if (filtering_group.getCheckedRadioButtonId() == -1) {
                    new SweetAlertDialog(validasi_bulanan.this, SweetAlertDialog.ERROR_TYPE)
                            .setContentText("Pilih Filter Terlebih Dahulu")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.dismissWithAnimation();
                                }
                            })
                            .show();
                } else {
                    already = true;
                    kategoriSparepart = pilih_kategori.getText().toString();
                    filter.dismiss();
                    if (pilih_gudang.getText().toString().equals("Pusat")) {
                        menu_stockopname.warehouse = "1,7,12,59";
                    } else if (pilih_gudang.getText().toString().equals("Cirendeu")) {
                        menu_stockopname.warehouse = "19,57";
                    } else if (pilih_gudang.getText().toString().equals("Rancamaya")) {
                        menu_stockopname.warehouse = "2,8";
                    } else if (pilih_gudang.getText().toString().equals("Cikuda")) {
                        menu_stockopname.warehouse = "3,9";
                    }

                    if(pass.isChecked()){
                        ket = pass.getText().toString();
                        tablayout.getTabAt(1).select();
                    } else if (invalid.isChecked()){
                        ket = invalid.getText().toString();
                        tablayout.getTabAt(1).select();
                    } else if (semua.isChecked()){
                        ket = semua.getText().toString();
                        tablayout.getTabAt(1).select();
                    }
//                    condition = "outstanding";
//                    link = "index_stock_bulanan";
//                    getLocationWarehouse();
//                    adding = "0";
                }
            }
        });

        filter.show();
    }

    private void cekButton() {
        {
            SimpleDateFormat year = new SimpleDateFormat("yyyy");
            String years = year.format(new Date());

            SimpleDateFormat month = new SimpleDateFormat("M");
            String months = month.format(new Date());

            StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/rest_server_stock_opname/Stock/index_stock_bulanan_cek?bulan=" + months+ "&tahun=" + years + "&warehouse=" + menu_stockopname.warehouse,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            simpan.setBackgroundColor(Color.parseColor("#0F4C81"));
                            simpan.setTextColor(Color.parseColor("#ffffff"));
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            simpan.setBackgroundColor(Color.parseColor("#D3D3D3"));
                            simpan.setTextColor(Color.parseColor("#808080"));

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
            if (requestQueue2 == null) {
                requestQueue2 = Volley.newRequestQueue(validasi_bulanan.this);
                requestQueue2.add(stringRequest);
            } else {
                requestQueue2.add(stringRequest);
            }
        }
    }

    private void updateStatus() {



        new Handler().postDelayed(new Runnable() {
            public void run() {
                pDialog.dismissWithAnimation();
                success = new SweetAlertDialog(validasi_bulanan.this, 2);
                success.setContentText("Data Berhasil Disimpan");
                success.setCancelable(false);
                success.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                        pDialog.dismissWithAnimation();
                        cekButton();
                    }
                });
                success.show();

            }
        }, (long) 5000);

        StringRequest stringRequest2 = new StringRequest(Request.Method.PUT, "https://apisec.tvip.co.id/rest_server_stock_opname/Stock/index_stockBulanan_status",
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
//                            getCekStatusAfter();
                    }
                }, new Response.ErrorListener() {
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
                params.put("warehouse", menu_stockopname.warehouse);

                params.put("tanggal_selesai", currentDateandTime2);
                params.put("nik_pic", nik_baru);



                params.put("nik_pic", nik_baru);

                return params;
            }

        };
        stringRequest2.setRetryPolicy(
                new DefaultRetryPolicy(
                        15000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                )
        );
        RequestQueue requestQueue2 = Volley.newRequestQueue(validasi_bulanan.this);
        requestQueue2.getCache().clear();
        requestQueue2.add(stringRequest2);

    }

    private void getCekStatusAfter() {
        {
            SimpleDateFormat year = new SimpleDateFormat("yyyy");
            String years = year.format(new Date());

            SimpleDateFormat month = new SimpleDateFormat("M");
            String months = month.format(new Date());

            StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/rest_server_stock_opname/Stock/index_stock_bulanan_cek?bulan=" + months+ "&tahun=" + years + "&warehouse=" + menu_stockopname.warehouse,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            simpan.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    updateStatus();
                                }
                            });
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

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

            if (requestQueue1 == null) {
                requestQueue1 = Volley.newRequestQueue(validasi_bulanan.this);
                requestQueue1.add(stringRequest);
            } else {
                requestQueue1.add(stringRequest);
            }
        }
    }

    private void getLocationWarehouse() {
        pDialog = new SweetAlertDialog(validasi_bulanan.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Harap Menunggu");
        pDialog.setCancelable(false);
        pDialog.show();

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

                            gudang = menu_stockopname.warehouse;

                            getHitung(menu_stockopname.warehouse);
                            cekButton();

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

    private void getCekStatus() {
        SimpleDateFormat year = new SimpleDateFormat("yyyy");
        String years = year.format(new Date());

        SimpleDateFormat month = new SimpleDateFormat("M");
        String months = month.format(new Date());

        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/rest_server_stock_opname/Stock/index_stock_bulanan_cek?bulan=" + months+ "&tahun=" + years + "&warehouse=" + menu_stockopname.warehouse,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        updateStatus();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pDialog.dismissWithAnimation();
                        success = new SweetAlertDialog(validasi_bulanan.this, SweetAlertDialog.SUCCESS_TYPE);
                        success.setContentText("Maaf, sudah tidak bisa di input");
                        success.setCancelable(false);
                        success.setConfirmText("OK");
                        success.show();
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
        if (requestQueue2 == null) {
            requestQueue2 = Volley.newRequestQueue(validasi_bulanan.this);
            requestQueue2.add(stringRequest);
        } else {
            requestQueue2.add(stringRequest);
        }
    }

    private void getHitung(String warehouse) {


        String category;

        if(kategoriSparepart.equals("Chasis & Tools")){
            category = "Chasis%20%26%20tools";
        } else if(kategoriSparepart.equals("Dan Lain-lain")){
            category = "other";
        } else {
            category = kategoriSparepart;
        }
        System.out.println("LINK API COUNT = " + "https://apisec.tvip.co.id/rest_server_stock_opname/Stock/index_stock_bulanan_count"+"?warehouse=" + menu_stockopname.warehouse+ "&load=" + String.valueOf(loadmore) + "&kategori=" + category);


        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/rest_server_stock_opname/Stock/index_stock_bulanan_count"+"?warehouse=" + menu_stockopname.warehouse+ "&load=" + String.valueOf(loadmore) + "&kategori=" + category,
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


                                getSpareParts(menu_stockopname.warehouse);

                                total.setText(String.valueOf(dikerjakan) + " / " + String.valueOf(alldata) + " Item");

                                if(dikerjakan != alldata){
                                    total.setChipStrokeColor(ColorStateList.valueOf(Color.parseColor("#FEC0C0")));
                                    total.setChipBackgroundColor(ColorStateList.valueOf(Color.parseColor("#FFF1F1")));
                                    total.setTextColor(Color.parseColor("#FB4141"));
                                } else {
                                    total.setChipStrokeColor(ColorStateList.valueOf(Color.parseColor("#B9EED0")));
                                    total.setChipBackgroundColor(ColorStateList.valueOf(Color.parseColor("#E8FFF2")));
                                    total.setTextColor(Color.parseColor("#2ECC71"));
                                }
                                done = String.valueOf(dikerjakan);
                                counting = String.valueOf(alldata);



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
            requestQueue3 = Volley.newRequestQueue(validasi_bulanan.this);
            requestQueue3.add(stringRequest);
        } else {
            requestQueue3.add(stringRequest);
        }
    }

    private void getSpareParts(String warehouse) {
        list_stock_bulanan.setVisibility(View.GONE);

        int default_angka = 15;
        int sekarang = loadmore + default_angka;

        if(adding.equals("1")){
            loadmore = sekarang;
        }


        counter = loadmore;

        adapter = new ListViewAdapterBulanan(sparepartBulanan_pojosList, getApplicationContext());
        adapter.clear();
        String category;
        if(kategoriSparepart.equals("Chasis & Tools")){
            category = "Chasis%20%26%20tools";
        } else if(kategoriSparepart.equals("Dan Lain-lain")){
            category = "other";
        } else {
            category = kategoriSparepart;
        }

        System.out.println("Link API = " + "https://apisec.tvip.co.id/rest_server_stock_opname/Stock/"+link+"?warehouse=" + menu_stockopname.warehouse + "&load=" + String.valueOf(loadmore) + "&kategori=" + category);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/rest_server_stock_opname/Stock/"+link+"?warehouse=" + menu_stockopname.warehouse + "&load=" + String.valueOf(loadmore) + "&kategori=" + category,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        list_stock_bulanan.setVisibility(View.VISIBLE);
                        pDialog.dismissWithAnimation();
                        int totalItem = 0;
                        try {
                            JSONObject obj = new JSONObject(response);
                            final JSONArray movieArray = obj.getJSONArray("data");

                            for (int i = 0; i < movieArray.length(); i++) {

                                String ref;
                                final JSONObject movieObject = movieArray.getJSONObject(i);
                                if(link.equals("index_stock_bulanan_validasi_complete")){
                                    ref = movieObject.getString("ref_id");
                                } else {
                                    ref = "";
                                }
                                final sparepartBulanan_pojos movieItem = new sparepartBulanan_pojos(
                                        i,
                                        movieObject.getString("order_id"),
                                        movieObject.getString("order_name"),
                                        movieObject.getString("warehouse"),
                                        movieObject.getString("name"),
                                        movieObject.getString("stok_awal"),
                                        movieObject.getString("stok_akhir"),
                                        movieObject.getString("stok_opname"),
                                        movieObject.getString("condition"),
                                        "0",
                                        movieObject.getString("in"),
                                        movieObject.getString("out"),
                                        "0",
                                        movieObject.getString("tgl_opname"),
                                        ref
                                );

                                sparepartBulanan_pojosList.add(movieItem);
                                list_stock_bulanan.setAdapter(adapter);
                                adapter.notifyDataSetChanged();

                                if(link.equals("index_stock_bulanan_validasi_complete")){
                                    list_stock_bulanan.setVisibility(View.VISIBLE);
                                } else {

                                }


                                if(condition.equals("outstanding")){

                                } else {
                                    if (ket.equals("Pass")){
                                        if(!movieObject.getString("condition").equals("Pass")){
                                            sparepartBulanan_pojosList.remove(movieItem);
                                            adapter.notifyDataSetChanged();
                                        }
                                    } else if (ket.equals("Invalid")){
                                        if(!movieObject.getString("condition").equals("Invalid")){
                                            sparepartBulanan_pojosList.remove(movieItem);
                                            adapter.notifyDataSetChanged();
                                        }
                                    }
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
                        list_stock_bulanan.setVisibility(View.GONE);
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
        if (requestQueue4 == null) {
            requestQueue4 = Volley.newRequestQueue(validasi_bulanan.this);
            requestQueue4.add(stringRequest);
        } else {
            requestQueue4.add(stringRequest);
        }
    }

    public class ListViewAdapterBulanan extends ArrayAdapter<sparepartBulanan_pojos> implements Filterable {

        LayoutInflater inflater;
        ArrayList<sparepartBulanan_pojos> sparepartBulanan_pojoss;
        ArrayList<sparepartBulanan_pojos> sparepartBulanan_pojoss_filtered;

        private final Context context;

        private class ViewHolder {
            TextView orderid, nama_produk, warehouse, stokawal, stokfisik, positions, stokakhir, stokfisik_text;
            MaterialCardView minusUpdate, addUpdate;
            EditText Updatestock_fisik;
            MaterialButton apply;

            int count;

            Chip status;

            RelativeLayout stokfisik_layout, updateStockFisik;

        }

        public ListViewAdapterBulanan(ArrayList<sparepartBulanan_pojos> sparepartBulanan_pojoss, Context context2) {
            super(context2, 0, sparepartBulanan_pojoss);
            this.sparepartBulanan_pojoss = sparepartBulanan_pojoss;
            this.sparepartBulanan_pojoss_filtered = sparepartBulanan_pojoss;

            this.context = context2;
            inflater = LayoutInflater.from(context2);

        }

        public Filter getFilter() {

            Filter filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults results = new FilterResults();
                    ArrayList<sparepartBulanan_pojos> arrayListFilter = new ArrayList<sparepartBulanan_pojos>();

                    if (constraint == null || constraint.length() == 0) {
                        results.count = sparepartBulanan_pojoss_filtered.size();
                        results.values = sparepartBulanan_pojoss_filtered;

                    } else {
                        for (sparepartBulanan_pojos modelData : sparepartBulanan_pojoss_filtered) {

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
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    sparepartBulanan_pojoss = (ArrayList<sparepartBulanan_pojos>) results.values;
                    notifyDataSetChanged();

                    if(link.equals("index_stock_bulanan_validasi_complete")){
                        if (results != null && results.count > 0) {
                            // Filtered data is available, show the ListView
                        } else {
                            // No filtered data available, hide the ListView
                        }
                    } else {
                        if (results != null && results.count > 0) {
                            // Filtered data is available, show the ListView
                        } else {
                            // No filtered data available, hide the ListView

                        }
                    }
                }
            };
            return filter;
        }

        public int getCount() {
            return sparepartBulanan_pojoss.size();
        }

        public sparepartBulanan_pojos getItem(int position) {
            return sparepartBulanan_pojoss.get(position);
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        public int getViewTypeCount() {
            return getCount();
        }

        public long getItemId(int position) {
            return 0;
        }


        @SuppressLint("NewApi")
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View convertView2;
            final ListViewAdapterBulanan.ViewHolder viewHolder;
            sparepartBulanan_pojos movieItem = getItem(position);
            if (convertView == null) {
                viewHolder = new ListViewAdapterBulanan.ViewHolder();
                convertView2 = inflater.inflate(R.layout.list_item_bulanan, null, false);

                viewHolder.stokfisik_layout = convertView2.findViewById(R.id.stokfisik_layout);
                viewHolder.updateStockFisik = convertView2.findViewById(R.id.updateStockFisik);

                viewHolder.orderid = convertView2.findViewById(R.id.orderid);
                viewHolder.nama_produk = convertView2.findViewById(R.id.nama_produk);
                viewHolder.warehouse = convertView2.findViewById(R.id.warehouse);
                viewHolder.stokawal = convertView2.findViewById(R.id.stokawal);
                viewHolder.stokfisik = convertView2.findViewById(R.id.stokfisik);

                viewHolder.minusUpdate = convertView2.findViewById(R.id.minusUpdate);
                viewHolder.addUpdate = convertView2.findViewById(R.id.addUpdate);

                viewHolder.Updatestock_fisik = convertView2.findViewById(R.id.Updatestock_fisik);
                viewHolder.apply = convertView2.findViewById(R.id.apply);
                viewHolder.status = convertView2.findViewById(R.id.status);
                viewHolder.positions = convertView2.findViewById(R.id.positions);

                viewHolder.stokakhir = convertView2.findViewById(R.id.stokakhir);

                viewHolder.count = 0;


                convertView2.setTag(viewHolder);

            } else {
                ViewGroup viewGroup = parent;
                viewHolder = (ListViewAdapterBulanan.ViewHolder) convertView.getTag();
                convertView2 = convertView;
            }

            System.out.println("Counting = " + String.valueOf(getCount()));

            viewHolder.stokfisik_layout.setTag(getItem(position).getId());
            viewHolder.updateStockFisik.setTag(getItem(position).getId());

            viewHolder.orderid.setTag(getItem(position).getId());
            viewHolder.nama_produk.setTag(getItem(position).getId());
            viewHolder.warehouse.setTag(getItem(position).getId());
            viewHolder.stokawal.setTag(getItem(position).getId());
            viewHolder.stokfisik.setTag(getItem(position).getId());
            viewHolder.positions.setTag(getItem(position).getId());

            viewHolder.minusUpdate.setTag(getItem(position).getId());
            viewHolder.addUpdate.setTag(getItem(position).getId());

            viewHolder.Updatestock_fisik.setTag(getItem(position).getId());
            viewHolder.apply.setTag(getItem(position).getId());
            viewHolder.status.setTag(getItem(position).getId());
            viewHolder.stokakhir.setTag(getItem(position).getId());

            viewHolder.orderid.setText(movieItem.getOrder_id());
            viewHolder.nama_produk.setText(movieItem.getOrder_name());
            viewHolder.warehouse.setText(movieItem.getName());
            viewHolder.stokawal.setText(movieItem.getStok_awal());

            viewHolder.positions.setVisibility(View.VISIBLE);
            viewHolder.positions.setText(String.valueOf(movieItem.getId() + 1));

            viewHolder.stokakhir.setText(movieItem.getStok_akhir());


            viewHolder.addUpdate.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    viewHolder.count++;
                    movieItem.setStok_opname_system(String.valueOf(viewHolder.count));
                    viewHolder.Updatestock_fisik.setText(movieItem.getStok_opname_system());

                }
            });

            viewHolder.minusUpdate.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (viewHolder.count != 0) {
                        viewHolder.count--;
                        movieItem.setStok_opname_system(String.valueOf(viewHolder.count));
                        viewHolder.Updatestock_fisik.setText(movieItem.getStok_opname_system());
                    }
                }
            });

            viewHolder.Updatestock_fisik.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if(viewHolder.Updatestock_fisik.getText().toString().length() == 0){

                    } else if(viewHolder.Updatestock_fisik.getText().toString().length() > 0 || movieItem.getId() == Integer.parseInt(viewHolder.positions.getText().toString())){
                        viewHolder.count = Integer.parseInt(viewHolder.Updatestock_fisik.getText().toString());
                        getItem(position).setStok_opname_system(String.valueOf(viewHolder.count));
                        viewHolder.Updatestock_fisik.setSelection(viewHolder.Updatestock_fisik.getText().length());
                        System.out.println("Data yang berubah = " + movieItem.getStok_opname_system() + " Di posisi = " + getItem(position).getId());
                    }
                }
            });
            viewHolder.stokfisik_layout.setVisibility(View.GONE);

            if(!condition.equals("outstanding")){

                if(movieItem.getRef_id().equals("null")){
                    viewHolder.status.setVisibility(View.VISIBLE);
                    viewHolder.status.setText(movieItem.getCondition());
                    viewHolder.stokfisik.setText(movieItem.getStok_opname());


                    if (movieItem.getCondition().equals("Pass")) {
                        viewHolder.updateStockFisik.setVisibility(View.GONE);
                        viewHolder.stokfisik_layout.setVisibility(View.VISIBLE);
                        viewHolder.status.setChipStrokeColor(ColorStateList.valueOf(Color.parseColor("#B9EED0")));
                        viewHolder.status.setChipBackgroundColor(ColorStateList.valueOf(Color.parseColor("#E8FFF2")));
                        viewHolder.status.setTextColor(Color.parseColor("#2ECC71"));
                    } else {
                        viewHolder.updateStockFisik.setVisibility(View.VISIBLE);
                        viewHolder.stokfisik_layout.setVisibility(View.GONE);
                        viewHolder.count = Integer.parseInt(movieItem.getStok_opname());
                        viewHolder.Updatestock_fisik.setText(String.valueOf(viewHolder.count));

                        viewHolder.status.setChipStrokeColor(ColorStateList.valueOf(Color.parseColor("#FEC0C0")));
                        viewHolder.status.setChipBackgroundColor(ColorStateList.valueOf(Color.parseColor("#FFF1F1")));
                        viewHolder.status.setTextColor(Color.parseColor("#FB4141"));
                    }
                } else {
                    viewHolder.status.setVisibility(View.VISIBLE);
                    viewHolder.status.setText(movieItem.getCondition());
                    viewHolder.stokfisik.setText(movieItem.getStok_opname());

                    viewHolder.updateStockFisik.setVisibility(View.GONE);
                    if (movieItem.getCondition().equals("Pass")) {
                        viewHolder.stokfisik_layout.setVisibility(View.VISIBLE);

                        viewHolder.status.setChipStrokeColor(ColorStateList.valueOf(Color.parseColor("#B9EED0")));
                        viewHolder.status.setChipBackgroundColor(ColorStateList.valueOf(Color.parseColor("#E8FFF2")));
                        viewHolder.status.setTextColor(Color.parseColor("#2ECC71"));
                    } else {
                        viewHolder.stokfisik_layout.setVisibility(View.VISIBLE);

                        if(movieItem.getStok_opname().equals("null")){
                            viewHolder.count = 0;
                        } else {
                            viewHolder.count = Integer.parseInt(movieItem.getStok_opname());

                        }
                        viewHolder.Updatestock_fisik.setText(String.valueOf(viewHolder.count));

                        viewHolder.status.setChipStrokeColor(ColorStateList.valueOf(Color.parseColor("#FEC0C0")));
                        viewHolder.status.setChipBackgroundColor(ColorStateList.valueOf(Color.parseColor("#FFF1F1")));
                        viewHolder.status.setTextColor(Color.parseColor("#FB4141"));
                    }
                }
            } else {
                viewHolder.updateStockFisik.setVisibility(View.GONE);
            }



            viewHolder.apply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(viewHolder.Updatestock_fisik.getText().length() == 0){
                        viewHolder.Updatestock_fisik.setError("Wajib Di isi");
                    } else {
                        list_stock_bulanan.setVisibility(View.GONE);
                        if(viewHolder.Updatestock_fisik.getText().toString().length() > 0 || movieItem.getId() == Integer.parseInt(viewHolder.positions.getText().toString())){
                            movieItem.setEditting("1");

                            viewHolder.stokfisik.setText(movieItem.getStok_opname_system());

                            if(!condition.equals("outstanding")){
                                viewHolder.status.setVisibility(View.VISIBLE);
                                viewHolder.status.setText(movieItem.getCondition());
                                viewHolder.stokfisik.setText(movieItem.getStok_opname());

                                viewHolder.stokfisik_layout.setVisibility(View.VISIBLE);
                                viewHolder.updateStockFisik.setVisibility(View.GONE);

                                if (movieItem.getCondition().equals("Pass")) {
                                    viewHolder.status.setChipStrokeColor(ColorStateList.valueOf(Color.parseColor("#B9EED0")));
                                    viewHolder.status.setChipBackgroundColor(ColorStateList.valueOf(Color.parseColor("#E8FFF2")));
                                    viewHolder.status.setTextColor(Color.parseColor("#2ECC71"));
                                } else {
                                    viewHolder.status.setChipStrokeColor(ColorStateList.valueOf(Color.parseColor("#FEC0C0")));
                                    viewHolder.status.setChipBackgroundColor(ColorStateList.valueOf(Color.parseColor("#FFF1F1")));
                                    viewHolder.status.setTextColor(Color.parseColor("#FB4141"));
                                }
                            } else {
                                viewHolder.stokfisik.setText(movieItem.getStok_opname_system());
                                if(movieItem.getStok_opname_system().equals("null")){
                                    viewHolder.stokfisik_layout.setVisibility(View.GONE);
                                    viewHolder.updateStockFisik.setVisibility(View.VISIBLE);

                                } else {
                                    viewHolder.stokfisik_layout.setVisibility(View.VISIBLE);
                                    viewHolder.updateStockFisik.setVisibility(View.GONE);

                                    if (movieItem.getStok_opname_system().equals(movieItem.getStok_akhir())) {
                                        viewHolder.status.setVisibility(View.VISIBLE);
                                        viewHolder.status.setText("Pass");
                                        viewHolder.status.setChipStrokeColor(ColorStateList.valueOf(Color.parseColor("#B9EED0")));
                                        viewHolder.status.setChipBackgroundColor(ColorStateList.valueOf(Color.parseColor("#E8FFF2")));
                                        viewHolder.status.setTextColor(Color.parseColor("#2ECC71"));
                                    } else if (movieItem.getStok_opname_system().equals("null")) {
                                        viewHolder.status.setVisibility(View.GONE);
                                    } else {
                                        viewHolder.status.setVisibility(View.VISIBLE);
                                        viewHolder.status.setText("Invalid");
                                        viewHolder.status.setChipStrokeColor(ColorStateList.valueOf(Color.parseColor("#FEC0C0")));
                                        viewHolder.status.setChipBackgroundColor(ColorStateList.valueOf(Color.parseColor("#FFF1F1")));
                                        viewHolder.status.setTextColor(Color.parseColor("#FB4141"));
                                    }
                                }
                                notifyDataSetChanged();
                            }

                            tablayout.getTabAt(1).select();


                            StringRequest stringRequest2 = new StringRequest(Request.Method.PUT, "https://apisec.tvip.co.id/rest_server_stock_opname/Stock/index_stock_update_bulanan",
                                    new Response.Listener<String>() {

                                        @Override
                                        public void onResponse(String response) {

                                        }
                                    }, new Response.ErrorListener() {
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

                                @Override
                                protected Map<String, String> getParams() throws AuthFailureError {
                                    Map<String, String> params = new HashMap<String, String>();
                                    sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
                                    String nik_baru = sharedPreferences.getString("nik_baru", null);

                                    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                    String currentDateandTime2 = sdf2.format(new Date());

                                    params.put("tgl_opname", movieItem.getTgl_opname());
                                    params.put("warehouse", movieItem.getWarehouse());
                                    params.put("order_id", movieItem.getOrder_id());

                                    params.put("stok_opname", viewHolder.Updatestock_fisik.getText().toString());
                                    params.put("last_update", currentDateandTime2);

                                    params.put("stok_opname_old", movieItem.getStok_opname());


                                    System.out.println("Update Params = " + params);

                                    return params;
                                }

                            };
                            stringRequest2.setRetryPolicy(
                                    new DefaultRetryPolicy(
                                            15000,
                                            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                                    )
                            );
                            RequestQueue requestQueue2 = Volley.newRequestQueue(validasi_bulanan.this);
                            requestQueue2.getCache().clear();
                            requestQueue2.add(stringRequest2);
                        }
                    }

                }
            });






            return convertView2;
        }
    }

    private void searchOutstanding(String toString) {


        adapter = new ListViewAdapterBulanan(sparepartBulanan_pojosList, getApplicationContext());
        adapter.clear();

        String category;

        if(kategoriSparepart.equals("Chasis & Tools")){
            category = "Chasis%20%26%20tools";
        } else if(kategoriSparepart.equals("Dan Lain-lain")){
            category = "other";
        } else {
            category = kategoriSparepart;
        }



        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/rest_server_stock_opname/Stock/index_stock_bulanan_search?warehouse="+menu_stockopname.warehouse+"&kategori="+category+"&sparepart="+toString,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        int totalItem = 0;
                        list_stock_bulanan.setVisibility(VISIBLE);
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (obj.getString("status").equals("true")) {

                                final JSONArray movieArray = obj.getJSONArray("data");

                                for (int i = 0; i < movieArray.length(); i++) {


                                    final JSONObject movieObject = movieArray.getJSONObject(i);

                                    final sparepartBulanan_pojos movieItem = new sparepartBulanan_pojos(
                                            i,
                                            movieObject.getString("order_id"),
                                            movieObject.getString("order_name"),
                                            movieObject.getString("warehouse"),
                                            movieObject.getString("name"),
                                            movieObject.getString("stok_awal"),
                                            movieObject.getString("stok_akhir"),
                                            movieObject.getString("stok_opname"),
                                            movieObject.getString("condition"),
                                            "0",
                                            movieObject.getString("in"),
                                            movieObject.getString("out"),
                                            "0",
                                            movieObject.getString("tgl_opname"),
                                            "");

                                    sparepartBulanan_pojosList.add(movieItem);
                                    list_stock_bulanan.setAdapter(adapter);
                                    adapter.notifyDataSetChanged();

                                    pDialog.dismissWithAnimation();


                                    // adapter.notifyDataSetChanged();
                                }
                            } else {
                                pDialog.dismissWithAnimation();
                                list_stock_bulanan.setVisibility(GONE);
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
                        list_stock_bulanan.setVisibility(GONE);
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
        if (requestQueue4 == null) {
            requestQueue4 = Volley.newRequestQueue(validasi_bulanan.this);
            requestQueue4.add(stringRequest);
        } else {
            requestQueue4.add(stringRequest);
        }
    }

    private void searchFinished(String toString) {


        adapter = new ListViewAdapterBulanan(sparepartBulanan_pojosList, getApplicationContext());
        adapter.clear();

        String category;

        if(kategoriSparepart.equals("Chasis & Tools")){
            category = "Chasis%20%26%20tools";
        } else if(kategoriSparepart.equals("Dan Lain-lain")){
            category = "other";
        } else {
            category = kategoriSparepart;
        }


        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/rest_server_stock_opname/Stock/index_stock_bulanan_complete_search?warehouse="+menu_stockopname.warehouse+"&kategori="+category+"&sparepart="+toString,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        int totalItem = 0;
                        list_stock_bulanan.setVisibility(VISIBLE);
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (obj.getString("status").equals("true")) {
                                final JSONArray movieArray = obj.getJSONArray("data");

                                for (int i = 0; i < movieArray.length(); i++) {


                                    final JSONObject movieObject = movieArray.getJSONObject(i);

                                    final sparepartBulanan_pojos movieItem = new sparepartBulanan_pojos(
                                            i,
                                            movieObject.getString("order_id"),
                                            movieObject.getString("order_name"),
                                            movieObject.getString("warehouse"),
                                            movieObject.getString("name"),
                                            movieObject.getString("stok_awal"),
                                            movieObject.getString("stok_akhir"),
                                            movieObject.getString("stok_opname"),
                                            movieObject.getString("condition"),
                                            "0",
                                            movieObject.getString("in"),
                                            movieObject.getString("out"),
                                            "0",
                                            movieObject.getString("tgl_opname"),
                                            "");

                                    sparepartBulanan_pojosList.add(movieItem);
                                    list_stock_bulanan.setAdapter(adapter);
                                    adapter.notifyDataSetChanged();

                                    pDialog.dismissWithAnimation();


                                    // adapter.notifyDataSetChanged();
                                }
                            } else {
                                pDialog.dismissWithAnimation();
                                list_stock_bulanan.setVisibility(GONE);
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
                        list_stock_bulanan.setVisibility(GONE);
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
        if (requestQueue4 == null) {
            requestQueue4 = Volley.newRequestQueue(validasi_bulanan.this);
            requestQueue4.add(stringRequest);
        } else {
            requestQueue4.add(stringRequest);
        }
    }

    private void setSearchViewEnabled(boolean enabled) {
        searchview.setEnabled(enabled);
        if (enabled) {
            // Re-attach the TextWatcher when enabled
            searchview.addTextChangedListener(textWatcher);
        } else {
            // Remove the TextWatcher when disabled
            searchview.removeTextChangedListener(textWatcher);
        }
    }
}