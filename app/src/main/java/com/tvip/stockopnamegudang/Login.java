package com.tvip.stockopnamegudang;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class Login extends AppCompatActivity {
    private static Context ctx;
    Button btnlogin;
    EditText editPassword;
    EditText editText;
    EditText editUsername;
    SweetAlertDialog pDialog;
    ProgressBar progressBar;
    SharedPreferences sharedPreferences;
    SweetAlertDialog success;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        this.progressBar = (ProgressBar) findViewById(R.id.progressBar);
        HttpsTrustManager.allowAllSSL();
        ((TextView) findViewById(R.id.version)).setText("1.0.2 Version");
        final EditText editText2 = (EditText) findViewById(R.id.editpassword);
        editText2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    editText2.setHint("");
                } else {
                    editText2.setHint("Your hint");
                }
            }
        });
        this.sharedPreferences = getSharedPreferences("user_details", 0);
        this.progressBar = (ProgressBar) findViewById(R.id.progressBar);
        this.editUsername = (EditText) findViewById(R.id.editusername);
        this.editPassword = (EditText) findViewById(R.id.editpassword);
        findViewById(R.id.loginbutton).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                userLogin();
            }

            private void userLogin() {
                String username = Login.this.editUsername.getText().toString();
                String password = Login.this.editPassword.getText().toString();
                if (TextUtils.isEmpty(username)) {
                    Login.this.editUsername.setError("Please enter your NIK");
                    Login.this.editUsername.requestFocus();
                } else if (TextUtils.isEmpty(password)) {
                    Login.this.editPassword.setError("Please enter your Password");
                    Login.this.editPassword.requestFocus();
                } else {
                    pDialog = new SweetAlertDialog(Login.this, 5);
                    pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                    pDialog.setTitleText("Loading");
                    pDialog.setCancelable(false);
                    pDialog.show();
                    StringRequest r3 = new StringRequest(0, "https://hrd.tvip.co.id/rest_server/api/login/index?nik_baru=" + editUsername.getText().toString(), new Response.Listener<String>() {
                        public void onResponse(String response) {
                            try {
                                JSONObject obj = new JSONObject(response);
                                if (obj.getString(NotificationCompat.CATEGORY_STATUS).equals("true")) {
                                    JSONArray movieArray = obj.getJSONArray("data");
                                    int i = 0;
                                    while (i < movieArray.length()) {
                                        JSONObject movieObject = movieArray.getJSONObject(i);
                                        pDialog.cancel();
                                        if (movieObject.getString("password").equals(md5(editPassword.getText().toString()))) {
                                            final String lokasi = movieObject.getString("lokasi_struktur");
                                            final String jabatan = movieObject.getString("jabatan_struktur");
                                            final String level = movieObject.getString("level_jabatan_karyawan");
                                            final String nama = movieObject.getString("nama_karyawan_struktur");
                                            success = new SweetAlertDialog(Login.this, 2);
                                            success.setContentText("Selamat Datang");
                                            success.setCancelable(false);
                                            success.setConfirmText("OK");
                                            SweetAlertDialog sweetAlertDialog = success;
                                            final JSONObject jSONObject = movieObject;


                                            pDialog.dismissWithAnimation();
                                            success = new SweetAlertDialog(Login.this, 2);
                                            success.setContentText("Anda Berhasil Login");
                                            success.setCancelable(false);
                                            success.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                public void onClick(SweetAlertDialog sDialog) {
                                                    Intent intent = new Intent(Login.this, menu_stockopname.class);
                                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                                    editor.putString("nik_baru", editUsername.getText().toString());
                                                    try {
                                                        editor.putString("szDocCall", jSONObject.getString("szId"));
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                    editor.putString("lokasi", lokasi);
                                                    editor.putString("jabatan", jabatan);
                                                    editor.putString("level", level);
                                                    editor.putString("nama", nama);
                                                    editor.apply();
                                                    sDialog.dismissWithAnimation();
                                                    startActivity(intent);
                                                }
                                            });
                                            success.show();


                                        } else if (!movieObject.getString("password").equals(md5(editPassword.getText().toString()))) {
                                            new SweetAlertDialog(Login.this, 1).setContentText("Oops... NIK / Password Salah").setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                public void onClick(SweetAlertDialog sDialog) {
                                                    sDialog.dismissWithAnimation();
                                                }
                                            }).show();
                                        }
                                        i++;
                                        String str = response;
                                    }
                                    return;
                                }
                                new SweetAlertDialog(Login.this, 1).setContentText("Oops... NIK / Password Salah").setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    public void onClick(SweetAlertDialog sDialog) {
                                        sDialog.dismissWithAnimation();
                                    }
                                }).show();
                                pDialog.cancel();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        public void onErrorResponse(VolleyError error) {
                            pDialog.cancel();
                            if (error instanceof ServerError) {
                                new SweetAlertDialog(Login.this, 1).setContentText("Nik anda salah!").show();
                            } else {
                                new SweetAlertDialog(Login.this, 1).setContentText("Jaringan sedang bermasalah!").show();
                            }
                        }
                    }) {
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            HashMap<String, String> params = new HashMap<>();
                            params.put("Authorization", "Basic " + Base64.encodeToString(String.format("%s:%s", new Object[]{"admin", "Databa53"}).getBytes(), 0));
                            return params;
                        }
                    };
                    r3.setRetryPolicy(new DefaultRetryPolicy(500000, 1, 1.0f));
                    Volley.newRequestQueue(Login.this).add(r3);
                }
            }
        });
    }

    public String md5(String s) {
        try {
            byte[] hash = MessageDigest.getInstance("MD5").digest(s.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(hash.length * 2);
            int length = hash.length;
            for (int i = 0; i < length; i++) {
                sb.append(String.format("%02x", new Object[]{Integer.valueOf(hash[i] & 255)}));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, (String) null, ex);
            return null;
        }
    }

    public void onBackPressed() {
        finishAffinity();
    }
}
