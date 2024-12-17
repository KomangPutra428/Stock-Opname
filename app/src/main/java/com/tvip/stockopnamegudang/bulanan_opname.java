package com.tvip.stockopnamegudang;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.tvip.stockopnamegudang.menu_stockopname.kategoriSparepart;
import static com.tvip.stockopnamegudang.menu_stockopname.warehousechoose;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.omadahealth.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.omadahealth.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;
import com.tvip.stockopnamegudang.pojos.sparepartBulanan_pojos;
import com.tvip.stockopnamegudang.pojos.sparepart_pojos;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class bulanan_opname extends AppCompatActivity {

    ListView list_stock_bulanan;
    LinearLayout refresh_layout;

    private Handler handler = new Handler();
    private Runnable typingRunnable;
    private static final long TYPING_DELAY = 500;

    RequestQueue requestQueue1, requestQueue2, requestQueue3, requestQueue4;

    String gudang;
    String needLoading;
    Chip total;
    ArrayList<sparepartBulanan_pojos> sparepartBulanan_pojosList = new ArrayList<>();
    ListViewAdapterBulanan adapter;
    SweetAlertDialog pDialog, success;
    SharedPreferences sharedPreferences;

    int counter;

    private TextWatcher textWatcher;
    MaterialToolbar AppToolBar;
    String all;

    String condition;

    TabLayout tablayout;

    EditText searchview;

    SwipyRefreshLayout swipeRefreshLayout;

    MaterialButton simpan, load_more;

    int loadmore;

    String link;

    String adding;

    String done, counting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bulanan_opname);
        list_stock_bulanan = findViewById(R.id.list_stock_bulanan);
        tablayout = findViewById(R.id.tablayout);
        total = findViewById(R.id.total);
        searchview = findViewById(R.id.searchview);
        simpan = findViewById(R.id.simpan);
        refresh_layout = findViewById(R.id.refresh_layout);

        list_stock_bulanan.setScrollingCacheEnabled(false);
        load_more = findViewById(R.id.load_more);
        AppToolBar = findViewById(R.id.AppToolBar);




        loadmore = 0;

        simpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                searchview.getText().clear();


                pDialog = new SweetAlertDialog(bulanan_opname.this, SweetAlertDialog.PROGRESS_TYPE);
                pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                pDialog.setTitleText("Harap Menunggu");
                pDialog.setCancelable(false);
                pDialog.show();

                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        final CountDownLatch latch = new CountDownLatch(sparepartBulanan_pojosList.size());

                        ExecutorService executorService = Executors.newFixedThreadPool(10); // Adjust the pool size based on your needs

                        for (int e = 0; e < sparepartBulanan_pojosList.size(); e++) {
                            final int finalE = e;
                            executorService.submit(new Runnable() {
                                @Override
                                public void run() {
                                    if (adapter.getItem(finalE).getStok_opname_system() == null || adapter.getItem(finalE).getStok_opname_system().isEmpty()) {
                                        // Handle case where stock_opname_system is null or empty
                                        latch.countDown();
                                    } else {
                                        StringRequest stringRequest2 = new StringRequest(Request.Method.POST, "https://apisec.tvip.co.id/rest_server_stock_opname/Stock/index_stockBulanan",
                                                new Response.Listener<String>() {
                                                    @Override
                                                    public void onResponse(String response) {
                                                        // On successful response, count down the latch
                                                        latch.countDown();
                                                    }
                                                },
                                                new Response.ErrorListener() {
                                                    @Override
                                                    public void onErrorResponse(VolleyError error) {
                                                        // On error response, count down the latch
                                                        latch.countDown();
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

                                                String[] parts = nik_baru.split("-");
                                                String branch = parts[0];

                                                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                                String currentDateandTime2 = sdf2.format(new Date());

                                                SimpleDateFormat year = new SimpleDateFormat("yyyy");
                                                String years = year.format(new Date());

                                                SimpleDateFormat month = new SimpleDateFormat("M");
                                                String months = month.format(new Date());

                                                params.put("bulan", months);
                                                params.put("tahun", years);
                                                params.put("warehouse", menu_stockopname.warehousechoose);

                                                params.put("tgl_opname", currentDateandTime2);

                                                params.put("order_id", adapter.getItem(finalE).getOrder_id());
                                                params.put("stok_awal", adapter.getItem(finalE).getStok_awal());

                                                params.put("in", adapter.getItem(finalE).getIn());
                                                params.put("out", adapter.getItem(finalE).getOut());
                                                params.put("stok_akhir", adapter.getItem(finalE).getStok_akhir());
                                                params.put("stok_opname", adapter.getItem(finalE).getStok_opname_system());

                                                params.put("nik_pic", nik_baru);
                                                System.out.println("Params posting = " + params);

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

                                        if (requestQueue2 == null) {
                                            requestQueue2 = Volley.newRequestQueue(bulanan_opname.this);
                                        }
                                        requestQueue2.add(stringRequest2);
                                    }
                                }
                            });
                        }

// Wait for all requests to complete
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    latch.await(); // Wait for all tasks to finish
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            new SweetAlertDialog(bulanan_opname.this, SweetAlertDialog.SUCCESS_TYPE)
                                                    .setContentText("Data Sudah Diupdate")
                                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                        @Override
                                                        public void onClick(SweetAlertDialog sDialog) {
                                                            sDialog.dismissWithAnimation();
                                                            pDialog.dismissWithAnimation();
                                                            tablayout.getTabAt(1).select();
                                                            searchview.getText().clear();
                                                        }
                                                    })
                                                    .show();
                                        }
                                    });
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();

// Shutdown the executor service
                        executorService.shutdown();
                    }
                }, 3000);







//                    getCekStatus();
//                }
            }
        });



        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);

        condition = "outstanding";
        link = "index_stock_bulanan";
        needLoading = "yes";
        getLocationWarehouse();
        all = "outstanding";

        adding = "0";

        load_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adding = "1";
                needLoading = "yes";
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
                        adapter.getFilter().filter(s);
//                        if (!s.toString().trim().isEmpty()) {
//                            System.out.println("Link Jenis = " + link);
//                            if (link.equals("index_stock_bulanan")) {
//                                pDialog = new SweetAlertDialog(bulanan_opname.this, SweetAlertDialog.PROGRESS_TYPE);
//                                pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
//                                pDialog.setTitleText("Harap Menunggu");
//                                pDialog.setCancelable(false);
//                                pDialog.show();
//
//                                searchOutstanding(s.toString());
//                            } else {
//                                pDialog = new SweetAlertDialog(bulanan_opname.this, SweetAlertDialog.PROGRESS_TYPE);
//                                pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
//                                pDialog.setTitleText("Harap Menunggu");
//                                pDialog.setCancelable(false);
//                                pDialog.show();
//
//                                searchFinished(s.toString());
//                            }
//                        } else {
//                            needLoading = "yes";
//                            getLocationWarehouse();
//                        }
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
                int position = tab.getPosition();
                if(position == 0){
                    all = "outstanding";
                    simpan.setVisibility(VISIBLE);
                    link = "index_stock_bulanan";
                    adding = "0";
                    needLoading = "yes";
                    setSearchViewEnabled(false);
                    searchview.getText().clear();
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            setSearchViewEnabled(true);
                        }
                    }, 2000);

                    getLocationWarehouse();
                    condition = "outstanding";
                    list_stock_bulanan.setVisibility(GONE);
                    refresh_layout.setVisibility(GONE);
                } else if (position == 1){
                    all = "all";
                    simpan.setVisibility(GONE);
                    link = "index_stock_bulanan_complete";
                    adding = "0";
                    needLoading = "yes";
                    setSearchViewEnabled(false);
                    searchview.getText().clear();
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            setSearchViewEnabled(true);
                        }
                    }, 2000);
                    getLocationWarehouse();
                    condition = "bergerak";
                    list_stock_bulanan.setVisibility(GONE);
                    refresh_layout.setVisibility(GONE);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                if(position == 0){
                    all = "outstanding";
                    simpan.setVisibility(VISIBLE);
                    link = "index_stock_bulanan";
                    adding = "0";
                    needLoading = "yes";
                    getLocationWarehouse();
                    condition = "outstanding";
                } else if (position == 1){
                    all = "all";
                    simpan.setVisibility(GONE);
                    link = "index_stock_bulanan_complete";
                    adding = "0";
                    needLoading = "yes";
                    getLocationWarehouse();
                    condition = "bergerak";
                }
            }
        });

        AppToolBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                long CLICK_THRESHOLD = 1000; // Threshold time in milliseconds (1 second)
                long lastClickTime = 0; // To track the last click time

                long currentTime = SystemClock.elapsedRealtime();
                if (currentTime - lastClickTime < CLICK_THRESHOLD) {
                    return true;
                }

                searchview.getText().clear();
                switch (item.getItemId()) {
                    case R.id.page_1:
                        all = "outstanding";
                        tablayout.setScrollPosition(0, 0f, true);
                        simpan.setVisibility(VISIBLE);
                        link = "index_stock_bulanan";
                        adding = "0";
                        needLoading = "yes";
                        setSearchViewEnabled(false);
                        searchview.getText().clear();
                        new Handler().postDelayed(new Runnable() {
                            public void run() {
                                setSearchViewEnabled(true);
                            }
                        }, 2000);

                        getLocationWarehouse();
                        condition = "outstanding";
                        list_stock_bulanan.setVisibility(GONE);
                        refresh_layout.setVisibility(GONE);
                        return true;
                    case R.id.page_2:
                        all = "pass";
                        tablayout.setScrollPosition(1, 0f, true);
                        simpan.setVisibility(GONE);
                        link = "index_stock_bulanan_complete";
                        adding = "0";
                        needLoading = "yes";
                        setSearchViewEnabled(false);
                        searchview.getText().clear();
                        new Handler().postDelayed(new Runnable() {
                            public void run() {
                                setSearchViewEnabled(true);
                            }
                        }, 2000);
                        getLocationWarehouse();
                        condition = "bergerak";
                        list_stock_bulanan.setVisibility(GONE);
                        refresh_layout.setVisibility(GONE);
                        return true;
                    case R.id.page_3:
                        all = "invalid";
                        tablayout.setScrollPosition(1, 0f, true);
                        simpan.setVisibility(GONE);
                        link = "index_stock_bulanan_complete";
                        adding = "0";
                        needLoading = "yes";
                        setSearchViewEnabled(false);
                        searchview.getText().clear();
                        new Handler().postDelayed(new Runnable() {
                            public void run() {
                                setSearchViewEnabled(true);
                            }
                        }, 2000);
                        getLocationWarehouse();
                        condition = "bergerak";
                        list_stock_bulanan.setVisibility(GONE);
                        refresh_layout.setVisibility(GONE);
                        return true;
                }
                return false;
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
                                needLoading = "yes";
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
                                needLoading = "no";
                                getSpareParts(warehousechoose);
                            }

                        }
                    }, 3000);
                }
            }
        });



    }

    private void cekButton() {
        {
            SimpleDateFormat year = new SimpleDateFormat("yyyy");
            String years = year.format(new Date());

            SimpleDateFormat month = new SimpleDateFormat("M");
            String months = month.format(new Date());

            StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/rest_server_stock_opname/Stock/index_stock_bulanan_cek?bulan=" + months+ "&tahun=" + years + "&warehouse=" + menu_stockopname.warehousechoose,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            simpan.setBackgroundColor(Color.parseColor("#0F4C81"));
                            simpan.setTextColor(Color.parseColor("#ffffff"));
                            simpan.setEnabled(true);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            simpan.setBackgroundColor(Color.parseColor("#D3D3D3"));
                            simpan.setTextColor(Color.parseColor("#808080"));
                            simpan.setEnabled(false);

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
                            5000,
                            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                    ));
            if (requestQueue2 == null) {
                requestQueue2 = Volley.newRequestQueue(bulanan_opname.this);
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
                success = new SweetAlertDialog(bulanan_opname.this, 2);
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
        RequestQueue requestQueue2 = Volley.newRequestQueue(bulanan_opname.this);
        requestQueue2.getCache().clear();
        requestQueue2.add(stringRequest2);

    }

    private void getCekStatusAfter() {
        {
            SimpleDateFormat year = new SimpleDateFormat("yyyy");
            String years = year.format(new Date());

            SimpleDateFormat month = new SimpleDateFormat("M");
            String months = month.format(new Date());

            StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/rest_server_stock_opname/Stock/index_stock_bulanan_cek?bulan=" + months+ "&tahun=" + years + "&warehouse=" + menu_stockopname.warehousechoose,
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
                            5000,
                            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                    ));

            if (requestQueue1 == null) {
                requestQueue1 = Volley.newRequestQueue(bulanan_opname.this);
                requestQueue1.add(stringRequest);
            } else {
                requestQueue1.add(stringRequest);
            }
        }
    }

    private void getLocationWarehouse() {
        pDialog = new SweetAlertDialog(bulanan_opname.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Harap Menunggu");
        pDialog.setCancelable(false);
        pDialog.show();

        if(pDialog.isShowing()){

        } else {
            pDialog.dismissWithAnimation();
        }



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

                            gudang = menu_stockopname.warehousechoose;

                            getHitung(menu_stockopname.warehousechoose);
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

        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/rest_server_stock_opname/Stock/index_stock_bulanan_cek?bulan=" + months+ "&tahun=" + years + "&warehouse=" + menu_stockopname.warehousechoose,
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
                        success = new SweetAlertDialog(bulanan_opname.this, SweetAlertDialog.SUCCESS_TYPE);
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
                        5000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                ));
        if (requestQueue2 == null) {
            requestQueue2 = Volley.newRequestQueue(bulanan_opname.this);
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

        System.out.println("LINK API COUNT = " + "https://apisec.tvip.co.id/rest_server_stock_opname/Stock/index_stock_bulanan_count"+"?warehouse=" + menu_stockopname.warehousechoose+ "&load=" + String.valueOf(loadmore) + "&kategori=" + category);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/rest_server_stock_opname/Stock/index_stock_bulanan_count"+"?warehouse=" + menu_stockopname.warehousechoose+ "&load=" + String.valueOf(loadmore) + "&kategori=" + category,
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



                                if(dikerjakan == 0 && alldata == 0){
                                    pDialog.dismissWithAnimation();
                                    total.setChipStrokeColor(ColorStateList.valueOf(Color.parseColor("#FEC0C0")));
                                    total.setChipBackgroundColor(ColorStateList.valueOf(Color.parseColor("#FFF1F1")));
                                    total.setTextColor(Color.parseColor("#FB4141"));
                                    new SweetAlertDialog(bulanan_opname.this, SweetAlertDialog.WARNING_TYPE)
                                            .setContentText("Maaf, data sparepart tidak ada")
                                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                @Override
                                                public void onClick(SweetAlertDialog sDialog) {
                                                    sDialog.dismissWithAnimation();
                                                    finish();
                                                }
                                            })
                                            .show();
                                } else if(dikerjakan == alldata){
                                    pDialog.dismissWithAnimation();
                                    total.setChipStrokeColor(ColorStateList.valueOf(Color.parseColor("#B9EED0")));
                                    total.setChipBackgroundColor(ColorStateList.valueOf(Color.parseColor("#E8FFF2")));
                                    total.setTextColor(Color.parseColor("#2ECC71"));
                                    new SweetAlertDialog(bulanan_opname.this, SweetAlertDialog.WARNING_TYPE)
                                            .setContentText("Maaf, data sparepart sudah diisi")
                                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                @Override
                                                public void onClick(SweetAlertDialog sDialog) {
                                                    sDialog.dismissWithAnimation();
                                                    finish();
                                                }
                                            })
                                            .show();
                                } else {
                                    getSpareParts(menu_stockopname.warehousechoose);
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
            requestQueue3 = Volley.newRequestQueue(bulanan_opname.this);
            requestQueue3.add(stringRequest);
        } else {
            requestQueue3.add(stringRequest);
        }
    }

    private void getSpareParts(String warehouse) {

        if(pDialog.isShowing()){

        } else {
            pDialog.dismissWithAnimation();
        }

        int default_angka = 15;
        int sekarang = loadmore + default_angka;

        if(adding.equals("1")){
            loadmore = sekarang;
        } else {
            loadmore = 100;
        }

        System.out.println("Pagination = " + String.valueOf(loadmore));
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

        System.out.println("Link API = " + "https://apisec.tvip.co.id/rest_server_stock_opname/Stock/"+link+"?warehouse=" + menu_stockopname.warehousechoose + "&load=" + String.valueOf(loadmore) + "&kategori=" + kategoriSparepart);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/rest_server_stock_opname/Stock/"+link+"?warehouse=" + menu_stockopname.warehousechoose + "&load=" + "10000" + "&kategori=" + category,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        int totalItem = 0;
                        list_stock_bulanan.setVisibility(VISIBLE);
                        try {
                            JSONObject obj = new JSONObject(response);
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
                                        null,
                                        movieObject.getString("in"),
                                        movieObject.getString("out"),
                                        "0",
                                        movieObject.getString("tgl_opname"),
                                        "");

                                sparepartBulanan_pojosList.add(movieItem);
                                list_stock_bulanan.setAdapter(adapter);
                                adapter.notifyDataSetChanged();

                                if(all.equals("invalid")){
                                    if(movieObject.getString("condition").equals("Pass")){
                                        sparepartBulanan_pojosList.remove(movieItem);
                                        adapter.notifyDataSetChanged();
                                    }
                                } else if (all.equals("pass")){
                                    if(movieObject.getString("condition").equals("Invalid")){
                                        sparepartBulanan_pojosList.remove(movieItem);
                                        adapter.notifyDataSetChanged();
                                    }
                                }



                                if(link.equals("index_stock_bulanan_complete")){
                                    list_stock_bulanan.setVisibility(VISIBLE);

                                    if(movieObject.getString("tgl_opname").equals("null")){
                                        sparepartBulanan_pojosList.remove(movieItem);
                                        adapter.notifyDataSetChanged();
                                    }
                                } else {

                                }


                                // adapter.notifyDataSetChanged();

                            }

                            pDialog.dismissWithAnimation();
                            new Handler().postDelayed(new Runnable() {
                                public void run() {
                                    pDialog.dismissWithAnimation();
                                }
                            }, 2000);

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
                        5000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                ));
        if (requestQueue4 == null) {
            requestQueue4 = Volley.newRequestQueue(bulanan_opname.this);
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


        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/rest_server_stock_opname/Stock/index_stock_bulanan_complete_search?warehouse="+menu_stockopname.warehousechoose+"&kategori="+category+"&sparepart="+toString,
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
                        5000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                ));
        if (requestQueue4 == null) {
            requestQueue4 = Volley.newRequestQueue(bulanan_opname.this);
            requestQueue4.add(stringRequest);
        } else {
            requestQueue4.add(stringRequest);
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



        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://apisec.tvip.co.id/rest_server_stock_opname/Stock/index_stock_bulanan_search?warehouse="+menu_stockopname.warehousechoose+"&kategori="+category+"&sparepart="+toString,
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
                        5000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                ));
        if (requestQueue4 == null) {
            requestQueue4 = Volley.newRequestQueue(bulanan_opname.this);
            requestQueue4.add(stringRequest);
        } else {
            requestQueue4.add(stringRequest);
        }
    }

    public class ListViewAdapterBulanan extends ArrayAdapter<sparepartBulanan_pojos> implements Filterable{

        LayoutInflater inflater;
        ArrayList<sparepartBulanan_pojos> sparepartBulanan_pojoss;
        ArrayList<sparepartBulanan_pojos> sparepartBulanan_pojoss_filtered;

        private final Context context;

        public boolean  clearFocusFromEditTexts() {
            boolean hasFocus = false;
            for (int i = 0; i < list_stock_bulanan.getChildCount(); i++) {
                View view = list_stock_bulanan.getChildAt(i);
                EditText editText = view.findViewById(R.id.Updatestock_fisik);
                if (editText != null && editText.hasFocus()) {
                    editText.clearFocus();
                    hasFocus = true;
                }
            }
            return hasFocus;

        }

        private class ViewHolder {
            TextView orderid, nama_produk, warehouse, stokawal, stokfisik, positions, stokakhir;
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
                    if(link.equals("index_stock_bulanan_complete")){
                        if (results != null && results.count > 0) {
                            // Filtered data is available, show the ListView
                            list_stock_bulanan.setVisibility(VISIBLE);
                        } else {
                            // No filtered data available, hide the ListView

                            list_stock_bulanan.setVisibility(VISIBLE);
                        }
                    } else {
                        if (results != null && results.count > 0) {
                            // Filtered data is available, show the ListView
                            list_stock_bulanan.setVisibility(VISIBLE);
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
            final ViewHolder viewHolder;
            sparepartBulanan_pojos movieItem = getItem(position);
            final int pos = position;
            if (convertView == null) {
                viewHolder = new ViewHolder();
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

                viewHolder.Updatestock_fisik.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        sparepartBulanan_pojos item = getItem(pos);
                        if (item != null) {
                            if (s == null || s.length() == 0) {
                                item.setStok_opname_system(null);
                            } else {
                                try {
                                    int newValue = Integer.parseInt(s.toString());
                                    viewHolder.count = newValue;
                                    item.setStok_opname_system(String.valueOf(newValue));
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
            viewHolder.positions.setText(String.valueOf(movieItem.getId() + 1));
            viewHolder.stokakhir.setText(movieItem.getStok_akhir());

            viewHolder.positions.setVisibility(VISIBLE);


            if(movieItem.getStok_opname_system() == null){
                viewHolder.Updatestock_fisik.setText("");
            } else {
                viewHolder.Updatestock_fisik.setText(movieItem.getStok_opname_system());
            }

            viewHolder.Updatestock_fisik.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    // Clear focus when the user is done editing
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        viewHolder.Updatestock_fisik.clearFocus();
                        // Hide the keyboard
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(viewHolder.Updatestock_fisik.getWindowToken(), 0);
                        return true;
                    }
                    return false;
                }
            });


            viewHolder.addUpdate.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    viewHolder.count++;
                    movieItem.setStok_opname_system(String.valueOf(viewHolder.count));
                    viewHolder.Updatestock_fisik.setText(String.valueOf(viewHolder.count));
                }
            });


            viewHolder.minusUpdate.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (viewHolder.count > 0) {
                        viewHolder.count--;
                        movieItem.setStok_opname_system(String.valueOf(viewHolder.count));
                        viewHolder.Updatestock_fisik.setText(String.valueOf(viewHolder.count));
                    }
                }
            });

            if(!condition.equals("outstanding")){
                viewHolder.status.setVisibility(VISIBLE);
                viewHolder.status.setText(movieItem.getCondition());
                viewHolder.stokfisik.setText(movieItem.getStok_opname());

                viewHolder.stokfisik_layout.setVisibility(VISIBLE);
                viewHolder.updateStockFisik.setVisibility(GONE);

                if (movieItem.getCondition().equals("Pass")) {
                    viewHolder.status.setChipStrokeColor(ColorStateList.valueOf(Color.parseColor("#B9EED0")));
                    viewHolder.status.setChipBackgroundColor(ColorStateList.valueOf(Color.parseColor("#E8FFF2")));
                    viewHolder.status.setTextColor(Color.parseColor("#2ECC71"));
                } else {
                    viewHolder.status.setChipStrokeColor(ColorStateList.valueOf(Color.parseColor("#FEC0C0")));
                    viewHolder.status.setChipBackgroundColor(ColorStateList.valueOf(Color.parseColor("#FFF1F1")));
                    viewHolder.status.setTextColor(Color.parseColor("#FB4141"));
                }
            }



            viewHolder.apply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(viewHolder.Updatestock_fisik.getText().length() == 0){
                        viewHolder.Updatestock_fisik.setError("Wajib Di isi");
                    } else {
                        searchview.getText().clear();
                         if(viewHolder.Updatestock_fisik.getText().toString().length() > 0 || movieItem.getId() == Integer.parseInt(viewHolder.positions.getText().toString())){
                             movieItem.setEditting("1");

                             viewHolder.stokfisik.setText(movieItem.getStok_opname_system());

                             if(!condition.equals("outstanding")){
                                 viewHolder.status.setVisibility(VISIBLE);
                                 viewHolder.status.setText(movieItem.getCondition());
                                 viewHolder.stokfisik.setText(movieItem.getStok_opname());

                                 viewHolder.stokfisik_layout.setVisibility(VISIBLE);
                                 viewHolder.updateStockFisik.setVisibility(GONE);

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
                                 if(movieItem.getStok_opname_system() == null){
                                     viewHolder.stokfisik_layout.setVisibility(GONE);
                                     viewHolder.updateStockFisik.setVisibility(VISIBLE);
                                 } else if(movieItem.getStok_opname_system().equals("null")){
                                     viewHolder.stokfisik_layout.setVisibility(GONE);
                                     viewHolder.updateStockFisik.setVisibility(VISIBLE);

                                 } else {
                                     viewHolder.stokfisik_layout.setVisibility(VISIBLE);
                                     viewHolder.updateStockFisik.setVisibility(GONE);

                                     if (movieItem.getStok_opname_system().equals(movieItem.getStok_akhir())) {
                                         viewHolder.status.setVisibility(VISIBLE);
                                         viewHolder.status.setText("Pass");
                                         viewHolder.status.setChipStrokeColor(ColorStateList.valueOf(Color.parseColor("#B9EED0")));
                                         viewHolder.status.setChipBackgroundColor(ColorStateList.valueOf(Color.parseColor("#E8FFF2")));
                                         viewHolder.status.setTextColor(Color.parseColor("#2ECC71"));
                                     } else if (movieItem.getStok_opname_system().equals("null")) {
                                         viewHolder.status.setVisibility(GONE);
                                     } else {
                                         viewHolder.status.setVisibility(VISIBLE);
                                         viewHolder.status.setText("Invalid");
                                         viewHolder.status.setChipStrokeColor(ColorStateList.valueOf(Color.parseColor("#FEC0C0")));
                                         viewHolder.status.setChipBackgroundColor(ColorStateList.valueOf(Color.parseColor("#FFF1F1")));
                                         viewHolder.status.setTextColor(Color.parseColor("#FB4141"));
                                     }
                                 }
                             }

                             tablayout.getTabAt(1).select();

                             StringRequest stringRequest2 = new StringRequest(Request.Method.POST, "https://apisec.tvip.co.id/rest_server_stock_opname/Stock/index_stockBulanan",
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

                                     String[] parts = nik_baru.split("-");
                                     String branch = parts[0];

                                     SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                     String currentDateandTime2 = sdf2.format(new Date());

                                     SimpleDateFormat year = new SimpleDateFormat("yyyy");
                                     String years = year.format(new Date());

                                     SimpleDateFormat month = new SimpleDateFormat("M");
                                     String months = month.format(new Date());

                                     params.put("bulan", months);
                                     params.put("tahun", years);
                                     params.put("warehouse", menu_stockopname.warehousechoose);

                                     params.put("tgl_opname", currentDateandTime2);

                                     params.put("order_id", movieItem.getOrder_id());
                                     params.put("stok_awal", movieItem.getStok_awal());

                                     params.put("in", movieItem.getIn());
                                     params.put("out", movieItem.getOut());
                                     params.put("stok_akhir", movieItem.getStok_akhir());
                                     params.put("stok_opname", viewHolder.Updatestock_fisik.getText().toString());

                                     params.put("nik_pic", nik_baru);
                                     System.out.println("Params posting = " + params);

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
                             RequestQueue requestQueue2 = Volley.newRequestQueue(bulanan_opname.this);
                             requestQueue2.getCache().clear();
                             requestQueue2.add(stringRequest2);
                         }
                    }

                }
            });






            return convertView2;
        }
    }
    @Override
    public void onBackPressed() {

        // If no EditText has focus, finish the activity
        boolean isSoftKeyboardVisible = isSoftKeyboardVisible();

        // Clear focus from all EditText fields in the adapter
        boolean anyEditTextHasFocus = adapter.clearFocusFromEditTexts();

        // If no EditText has focus and the soft keyboard is not visible, finish the activity
        if (!anyEditTextHasFocus || !isSoftKeyboardVisible) {
            finish();
        } else {
            // Otherwise, let the super handle the back press
            super.onBackPressed();
        }


        // Call super onBackPressed
    }

    private boolean isSoftKeyboardVisible() {
        // Check if the soft keyboard is visible by measuring the height of the root view
        View rootView = getWindow().getDecorView().getRootView();
        Rect r = new Rect();
        rootView.getWindowVisibleDisplayFrame(r);
        int screenHeight = rootView.getHeight();
        int heightDiff = screenHeight - (r.bottom - r.top);
        int pixelThreshold = 100; // Adjust this threshold as needed
        return heightDiff > pixelThreshold;
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