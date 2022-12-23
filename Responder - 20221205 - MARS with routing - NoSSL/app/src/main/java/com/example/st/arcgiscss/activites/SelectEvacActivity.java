package com.example.st.arcgiscss.activites;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.st.arcgiscss.R;
import com.example.st.arcgiscss.constant.MyApplication;
import com.example.st.arcgiscss.d3View.D3View;
import com.example.st.arcgiscss.model.NewIncident;
import com.example.st.arcgiscss.util.CacheUtils;
import com.example.st.arcgiscss.util.RetrofitUtils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SelectEvacActivity extends BaseActivity {

    public Gson gson;
    public String selected_Evac = "";

    @D3View
    RadioGroup evacGroup;

    @Override
    protected void onCreate(Bundle arg0) {
        Log.i("EVENT", "[SelectEvacActivity] onCreate");
        super.onCreate(arg0);
        setContentView(R.layout.acti_select_evac);
    }

    public void onClick(View v) {
        if (v.getId() == R.id.btn_confirm) {
            Log.i("EVAC", "Confirm button pressed");

            //ZN - 20210503 take default (MC) if user straight press Confirm
            if (evacGroup.getCheckedRadioButtonId() == -1) {
                Toast.makeText(getApplicationContext(), "Please select Evac", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(SelectEvacActivity.this, MainActivity.class);
            intent.putExtra("Evac", selected_Evac);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            finish();
        } else {
            boolean checked = ((RadioButton) v).isChecked();

            // Check which radio button was clicked
            //ZN - 20211201 add new hospitals - CGH, SKGH
            switch (v.getId()) {
                case R.id.rb_MC:
                    if (checked) {
                        Log.i("EVAC", "MC checked");
                        selected_Evac = MyApplication.EVAC_MC;
                    }
                    break;
                case R.id.rb_AH:
                    if (checked) {
                        Log.i("EVAC", "AH checked");
                        selected_Evac = MyApplication.EVAC_AH;
                    }
                    break;
                case R.id.rb_CGH:
                    if (checked) {
                        Log.i("EVAC", "CGH checked");
                        selected_Evac = MyApplication.EVAC_CGH;
                    }
                    break;
                case R.id.rb_KTPH:
                    if (checked) {
                        Log.i("EVAC", "KTPH checked");
                        selected_Evac = MyApplication.EVAC_KTPH;
                    }
                    break;
                case R.id.rb_NTFGH:
                    if (checked) {
                        Log.i("EVAC", "NTFGH checked");
                        selected_Evac = MyApplication.EVAC_NTFGH;
                    }
                    break;
                case R.id.rb_NUH:
                    if (checked) {
                        Log.i("EVAC", "NUH checked");
                        selected_Evac = MyApplication.EVAC_NUH;
                    }
                    break;
                case R.id.rb_SKGH:
                    if (checked) {
                        Log.i("EVAC", "SKGH checked");
                        selected_Evac = MyApplication.EVAC_SKGH;
                    }
                    break;
                case R.id.rb_SGH:
                    if (checked) {
                        Log.i("EVAC", "SGH checked");
                        selected_Evac = MyApplication.EVAC_SGH;
                    }
                    break;
                case R.id.rb_IMH:
                    if (checked) {
                        Log.i("EVAC", "IMH checked");
                        selected_Evac = MyApplication.EVAC_IMH;
                    }
                    break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        Log.i("EVENT", "[SelectEvacActivity] onDestroy");
        super.onDestroy();
    }
}