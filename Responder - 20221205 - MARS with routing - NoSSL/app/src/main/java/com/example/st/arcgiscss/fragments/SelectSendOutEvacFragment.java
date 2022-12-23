package com.example.st.arcgiscss.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.st.arcgiscss.R;
import com.example.st.arcgiscss.activites.NewMainActivity;
import com.example.st.arcgiscss.constant.MyApplication;
import com.example.st.arcgiscss.d3View.D3Fragment;
import com.example.st.arcgiscss.d3View.D3View;
import com.example.st.arcgiscss.model.IncidentLoc;
import com.example.st.arcgiscss.util.CornerUtil;
import com.google.gson.Gson;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.ArrayList;
import java.util.List;


public class SelectSendOutEvacFragment extends D3Fragment {

    public Gson gson;
    public String selected_Evac = "";
    public String selected_type = "", selected_no_casualties = "";

    @D3View
    RadioGroup _sent_out_evacGroup;

    @D3View
    EditText et_frag_send_out_evac_remarks;

    @D3View
    TextView textView1, textView2;

    //ZN - 20220224 new send-out feature
    @D3View(click = "onClick")
    RadioButton rb_sent_out_MC,rb_sent_out_AH,rb_sent_out_CGH,rb_sent_out_KTPH,rb_sent_out_NTFGH,rb_sent_out_NUH,rb_sent_out_SKGH,rb_sent_out_SGH,rb_sent_out_IMH ;
    @D3View(click = "onClick")
    Button send_out_btn_confirm;

    //ZN - 20220322 smart search feature for medical centre / hospital
    @D3View(click = "onClick")
    AutoCompleteTextView tv_act_select_EvacCentre;

    String[] mc_options, evac_options, type_options, no_casualties_options;
    ArrayAdapter<String> mc_arrayAdapter, evac_arrayAdapter, type_arrayAdapter, no_casualties_arrayAdapter;
    private boolean isEvacTextEntered;

    //ZN - 20220322 smart search feature for medical centre / hospital - add evac type drop down
    @D3View
    MaterialSpinner sp_evac_type, sp_no_of_casualties;
    private List<String> evacTypes = new ArrayList<>();
    private List<String> noCasualties = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i("EVENT", "[SelectSendOutEvacFragment] onCreateView");

        //ZN - 20220322 smart search feature for medical centre / hospital - read from only one combined hashmap (i.e. hospital)
        //ZN - 20220521 smart search feature for medical centre / hospital - add casualties drop down
        //mc_options = (String[]) MyApplication.getInstance().getEvacMCLocations().keySet().toArray(new String[0]);
        evac_options = (String[]) MyApplication.getInstance().getEvacHospitalLocations().keySet().toArray(new String[0]);
        type_options = new String[] {"Urgent", "Routine"};
        no_casualties_options = new String[]{"1", "2", "3", "4", "5"};

        View view = setContentView(inflater, R.layout.frag_select_send_out_evac_with_search);
        //ZN - 20220322 smart search feature for medical centre / hospital - read from only one combined hashmap (i.e. hospital)
        //mc_arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, mc_options);
        evac_arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, evac_options);
        //no_casualties_arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, evac_options);

        //default settings
        tv_act_select_EvacCentre.setText("");
//        tv_act_select_Hospital.setText("");
        tv_act_select_EvacCentre.setEnabled(true);
//        tv_act_select_Hospital.setEnabled(true);
        textView1.setTextColor(Color.WHITE);
        textView2.setTextColor(Color.WHITE);
        et_frag_send_out_evac_remarks.setText("");

        tv_act_select_EvacCentre.setThreshold(1);
        tv_act_select_EvacCentre.setAdapter(evac_arrayAdapter);
        tv_act_select_EvacCentre.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                tv_act_select_EvacCentre.setText(evac_arrayAdapter.getItem(i));
//                tv_act_select_Hospital.setEnabled(false);
                textView2.setTextColor(Color.GRAY);
                isEvacTextEntered = true;
                selected_Evac = evac_arrayAdapter.getItem(i);

            }
        });
        tv_act_select_EvacCentre.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (tv_act_select_EvacCentre.getText().length() == 0) {
//                    tv_act_select_Hospital.setEnabled(true);
                    textView2.setTextColor(Color.WHITE);
                    isEvacTextEntered = false;
                    selected_Evac = "";
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

//        tv_act_select_Hospital.setThreshold(1);
//        tv_act_select_Hospital.setAdapter(hospital_arrayAdapter);
//        tv_act_select_Hospital.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                tv_act_select_Hospital.setText(hospital_arrayAdapter.getItem(i));
//                tv_act_select_MedicalCentre.setEnabled(false);
//                textView1.setTextColor(Color.GRAY);
//                isEvacTextEntered = true;
//                selected_Evac = hospital_arrayAdapter.getItem(i);
//
//            }
//        });
//        tv_act_select_Hospital.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                if (tv_act_select_Hospital.getText().length() == 0) {
//                    tv_act_select_MedicalCentre.setEnabled(true);
//                    textView1.setTextColor(Color.WHITE);
//                    isEvacTextEntered = false;
//                    selected_Evac = "";
//                }
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//            }
//        });

        //ZN - 20220322 smart search feature for medical centre / hospital - add evac type drop down
        selected_type = type_options[0];
        sp_evac_type.setItems(type_options);
        sp_evac_type.setPadding(40, 0, 0, 0);
        sp_evac_type.setDropdownMaxHeight(550);
        sp_evac_type.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                selected_type = sp_evac_type.getText().toString();
            }
        });
        CornerUtil.clipViewCornerByDp(sp_evac_type,10);

        //ZN - 20220521 smart search feature for medical centre / hospital - add casualties drop down
        selected_no_casualties = no_casualties_options[0];
        sp_no_of_casualties.setItems(no_casualties_options);
        sp_no_of_casualties.setPadding(40, 0, 0, 0);
        sp_no_of_casualties.setDropdownMaxHeight(550);
        sp_no_of_casualties.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                selected_no_casualties = sp_no_of_casualties.getText().toString();
            }
        });
        CornerUtil.clipViewCornerByDp(sp_evac_type,10);

        return view;
    }



    public void onClick(View v) {
        if (v.getId() == R.id.send_out_btn_confirm) {
            Log.i("EVAC", "Confirm button pressed");

//            if (_sent_out_evacGroup.getCheckedRadioButtonId() == -1) {
//                Toast.makeText(getContext(), "Please select Evac", Toast.LENGTH_SHORT).show();
//                return;
//            }

            if (!isEvacTextEntered) {
                Toast.makeText(getContext(), "Please select Evac", Toast.LENGTH_SHORT).show();
                return;
            }

            //ZN - 20220521 smart search feature for medical centre / hospital - add casualties drop down
            String str_remarks = selected_type + "-" + selected_no_casualties + " casualties" + "-" + et_frag_send_out_evac_remarks.getText().toString();
            String str_ret = "Send Out-" + selected_Evac + "-" + str_remarks;
            Log.i("SEND OUT", "return string: " + str_ret);
            ((NewMainActivity) getActivity()).updateSystemReadyStatus(NewMainActivity.SYSTEM_NOT_READY);
            ((NewMainActivity) getActivity()).callServerSystemReady(NewMainActivity.SYSTEM_NOT_READY, str_ret);
            ((NewMainActivity) getActivity()).switchFragment(NewMainActivity.FRAG_HOME);

            //ZN - 20220224 new send-out feature - add to incident record
            //ZN - 20220521 smart search feature for medical centre / hospital - add casualties drop down
            ((NewMainActivity) getActivity()).createSendOutIncident(str_ret);

            tv_act_select_EvacCentre.setText("");

            //ZN - 20220619 logging to external file
            Log.i("INCIDENT", "[onClick] Send out incident created: " + str_ret);

//            tv_act_select_Hospital.setText("");
        }
//        } else {
//
//            boolean checked = ((RadioButton) v).isChecked();
//
//            // Check which radio button was clicked
//            switch (v.getId()) {
//                case R.id.rb_sent_out_MC:
//                    if (checked) {
//                        Log.i("EVAC", "MC checked");
//                        selected_Evac = MyApplication.EVAC_MC;
//                    }
//                    break;
//                case R.id.rb_sent_out_AH:
//                    if (checked) {
//                        Log.i("EVAC", "AH checked");
//                        selected_Evac = MyApplication.EVAC_AH;
//                    }
//                    break;
//                case R.id.rb_sent_out_CGH:
//                    if (checked) {
//                        Log.i("EVAC", "CGH checked");
//                        selected_Evac = MyApplication.EVAC_CGH;
//                    }
//                    break;
//                case R.id.rb_sent_out_KTPH:
//                    if (checked) {
//                        Log.i("EVAC", "KTPH checked");
//                        selected_Evac = MyApplication.EVAC_KTPH;
//                    }
//                    break;
//                case R.id.rb_sent_out_NTFGH:
//                    if (checked) {
//                        Log.i("EVAC", "NTFGH checked");
//                        selected_Evac = MyApplication.EVAC_NTFGH;
//                    }
//                    break;
//                case R.id.rb_sent_out_NUH:
//                    if (checked) {
//                        Log.i("EVAC", "NUH checked");
//                        selected_Evac = MyApplication.EVAC_NUH;
//                    }
//                    break;
//                case R.id.rb_sent_out_SKGH:
//                    if (checked) {
//                        Log.i("EVAC", "SKGH checked");
//                        selected_Evac = MyApplication.EVAC_SKGH;
//                    }
//                    break;
//                case R.id.rb_sent_out_SGH:
//                    if (checked) {
//                        Log.i("EVAC", "SGH checked");
//                        selected_Evac = MyApplication.EVAC_SGH;
//                    }
//                    break;
//                case R.id.rb_sent_out_IMH:
//                    if (checked) {
//                        Log.i("EVAC", "IMH checked");
//                        selected_Evac = MyApplication.EVAC_IMH;
//                    }
//                    break;
//            }
//        }
    }

    @Override
    public void onDetach() {
        Log.i("EVENT", "[SelectSendOutEvacFragment] onDetach");
        super.onDetach();
    }

    @Override
    public void onPause() {
        Log.i("EVENT", "[SelectSendOutEvacFragment] onPause");
        super.onPause();
    }

    @Override
    public void onResume() {
        Log.i("EVENT", "[SelectSendOutEvacFragment] onResume");
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        Log.i("EVENT", "[SelectSendOutEvacFragment] onDestroyView");
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        Log.i("EVENT", "[SelectSendOutEvacFragment] onPause");
        super.onDestroy();
    }



}