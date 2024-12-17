package com.tvip.stockopnamegudang;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.tvip.stockopnamegudang.pojos.sparepart_pojos;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class validasi_stockopname extends AppCompatActivity {

    MaterialToolbar AppToolBar;

    MaterialCardView card_layout_simpan;
    EditText searchview;
    TextView totalstock, total, refreshText, totaloutstanding;

    RelativeLayout invalidkosongan;


    SwipeRefreshLayout swipeRefreshLayout;
    ListView list_stock;

    SweetAlertDialog pDialog, success;

    String lokasiHrd, ket, link;

     ListViewAdapterStockUpdate adapter;

    List<sparepart_pojos> sparepart_pojosList = new ArrayList();

    SharedPreferences sharedPreferences;

    ImageButton filter;

    RelativeLayout totallayout, invalidlayout;

    MaterialCardView refresh, warning, refreshswipe;

    TextView tanggal;

    Button simpan;

    RequestQueue requestQueue32, requestQueue33, requestQueue34;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validasi_stockopname);
        HttpsTrustManager.allowAllSSL();
        AppToolBar = findViewById(R.id.AppToolBar);
        searchview = findViewById(R.id.searchview);
        refreshText = findViewById(R.id.textlokasi);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        list_stock = findViewById(R.id.list_stock);
        tanggal = findViewById(R.id.tanggal);
        refreshswipe = findViewById(R.id.refreshswipe);
        invalidkosongan = findViewById(R.id.invalidkosongan);

        card_layout_simpan = findViewById(R.id.card_layout_simpan);


        simpan = findViewById(R.id.simpan);

        totaloutstanding = findViewById(R.id.totaloutstanding);

        refresh = findViewById(R.id.refresh);
        warning = findViewById(R.id.warning);

        totallayout = findViewById(R.id.totallayout);
        invalidlayout = findViewById(R.id.invalidlayout);

        totalstock = findViewById(R.id.totalstock);
        total = findViewById(R.id.total);

        filter = findViewById(R.id.filter);

        list_stock.setDescendantFocusability(ListView.FOCUS_AFTER_DESCENDANTS);


        tanggal.setVisibility(View.GONE);

        simpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                simpan.setEnabled(false);
                String currentDateandTime2 = new SimpleDateFormat("HHmm").format(new Date());
                if(Integer.parseInt(currentDateandTime2) >= 1715){
                    new SweetAlertDialog(validasi_stockopname.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Maaf, waktu validasi maksimal sudah lewat")
                            .setConfirmText("OK")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.dismissWithAnimation();
                                    finish();
                                }
                            })
                            .show();
                } else if(adapter.getCount() == 0){
                    new SweetAlertDialog(validasi_stockopname.this, 1).setContentText("Data Masih Ada Yang Kosong").setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.dismissWithAnimation();
                        }
                    }).show();
                } else {
                    for (int e = 0; e <= sparepart_pojosList.size() - 1; e++) {
                        if (adapter.getItem(e).getStock_fisik() == null) {
                            new Handler().postDelayed(new Runnable() {
                                public void run() {
                                    simpandata();
                                }
                            }, 500);

                            searchview.getText().clear();
                            break;
                        } else if (adapter.getItem(e).getStock_fisik() != null) {
                            new Handler().postDelayed(new Runnable() {
                                public void run() {
                                    simpandata();
                                }
                            }, 500);
                            searchview.getText().clear();
                            break;
                        } else if (e == sparepart_pojosList.size() - 1) {
                            new SweetAlertDialog(validasi_stockopname.this, 1).setContentText("Data Masih Ada Yang Kosong").setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.dismissWithAnimation();
                                }
                            }).show();
                        }
                    }
                }
            }
        });

        String currentDateandTime2 = new SimpleDateFormat("HHmm").format(new Date());
        if(Integer.parseInt(currentDateandTime2) >= 1715){
            warning.setVisibility(View.VISIBLE);
            refresh.setVisibility(View.GONE);
            refreshswipe.setVisibility(View.GONE);
            filter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new SweetAlertDialog(validasi_stockopname.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Maaf, waktu validasi maksimal sudah lewat")
                            .setConfirmText("OK")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.dismissWithAnimation();
                                    finish();
                                }
                            })
                            .show();
                }
            });
        } else {
            warning.setVisibility(View.GONE);
            refresh.setVisibility(View.VISIBLE);
            refreshswipe.setVisibility(View.VISIBLE);

            filter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Dialog filter = new Dialog(validasi_stockopname.this);
                    filter.setContentView(R.layout.filtering);
                    filter.setCancelable(false);

                    String[] array={"Pusat", "Cirendeu", "Rancamaya", "Cikuda"};
                    ArrayAdapter<String> adapter;

                    AutoCompleteTextView searchview = filter.findViewById(R.id.searchview);

                    RadioGroup filtering_group = filter.findViewById(R.id.filtering_group);
                    RadioButton pass = filter.findViewById(R.id.pass);
                    RadioButton invalid = filter.findViewById(R.id.invalid);
                    RadioButton semua = filter.findViewById(R.id.semua);
                    RadioButton outstanding = filter.findViewById(R.id.outstanding);


                    Button batal = filter.findViewById(R.id.batal);
                    Button lihat = filter.findViewById(R.id.lanjutkan);

                    adapter = new ArrayAdapter<String>(validasi_stockopname.this,
                            android.R.layout.simple_list_item_1, array);

                    searchview.setAdapter(adapter);

                    batal.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            filter.dismiss();
                        }
                    });

                    lihat.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            searchview.setError(null);

                            if(searchview.getText().toString().length() == 0){
                                searchview.setError("Pilih Lokasi");
                            } else if (filtering_group.getCheckedRadioButtonId() == -1) {
                                new SweetAlertDialog(validasi_stockopname.this, SweetAlertDialog.ERROR_TYPE)
                                        .setContentText("Pilih Filter Terlebih Dahulu")
                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sDialog) {
                                                sDialog.dismissWithAnimation();
                                            }
                                        })
                                        .show();
                            } else {
                                if (searchview.getText().toString().equals("Pusat")) {
                                    lokasiHrd = "1,7,59";
                                    refreshText.setText("Lokasi Pusat");
                                } else if (searchview.getText().toString().equals("Cirendeu")) {
                                    lokasiHrd = "19,57";
                                    refreshText.setText("Lokasi Cirendeu");
                                } else if (searchview.getText().toString().equals("Rancamaya")) {
                                    lokasiHrd = "2,8";
                                    refreshText.setText("Lokasi Rancamaya");
                                } else if (searchview.getText().toString().equals("Cikuda")) {
                                    lokasiHrd = "3,9";
                                    refreshText.setText("Lokasi Cikuda");
                                }


                                filter.dismiss();
                                if(pass.isChecked()){
                                    link = "https://apisec.tvip.co.id/rest_server_stock_opname/Stock/index_Stock_after?warehouse=";
                                    ket = pass.getText().toString();
                                    getData(lokasiHrd, pass.getText().toString());
                                    totallayout.setVisibility(View.VISIBLE);
                                    invalidlayout.setVisibility(View.GONE);
                                    invalidkosongan.setVisibility(View.GONE);
                                    simpan.setVisibility(View.GONE);
                                } else if (invalid.isChecked()){
                                    link = "https://apisec.tvip.co.id/rest_server_stock_opname/Stock/index_Stock_after?warehouse=";
                                    ket = invalid.getText().toString();
                                    getData(lokasiHrd, invalid.getText().toString());
                                    totallayout.setVisibility(View.GONE);
                                    invalidlayout.setVisibility(View.VISIBLE);
                                    invalidkosongan.setVisibility(View.GONE);
                                    simpan.setVisibility(View.VISIBLE);
                                } else if (semua.isChecked()){
                                    link = "https://apisec.tvip.co.id/rest_server_stock_opname/Stock/index_Stock?warehouse=";
                                    ket = semua.getText().toString();
                                    getData(lokasiHrd, semua.getText().toString());
                                    totallayout.setVisibility(View.VISIBLE);
                                    invalidlayout.setVisibility(View.VISIBLE);
                                    invalidkosongan.setVisibility(View.GONE);
                                    simpan.setVisibility(View.VISIBLE);
                                } else {
                                    link = "https://apisec.tvip.co.id/rest_server_stock_opname/Stock/index_Kosongan?warehouse=";
                                    ket = outstanding.getText().toString();
                                    getData(lokasiHrd, semua.getText().toString());
                                    totallayout.setVisibility(View.GONE);
                                    invalidlayout.setVisibility(View.GONE);
                                    invalidkosongan.setVisibility(View.VISIBLE);
                                    simpan.setVisibility(View.GONE);
                                }
                            }
                        }
                    });

                    filter.show();
                }
            });
        }

        setSupportActionBar(AppToolBar);



        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                searchview.getText().clear();
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(swipeRefreshLayout.isRefreshing()){

                            if(lokasiHrd == null){
                                swipeRefreshLayout.setRefreshing(false);
                                new SweetAlertDialog(validasi_stockopname.this, SweetAlertDialog.WARNING_TYPE)
                                        .setTitleText("Pilih Lokasi Terlebih Dahulu")
                                        .setConfirmText("OK")
                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sDialog) {
                                                sDialog.dismissWithAnimation();
                                            }
                                        })
                                        .show();
                            } else {
                                swipeRefreshLayout.setRefreshing(false);
                                getData(lokasiHrd, ket);
                            }

                        }
                    }
                }, 3000);


            }
        });
    }

    private void simpandata() {
        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(this, 5);
        this.pDialog = sweetAlertDialog;
        sweetAlertDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        this.pDialog.setTitleText("Harap Menunggu");
        this.pDialog.setCancelable(false);
        this.pDialog.show();


        for (int i = 0; i <= this.sparepart_pojosList.size() - 1; i++) {
            int finalI = i;
            System.out.println(adapter.getItem(i).getStock_fisik() + " And Number is " + adapter.getItem(i).getStock_system());
            if(adapter.getItem(finalI).getStock_fisik() == null){
                if (finalI == this.sparepart_pojosList.size() - 1) {

                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            success = new SweetAlertDialog(validasi_stockopname.this, 2);
                            success.setContentText("Data Berhasil Disimpan");
                            success.setCancelable(false);
                            success.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.dismissWithAnimation();
                                    pDialog.dismissWithAnimation();
                                    searchview.getText().clear();
                                    getData(lokasiHrd, ket);
                                    simpan.setEnabled(true);
                                }
                            });
                            success.show();
                        }
                    }, 7000);
                }

            } else if(!adapter.getItem(finalI).getStock_system().equals(adapter.getItem(finalI).getStock_fisik())){
                StringRequest r1 = new StringRequest(2, "https://apisec.tvip.co.id/rest_server_stock_opname/Stock/index_stock", new Response.Listener<String>() {
                    public void onResponse(String response) {
                        postHistory(adapter.getItem(finalI).getTgl_closing(), adapter.getItem(finalI).getOrder_id(), adapter.getItem(finalI).getStok_awal(), adapter.getItem(finalI).getStok_akhir(), adapter.getItem(finalI).getWarehouse(), adapter.getItem(finalI).getStock_system());
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

                        params.put("tgl_closing", adapter.getItem(finalI).getTgl_closing());
                        params.put("warehouse", adapter.getItem(finalI).getWarehouse());
                        params.put("order_id", adapter.getItem(finalI).getOrder_id());
                        params.put("stok_fisik", adapter.getItem(finalI).getStock_fisik());
                        params.put("last_update", currentDateandTime2);
                        params.put("nik_update", nik_baru);

                        System.out.println("Params index = " + params);

                        return params;

                    }
                };
                if (requestQueue32 == null) {
                    requestQueue32 = Volley.newRequestQueue(validasi_stockopname.this);
                    requestQueue32.add(r1);
                } else {
                    requestQueue32.add(r1);
                }
            } else {
                if (finalI == this.sparepart_pojosList.size() - 1) {

                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            success = new SweetAlertDialog(validasi_stockopname.this, 2);
                            success.setContentText("Data Berhasil Disimpan");
                            success.setCancelable(false);
                            success.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.dismissWithAnimation();
                                    pDialog.dismissWithAnimation();
                                    searchview.getText().clear();
                                    getData(lokasiHrd, ket);
                                    simpan.setEnabled(true);
                                }
                            });
                            success.show();
                        }
                    }, 7000);
                }
            }

        }

    }

    private void getData(String location, String kondisi) {
        totalstock.setText("0 Item");
        total.setText("0 Item");
        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(this, 5);
        this.pDialog = sweetAlertDialog;
        sweetAlertDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        this.pDialog.setTitleText("Harap Menunggu");
        this.pDialog.setCancelable(false);
        this.pDialog.show();


        list_stock.setAdapter(null);
        adapter = new ListViewAdapterStockUpdate(this.sparepart_pojosList, getApplicationContext());
        sparepart_pojosList.clear();
        adapter.clear();

        StringRequest r1 = new StringRequest(0, link + location, new Response.Listener<String>() {
            public void onResponse(String response) {
                int valid = 0;
                int invalid = 0;
                int diisi = 0;
                pDialog.dismissWithAnimation();
                swipeRefreshLayout.setRefreshing(false);

                tanggal.setVisibility(View.VISIBLE);
                tanggal.setText("Update • " + new SimpleDateFormat("dd MMMM yyyy").format(new Date()));

                try {
                    JSONObject obj = new JSONObject(response);
                    if (obj.getString(NotificationCompat.CATEGORY_STATUS).equals("true")) {
                        JSONArray movieArray = new JSONObject(response).getJSONArray("data");
                        for (int i = 0; i < movieArray.length(); i++) {
                            JSONObject movieObject = movieArray.getJSONObject(i);



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
                                    movieObject.getString("stok_fisik"),
                                    movieObject.getString("condition"),
                                    movieObject.getString("stok_fisik"));
                            sparepart_pojosList.add(movieItem);
                            list_stock.setAdapter(adapter);
                            adapter.notifyDataSetChanged();

                            if(!movieObject.getString("condition").equals("None")){
                                diisi++;
                                System.out.println("Status Kondisi = " + movieObject.getString("condition"));
                            }

                            if (movieObject.getString("condition").equals("Pass")){
                                valid++;
                            }

                            if (movieObject.getString("condition").equals("Invalid")){
                                invalid++;
                                total.setText(String.valueOf(invalid) + " Item");
                            }

                            if(kondisi.equals("Pass")){
                                if (!movieObject.getString("stok_fisik").equals(movieObject.getString("stok_akhir"))){
                                    sparepart_pojosList.remove(movieItem);
                                    adapter.notifyDataSetChanged();
                                }
                            } else if(kondisi.equals("Invalid")){
                                if (movieObject.getString("stok_fisik").equals(movieObject.getString("stok_akhir"))){
                                    sparepart_pojosList.remove(movieItem);
                                    adapter.notifyDataSetChanged();
                                }
                            } else {

                            }

                            if(ket.equals("OutStanding")){
                                totaloutstanding.setText(String.valueOf(movieArray.length()) + " Item");
                            } else {

                            }

                            if(!movieObject.getString("stok_fisik").equals("null")){

                            }

                            if(kondisi.equals("semua")){
                                totalstock.setText(String.valueOf(valid) + "/" + String.valueOf(movieArray.length()) + " Item");
                            } else if(kondisi.equals("Pass")){
                                totalstock.setText(String.valueOf(valid) + " Item");
                            } else {
                                totalstock.setText(String.valueOf(valid) + "/" + String.valueOf(movieArray.length()) + " Item");
                            }

                            searchview.addTextChangedListener(new TextWatcher() {
                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                }

                                public void onTextChanged(CharSequence s, int start, int before, int count) {

                                }

                                public void afterTextChanged(Editable s) {
                                    adapter.getFilter().filter(s.toString().toLowerCase());
                                }
                            });


                        }


                    } else {
                        tanggal.setVisibility(View.GONE);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                tanggal.setVisibility(View.GONE);
                searchview.getText().clear();
                pDialog.dismissWithAnimation();
                Toast.makeText(getApplicationContext(), String.valueOf(error), Toast.LENGTH_SHORT).show();

                totalstock.setText("0 Item");
                total.setText("0 Item");
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

    public class ListViewAdapterStockUpdate extends ArrayAdapter<sparepart_pojos> implements Filterable {
        private Context context;
        private ItemFilter mFilter = new ItemFilter();
        List<sparepart_pojos> sparepart_pojosList;
        List<sparepart_pojos> sparepart_pojosList_filtered;

        private class ViewHolder {
            EditText Updatestock_fisik;
            MaterialCardView addUpdate;
            TextView akhir;
            TextView awal;
            MaterialCardView cardview;
            int count;
            TextView fisik;

            /* renamed from: in */
            TextView in;
            RelativeLayout layout_fisik;
            MaterialCardView minusUpdate;
            TextView nama_produk;
            TextView orderid;
            TextView out;
            Button simpan;
            Chip status;
            RelativeLayout updateStockFisik;

            TextView ids;

            private ViewHolder() {
            }
        }

        public ListViewAdapterStockUpdate(List<sparepart_pojos> sparepart_pojosList2, Context context2) {
            super(context2, R.layout.listitem_stock_spv, sparepart_pojosList2);
            this.sparepart_pojosList = sparepart_pojosList2;
            this.sparepart_pojosList_filtered = sparepart_pojosList2;
            this.context = context2;
        }

        private class ItemFilter extends Filter {
            private ItemFilter() {
            }

            /* access modifiers changed from: protected */
            public Filter.FilterResults performFiltering(CharSequence charSequence) {
                String filterString = charSequence.toString().toLowerCase();
                Filter.FilterResults results = new Filter.FilterResults();
                List<sparepart_pojos> list = sparepart_pojosList_filtered;
                int count = list.size();
                List<sparepart_pojos> newList = new ArrayList<>(count);
                for (int i = 0; i < count; i++) {
                    sparepart_pojos a = list.get(i);
                    if (String.valueOf(a.getOrder_name()).toLowerCase().contains(filterString) || String.valueOf(a.getOrder_id()).toLowerCase().contains(filterString)) {
                        newList.add(a);
                    }
                }
                results.values = newList;
                results.count = newList.size();
                return results;
            }

            /* access modifiers changed from: protected */
            public void publishResults(CharSequence charSequence, Filter.FilterResults filterResults) {
                sparepart_pojosList = (List) filterResults.values;
                adapter.notifyDataSetChanged();


            }
        }

        public Filter getFilter() {
            return this.mFilter;
        }

        public int getCount() {
            return this.sparepart_pojosList.size();
        }

        public sparepart_pojos getItem(int position) {
            return sparepart_pojosList.get(position); // Line 54.
        }

        public int getViewTypeCount() {
            int count;
            if(sparepart_pojosList.size() >0){
                count = getCount();
            } else {
                count = 1;
            }
            return count;
        }

        public int getItemViewType(int position) {
            return position;
        }

        public long getItemId(int position) {
            return 0;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            View convertView2;
            final ViewHolder viewHolder;
            final sparepart_pojos movieItem = getItem(position);
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView2 = LayoutInflater.from(getContext()).inflate(R.layout.listitem_stock_spv, parent, false);
                viewHolder.nama_produk = (TextView) convertView2.findViewById(R.id.nama_produk);
                viewHolder.in = (TextView) convertView2.findViewById(R.id.in);
                viewHolder.out = (TextView) convertView2.findViewById(R.id.out);
                viewHolder.awal = (TextView) convertView2.findViewById(R.id.awal);
                viewHolder.akhir = (TextView) convertView2.findViewById(R.id.akhir);
                viewHolder.fisik = (TextView) convertView2.findViewById(R.id.fisik);
                viewHolder.orderid = (TextView) convertView2.findViewById(R.id.orderid);
                viewHolder.updateStockFisik = (RelativeLayout) convertView2.findViewById(R.id.updateStockFisik);
                viewHolder.simpan = (Button) convertView2.findViewById(R.id.simpan);
                viewHolder.minusUpdate = (MaterialCardView) convertView2.findViewById(R.id.minusUpdate);
                viewHolder.addUpdate = (MaterialCardView) convertView2.findViewById(R.id.addUpdate);
                viewHolder.Updatestock_fisik = (EditText) convertView2.findViewById(R.id.Updatestock_fisik);
                viewHolder.status = (Chip) convertView2.findViewById(R.id.status);
                viewHolder.cardview = (MaterialCardView) convertView2.findViewById(R.id.cardview);
                viewHolder.layout_fisik = (RelativeLayout) convertView2.findViewById(R.id.layout_fisik);
                viewHolder.ids = (TextView) convertView2.findViewById(R.id.ids);


                convertView2.setTag(viewHolder);
            } else {
                ViewGroup viewGroup = parent;
                viewHolder = (ViewHolder) convertView.getTag();
                convertView2 = convertView;
            }

            viewHolder.nama_produk.setTag(getItem(position).getId());
            viewHolder.in.setTag(getItem(position).getId());
            viewHolder.out.setTag(getItem(position).getId());
            viewHolder.awal.setTag(getItem(position).getId());
            viewHolder.akhir.setTag(getItem(position).getId());
            viewHolder.fisik.setTag(getItem(position).getId());
            viewHolder.orderid.setTag(getItem(position).getId());
            viewHolder.ids.setTag(getItem(position).getId());


            viewHolder.updateStockFisik.setTag(getItem(position).getId());
            viewHolder.simpan.setTag(getItem(position).getId());
            viewHolder.minusUpdate.setTag(getItem(position).getId());
            viewHolder.addUpdate.setTag(getItem(position).getId());
            viewHolder.Updatestock_fisik.setTag(getItem(position).getId());
            viewHolder.status.setTag(getItem(position).getId());
            viewHolder.cardview.setTag(getItem(position).getId());
            viewHolder.layout_fisik.setTag(getItem(position).getId());

            viewHolder.Updatestock_fisik.setText(movieItem.getStock_fisik());
            viewHolder.nama_produk.setText(movieItem.getOrder_name());
            viewHolder.in.setText(movieItem.getIn() + " Pcs");
            viewHolder.out.setText(movieItem.getOut() + " Pcs");
            viewHolder.awal.setText(movieItem.getStok_awal() + " Pcs");
            viewHolder.akhir.setText(movieItem.getStok_akhir() + " Pcs");

            viewHolder.orderid.setText(movieItem.getOrder_id());
            viewHolder.status.setVisibility(View.VISIBLE);

            viewHolder.ids.setText(String.valueOf(movieItem.getId()));

            if(movieItem.getStock_fisik().equals("null")){
                viewHolder.count = 0;

            } else {
                viewHolder.count = Integer.parseInt(movieItem.getStock_fisik());

            }

            viewHolder.Updatestock_fisik.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if (keyboardShown(viewHolder.Updatestock_fisik.getRootView())) {
                        card_layout_simpan.setVisibility(View.GONE);
                    } else {
                        card_layout_simpan.setVisibility(View.VISIBLE);
                    }
                }
            });

            if(movieItem.getCondition().equals("Pass")){
                viewHolder.layout_fisik.setVisibility(View.VISIBLE);
                viewHolder.updateStockFisik.setVisibility(View.GONE);
                viewHolder.fisik.setText(movieItem.getStock_fisik() + " Pcs");
                viewHolder.status.setText("Pass");
                viewHolder.status.setChipStrokeColor(ColorStateList.valueOf(Color.parseColor("#B9EED0")));
                viewHolder.status.setChipBackgroundColor(ColorStateList.valueOf(Color.parseColor("#E8FFF2")));
                viewHolder.status.setTextColor(Color.parseColor("#2ECC71"));
            } else if(movieItem.getCondition().equals("Invalid")){
                viewHolder.layout_fisik.setVisibility(View.GONE);
                viewHolder.updateStockFisik.setVisibility(View.VISIBLE);
                viewHolder.status.setText("Invalid");
                viewHolder.status.setChipStrokeColor(ColorStateList.valueOf(Color.parseColor("#FEC0C0")));
                viewHolder.status.setChipBackgroundColor(ColorStateList.valueOf(Color.parseColor("#FFF1F1")));
                viewHolder.status.setTextColor(Color.parseColor("#FB4141"));
            } else {
                viewHolder.updateStockFisik.setVisibility(View.GONE);
                viewHolder.status.setVisibility(View.GONE);
            }

            viewHolder.addUpdate.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                 if(viewHolder.Updatestock_fisik.getText().toString().length() > 0 || movieItem.getId() == Integer.parseInt(viewHolder.ids.getText().toString())){
                     viewHolder.count++;
                     getItem(position).setStock_fisik(String.valueOf(viewHolder.count));
                     viewHolder.Updatestock_fisik.setText(movieItem.getStock_fisik());
                     adapter.notifyDataSetChanged();
                 }


                }
            });
            viewHolder.minusUpdate.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if(viewHolder.Updatestock_fisik.getText().toString().length() > 0 || movieItem.getId() == Integer.parseInt(viewHolder.ids.getText().toString())) {
                        if (viewHolder.count != 0) {
                            viewHolder.count--;
                            getItem(position).setStock_fisik(String.valueOf(viewHolder.count));
                            viewHolder.Updatestock_fisik.setText(movieItem.getStock_fisik());
                            adapter.notifyDataSetChanged();
                        }
                    }
                }
            });

            if(ket.equals("OutStanding")){

            } else {
                viewHolder.Updatestock_fisik.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if(viewHolder.Updatestock_fisik.getText().toString().length() == 0 || viewHolder.Updatestock_fisik.getText().toString().equals("null")){

                        } else if(viewHolder.Updatestock_fisik.getText().toString().length() > 0 || movieItem.getId() == Integer.parseInt(viewHolder.ids.getText().toString())){
                            viewHolder.count = Integer.parseInt(viewHolder.Updatestock_fisik.getText().toString());
                            getItem(position).setStock_fisik(String.valueOf(viewHolder.count));
                            viewHolder.Updatestock_fisik.setSelection(viewHolder.Updatestock_fisik.getText().length());
                            System.out.println("Data yang berubah = " + movieItem.getStock_fisik() + " Di posisi = " + getItem(position).getId());
                        }
                    }
                });
//                viewHolder.Updatestock_fisik.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//                    @Override
//                    public void onFocusChange(View v, boolean hasFocus) {
//                        if(!hasFocus){
//
//                        }
//                    }
//                });
            }

            viewHolder.simpan.setVisibility(View.GONE);


            viewHolder.simpan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (viewHolder.Updatestock_fisik.getText().toString().length() == 0) {
                        viewHolder.Updatestock_fisik.setError("Data Masih Kosong");
                    } else {
                        updateData(movieItem.getTgl_closing(), movieItem.getOrder_id(), viewHolder.Updatestock_fisik.getText().toString(), movieItem.getStok_awal(), movieItem.getStok_akhir(), movieItem.getWarehouse());
                    }
                }
            });

            return convertView2;
        }

        /* access modifiers changed from: private */
        public void updateData(String tgl_closing, String order_id, String stock_fisik, String stok_awal, String stok_akhir, String warehouse) {
            pDialog = new SweetAlertDialog(validasi_stockopname.this, 5);
            pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            pDialog.setTitleText("Harap Menunggu");
            pDialog.setCancelable(false);
            pDialog.show();

            StringRequest r1 = new StringRequest(2, "https://apisec.tvip.co.id/rest_server_stock_opname/Stock/index_stock", new Response.Listener<String>() {
                public void onResponse(String response) {
                    postHistory(tgl_closing, order_id, stok_awal, stok_akhir, warehouse, stock_fisik);
                }
            }, new Response.ErrorListener() {
                public void onErrorResponse(VolleyError error) {
                    pDialog.dismissWithAnimation();
                    success = new SweetAlertDialog(validasi_stockopname.this, 2);
                    success.setContentText("Data Berhasil Disimpan");
                    success.setCancelable(false);
                    success.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.dismissWithAnimation();
                        }
                    });
                    success.show();
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
                    params.put("warehouse", warehouse);
                    params.put("order_id", order_id);
                    params.put("stok_fisik", stock_fisik);
                    params.put("last_update", currentDateandTime2);
                    params.put("nik_update", nik_baru);

                    System.out.println("Params index = " + params);

                    return params;

                }
            };
            r1.setRetryPolicy(new DefaultRetryPolicy(5000, 1, 1.0f));
            RequestQueue requestQueue2 = Volley.newRequestQueue(validasi_stockopname.this);
            requestQueue2.getCache().clear();
            requestQueue2.add(r1);
        }
    }

    private void postHistory(String tgl_closing, String order_id, String stok_awal, String stok_akhir, String warehouse, String stock_fisik) {
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
                sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
                String nik_baru = sharedPreferences.getString("nik_baru", null);

                params.put("tgl_closing", tgl_closing);
                params.put("warehouse", warehouse);
                params.put("order_id", order_id);
                params.put("stok_awal", stok_awal);
                params.put("stok_akhir", stok_akhir);
                params.put("stok_fisik", stock_fisik);
                params.put("nik_update", nik_baru);
                System.out.println("Params = " + params);
                return params;
            }
        };
        if (requestQueue33 == null) {
            requestQueue33 = Volley.newRequestQueue(validasi_stockopname.this);
            requestQueue33.add(stringRequest2);
        } else {
            requestQueue33.add(stringRequest2);
        }
    }

    private boolean keyboardShown(View rootView) {

        final int softKeyboardHeight = 100;
        Rect r = new Rect();
        rootView.getWindowVisibleDisplayFrame(r);
        DisplayMetrics dm = rootView.getResources().getDisplayMetrics();
        int heightDiff = rootView.getBottom() - r.bottom;
        return heightDiff > softKeyboardHeight * dm.density;
    }

}