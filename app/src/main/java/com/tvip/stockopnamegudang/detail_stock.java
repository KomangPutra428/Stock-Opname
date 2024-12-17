package com.tvip.stockopnamegudang;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.tvip.stockopnamegudang.pojos.sparepart_pojos;
import java.text.ParseException;
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

public class detail_stock extends AppCompatActivity {
    ListViewAdapterStockOpnameDetail adapter;
    TextView id_produk;
    ListView list_stock_detail;
    TextView nama_produk;
    SweetAlertDialog pDialog;
    List<sparepart_pojos> sparepart_pojosList = new ArrayList();

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_stock);
        this.id_produk = (TextView) findViewById(R.id.id_produk);
        this.nama_produk = (TextView) findViewById(R.id.nama_produk);
        this.list_stock_detail = (ListView) findViewById(R.id.list_stock_detail);
        this.id_produk.setText(getIntent().getStringExtra("id"));
        this.nama_produk.setText(getIntent().getStringExtra("name"));
        getData();
    }

    private void getData() {
        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(this, 5);
        this.pDialog = sweetAlertDialog;
        sweetAlertDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        this.pDialog.setTitleText("Harap Menunggu");
        this.pDialog.setCancelable(false);
        this.pDialog.show();
        ListViewAdapterStockOpnameDetail listViewAdapterStockOpnameDetail = new ListViewAdapterStockOpnameDetail(this.sparepart_pojosList, getApplicationContext());
        this.adapter = listViewAdapterStockOpnameDetail;
        listViewAdapterStockOpnameDetail.clear();
        System.out.println("Link = " + "https://apisec.tvip.co.id/rest_server_stock_opname/Stock/index_Rekap?order_id=" + getIntent().getStringExtra("id") + "&warehouse=" + getIntent().getStringExtra("lokasi"));
        StringRequest r3 = new StringRequest(0, "https://apisec.tvip.co.id/rest_server_stock_opname/Stock/index_Rekap?order_id=" + getIntent().getStringExtra("id") + "&warehouse=" + getIntent().getStringExtra("lokasi"), new Response.Listener<String>() {
            public void onResponse(String response) {
                detail_stock.this.pDialog.dismissWithAnimation();
                try {
                    try {
                        JSONArray movieArray = new JSONObject(response).getJSONArray("data");
                        for (int i = 0; i < movieArray.length(); i++) {
                            JSONObject movieObject = movieArray.getJSONObject(i);
                            detail_stock.this.sparepart_pojosList.add(new sparepart_pojos(
                                    i,
                                    movieObject.getString("tgl_closing"),
                                    "",
                                    "",
                                    movieObject.getString("order_id"),
                                    "",
                                    movieObject.getString("stok_awal"),
                                    movieObject.getString("in"),
                                    movieObject.getString("out"),
                                    movieObject.getString("stok_akhir"),
                                    movieObject.getString("stok_fisik"),
                                    "",
                                    ""));
                            detail_stock.this.list_stock_detail.setAdapter(detail_stock.this.adapter);
                            detail_stock.this.adapter.notifyDataSetChanged();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                detail_stock.this.pDialog.dismissWithAnimation();
            }
        }) {
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("Authorization", "Basic " + Base64.encodeToString(String.format("%s:%s", new Object[]{"admin", "Databa53"}).getBytes(), 0));
                return params;
            }
        };
        r3.setRetryPolicy(new DefaultRetryPolicy(500000, 1, 1.0f));
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.getCache().clear();
        requestQueue.add(r3);
    }

    public class ListViewAdapterStockOpnameDetail extends ArrayAdapter<sparepart_pojos> {
        private Context context;
        List<sparepart_pojos> sparepart_pojoss;

        private class ViewHolder {
            TextView akhir;
            TextView awal;
            TextView date;

            /* renamed from: in */
            TextView in;
            TextView out;
            TextView stock_fisik;

            private ViewHolder() {
            }
        }

        public ListViewAdapterStockOpnameDetail(List<sparepart_pojos> sparepart_pojoss2, Context context2) {
            super(context2, R.layout.listitem_stock_detail, sparepart_pojoss2);
            this.sparepart_pojoss = sparepart_pojoss2;
            this.context = context2;
        }

        public int getCount() {
            return this.sparepart_pojoss.size();
        }

        public sparepart_pojos getItem(int position) {
            return this.sparepart_pojoss.get(position);
        }

        public int getViewTypeCount() {
            return getCount();
        }

        public int getItemViewType(int position) {
            return position;
        }

        public long getItemId(int position) {
            return 0;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            sparepart_pojos movieItem = this.sparepart_pojoss.get(position);
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.listitem_stock_detail, parent, false);
                viewHolder.in = (TextView) convertView.findViewById(R.id.in);
                viewHolder.out = (TextView) convertView.findViewById(R.id.out);
                viewHolder.awal = (TextView) convertView.findViewById(R.id.awal);
                viewHolder.akhir = (TextView) convertView.findViewById(R.id.akhir);
                viewHolder.stock_fisik = (TextView) convertView.findViewById(R.id.stock_fisik);
                viewHolder.date = (TextView) convertView.findViewById(R.id.date);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            if (position == 0) {
                viewHolder.date.setText("Today • " + detail_stock.convertFormat(movieItem.getTgl_closing()));
            } else {
                viewHolder.date.setText("Last Update • " + detail_stock.convertFormat(movieItem.getTgl_closing()));
            }
            viewHolder.in.setText(movieItem.getIn() + " Pcs");
            viewHolder.out.setText(movieItem.getOut() + " Pcs");
            viewHolder.awal.setText(movieItem.getStok_awal() + " Pcs");
            viewHolder.akhir.setText(movieItem.getStok_akhir() + " Pcs");
            viewHolder.stock_fisik.setText(movieItem.getStock_fisik() + " Pcs");
            return convertView;
        }
    }

    public static String convertFormat(String inputDate) {
        Date date = null;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(inputDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (date == null) {
            return "";
        }
        return new SimpleDateFormat("HH:mm • dd/MM/yy").format(date);
    }
}
