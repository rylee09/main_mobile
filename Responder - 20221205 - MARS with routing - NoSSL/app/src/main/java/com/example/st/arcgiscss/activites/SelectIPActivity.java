package com.example.st.arcgiscss.activites;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import com.example.st.arcgiscss.R;
import com.example.st.arcgiscss.constant.MyApplication;
import com.example.st.arcgiscss.dao.NewIncidentDBHelper;
import com.example.st.arcgiscss.model.NewIncident;
import com.example.st.arcgiscss.util.CacheUtils;
import com.example.st.arcgiscss.util.RetrofitUtils;
import com.example.st.arcgiscss.util.ToastUtil;
import com.example.st.arcgiscss.util.TypefaceUtil;
import com.google.gson.JsonObject;
import com.jaredrummler.materialspinner.MaterialSpinner;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by daxue on 2018/1/2.
 */

public class SelectIPActivity extends Activity implements View.OnClickListener {
    private static String ip;
    private List<String> ipList;
    private EditText et_ip;
    //    private ImageView iv_ip;
    private Button butt_confirmip;
    private MaterialSpinner ms_typefonts;
    private String selectMap, typeFont;

    //ZN - 20220208 check for valid IMEI
    private int int_imei_result;
    private String str_imei = "";
    private static final int READ_PHONE_STATE = 100;
    private RadioButton sw1, sw2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_ip);
        et_ip = findViewById(R.id.et_ip);
        butt_confirmip = findViewById(R.id.butt_confirmip);
        ms_typefonts = findViewById(R.id.ms_typefonts);
        initListner();
        initData();

        sw1 = findViewById(R.id.httpsSwitch);
        sw2 = findViewById(R.id.httpsSwitch);

//        //ZN - 20220208 check for valid IMEI - permission to read imei
//        if (Build.VERSION.SDK_INT >= 26) {
//            showContacts();
//        }

    }

    public void onSwitchClick(View view){
        if(sw1.isChecked()){
            Toast.makeText(getApplicationContext(), "on", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getApplicationContext(), "off", Toast.LENGTH_SHORT).show();

        }
    }

    private void initListner() {
        butt_confirmip.setOnClickListener(this);

    }

    public void initData() {

        String ip = CacheUtils.getIP(getApplication());

        //ZN - 20220710 put in default string
        if (ip == null || ip.equalsIgnoreCase( ""));
        ip = "192.168.1.2";

        et_ip.setText(ip);


        List<String> typeFonts = new ArrayList<>();
        typeFonts.add("tw_cen_mt");
        typeFonts.add("Wesley");
        typeFont = "tw_cen_mt";


        ms_typefonts.setPadding(10, 0, 10, 0);
        ms_typefonts.setItems(typeFonts);
        ms_typefonts.setDropdownMaxHeight(550);
        ms_typefonts.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                typeFont = typeFonts.get(position);
            }
        });

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.butt_confirmip:
                String ip = et_ip.getText().toString().trim();
                boolean b = checkIP(ip);
                if (b) {

//                    //ZN - 20220208 check for valid IMEI
//                    int int_imei_result = checkIMEI();
//                    try {
//                        Thread.sleep(1000);
//                    } catch (Exception e) {
//
//                    }
//                    if (int_imei_result == 1) {
//                        Log.i("IMEI", "checkIMEI passed");
//                        //if pass, continue with login
//                        CacheUtils.saveIp(getApplication(), ip);
//                        CacheUtils.saveTypeFonts(SelectIPActivity.this, typeFont);
//
//                        String s_typeFont = "fonts/" + typeFont + ".ttf";
//                        TypefaceUtil.replaceSystemDefaultFont(SelectIPActivity.this, s_typeFont);
//                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
//                        intent.putExtra("IS_IMEI_PASSED",true);
//                        startActivity(intent);
//                        finish();
//                        Toast.makeText(this, "ip have set to:" + ip, Toast.LENGTH_LONG).show();
//                    } else if (int_imei_result == -1){
//                        //if fail, return false for LoginActivity to close app
//                        Log.i("IMEI", "checkIMEI failed");
//                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
//                        intent.putExtra("IS_IMEI_PASSED",false);
//                        startActivity(intent);
//                        finish();
//                    } else {
//                        //no valid ip, hence do nothing and repeat steps
//                    }

                    CacheUtils.saveIp(getApplication(), ip);
                    CacheUtils.saveTypeFonts(SelectIPActivity.this, typeFont);

                    String s_typeFont = "fonts/" + typeFont + ".ttf";
                    TypefaceUtil.replaceSystemDefaultFont(SelectIPActivity.this, s_typeFont);
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    intent.putExtra("IS_IMEI_PASSED",true);
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                    finish();
                    Toast.makeText(this, "ip have set to:" + ip, Toast.LENGTH_LONG).show();

                }
                break;
        }
    }


    private void selectTypeDiag(final List<String> list, final EditText v) {
        int len = list.size();
        final String items[] = new String[len];
        for (int i = 0; i < list.size(); i++) {
            items[i] = list.get(i);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Please Select");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                v.setText(items[which]);
            }
        });
        builder.create();
        builder.create().show();
    }


    public boolean checkIP(String newIp) {


        String string = newIp;

//        String ip = "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\."
//                + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
//                + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
//                + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$";
//        Pattern p = Pattern.compile(ip);
//        Matcher m = p.matcher(string);
//        boolean b = m.matches();
        if (string.equals("")) {
            Toast.makeText(this, "IP address can't be empty", Toast.LENGTH_LONG).show();
            return false;
        }
//        else {
//            if (b == false) {
//                Toast.makeText(this, "IP format input error", Toast.LENGTH_LONG).show();
//                return false;
//            }
//        }
        return true;
    }

    //ZN - 20220208 check for valid IMEI
    public int checkIMEI() {
        Map<String, String> params = new HashMap<>();
//        params.put("imei", str_imei);
        params.put("imei", MyApplication.getInstance().getImei());

        Call<JsonObject> call = RetrofitUtils.getInstance().getMatchingIMEI(params);
        call.enqueue(new Callback<JsonObject>() {

            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response.body().toString());
                    if (jsonObject.getInt("resp_code") == 1) {
                        //IMEI matched
                        Log.i("IMEI", "imei matched!");
                        int_imei_result = 1;
                    } else if (jsonObject.getInt("resp_code") == -1) {
                        //IMEI dont match
                        Log.i("IMEI", "imei not matched!");
                        int_imei_result = -1;
                    }
                } catch (JSONException e) {
                    int_imei_result = 0;
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                ToastUtil.showToast(SelectIPActivity.this,"Please check your ip address.");
                int_imei_result = -1;
            }
        });

        return int_imei_result;
    }

}