package com.esnplus.digitalG10;

import static android.content.Context.MODE_PRIVATE;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import com.esnplus.digitalG10.databinding.FragmentGd10Binding;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.textfield.TextInputLayout;

public class FragmentGD10 extends Fragment {
    private FragmentGd10Binding binding;
    private FragmentActivity fragmentActivity;
    private CustomSpinner spinner;
    public FragmentGD10(CustomSpinner spinner){
        this.spinner = spinner;
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentGd10Binding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fragmentActivity = getActivity();

        binding.cvManageDevices.setOnClickListener(v -> {
            startActivity(new Intent(fragmentActivity, ManageAlarmDevicesActivity.class));
            fragmentActivity.finish();
        });
        binding.cvDeviceSettings.setOnClickListener(v -> {
            AlarmDevice alarmDevice = spinner.getSelectedItem();
            if (alarmDevice.getName().isEmpty()){
                Utils.showMessage("هیچ دستگاهی ثبت نام نشده است!",fragmentActivity);
            }else {
                Intent intent = new Intent(fragmentActivity,Settings.class);
                intent.putExtra("name",alarmDevice.getName());
                intent.putExtra("phone",alarmDevice.getPhoneNumber());
                intent.putExtra("type",alarmDevice.getType());
                startActivity(intent);
            }
        });
        binding.cvReport.setOnClickListener(v -> makeSmsDialog("گزارش"));
        binding.cvDeviceStatus.setOnClickListener(v -> makeSmsDialog("وضعیت"));
        binding.cvPhoneList.setOnClickListener(v -> makeSmsDialog("لیست"));
        binding.cvAddBalance.setOnClickListener(v -> {
            Dialog d = new Dialog(fragmentActivity, R.style.AppDialogTheme);
            d.requestWindowFeature(Window.FEATURE_NO_TITLE);
            d.setContentView(R.layout.dialog_edit_text);
            ImageView ivClose = d.findViewById(R.id.ivClose);
            TextView title = d.findViewById(R.id.tv_title);
            Button bSendOrder = d.findViewById(R.id.bSendOrder);
            TextInputLayout inputLayoutSmsText = d.findViewById(R.id.input_layout_sms_text);
            TextInputLayout inputLayoutBalance = d.findViewById(R.id.input_layout_balance);
            EditText etSmsText = d.findViewById(R.id.et_sms_text);
            EditText etBalance = d.findViewById(R.id.et_balance);
            inputLayoutSmsText.setVisibility(View.GONE);
            ivClose.setOnClickListener(view2 -> d.dismiss());
            title.setText("افزایش اعتبار");
            bSendOrder.setOnClickListener(view4 -> {
                String balance = etBalance.getText().toString();
                if (balance.isEmpty()){
                    Utils.showMessage("لطفا سریال شارژ را وارد نمایید!",fragmentActivity);
                }else {
                    makeSmsDialog("شارژ:" + balance);
                    d.dismiss();
                }
            });
            d.show();
        });
        binding.cvRealizeBalance.setOnClickListener(v -> makeSmsDialog("اعتبار"));
        binding.cvStopAlarm.setOnClickListener(v -> makeSmsDialog("توقف"));
        binding.cvManageOuts.setOnClickListener(v -> {
            Dialog d = new Dialog(fragmentActivity, R.style.AppDialogTheme);
            d.requestWindowFeature(Window.FEATURE_NO_TITLE);
            d.setContentView(R.layout.dialog_manage_outputs);
            ImageView ivClose = d.findViewById(R.id.ivClose);
            MaterialButton bOut1 = d.findViewById(R.id.bOut1);
            MaterialButton bOut2 = d.findViewById(R.id.bOut2);
            MaterialSwitch switchStatus = d.findViewById(R.id.switchStatus);
            bOut1.setChecked(true);
            Button bSendOrder = d.findViewById(R.id.bSendOrder);
            ivClose.setOnClickListener(view3 -> d.dismiss());
            switchStatus.setOnCheckedChangeListener((compoundButton, isChecked) -> {
                if (isChecked){
                    switchStatus.setText("وضعیت : روشن");
                }else{
                    switchStatus.setText("وضعیت : خاموش");
                }
            });
            bSendOrder.setOnClickListener(view3 -> {
                if (switchStatus.isChecked()){
                    makeSmsDialog(bOut1.isChecked() ? "1" : "3");
                }else {
                    makeSmsDialog(bOut1.isChecked() ? "2" : "4");
                }
                d.dismiss();
            });
            d.show();
        });
    }
    private void makeSmsDialog(String smsCommand) {
        Utils.createAlertDialog(fragmentActivity, "آیا پیامک فرمان ارسال گردد؟", "بله", "خیر", view1 -> {
            AlarmDevice alarmDevice = spinner.getSelectedItem();
            if (alarmDevice.getName().isEmpty()){
                Utils.showMessage("هیچ دستگاهی ثبت نام نشده است!",fragmentActivity);
            }else {
                SharedPreferences prefs = fragmentActivity.getSharedPreferences("storage", MODE_PRIVATE);
                String pass = prefs.getString("pass","1234");
                boolean isPassIncluded = prefs.getBoolean("pass_included",false);
                Utils.sendSMS(fragmentActivity, alarmDevice.getPhoneNumber(),isPassIncluded ? pass + smsCommand : smsCommand);
            }
        });
    }
}