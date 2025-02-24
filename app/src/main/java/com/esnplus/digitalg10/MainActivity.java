package com.esnplus.digitalg10;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.materialswitch.MaterialSwitch;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import com.esnplus.digitalg10.databinding.ActivityMainBinding;
import com.google.android.material.textfield.TextInputLayout;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.content.toolbar);

        binding.content.spinner.setUpAdapter(Utils.getAlarmDeviceList(MainActivity.this));
        if (getIntent().getExtras() != null){
            binding.content.spinner.setSelectedItem(getIntent().getExtras().getInt("position"));
        }

        binding.content.bSound.setChecked(true);
        binding.content.cvManageDevices.setOnClickListener(view -> {
            MainActivity.this.startActivity(new Intent(MainActivity.this, ManageAlarmDevicesActivity.class));
            finish();
        });
        binding.content.cvDeviceSettings.setOnClickListener(view -> {
            AlarmDevice alarmDevice = binding.content.spinner.getSelectedItem();
            if (alarmDevice.getName().isEmpty()){
                Utils.showMessage("هیچ دستگاهی ثبت نام نشده است!",MainActivity.this);
            }else {
                Intent intent = new Intent(MainActivity.this,Settings.class);
                intent.putExtra("name",alarmDevice.getName());
                intent.putExtra("phone",alarmDevice.getPhoneNumber());
                startActivity(intent);
            }
        });
        binding.content.cvOn.setOnClickListener(view -> makeSmsDialog(binding.content.bSound.isChecked() ? "روشن" : "روشن بیصدا"));
        binding.content.cvOff.setOnClickListener(view -> makeSmsDialog(binding.content.bSound.isChecked() ? "خاموش" : "خاموش بیصدا"));
        binding.content.cvHalfOn.setOnClickListener(view -> makeSmsDialog("نیمه روشن"));
        binding.content.cvStopSiren.setOnClickListener(view -> makeSmsDialog("قطع"));
        binding.content.cvStartSiren.setOnClickListener(view -> makeSmsDialog("آژیر"));
        binding.content.cvReport.setOnClickListener(view -> makeSmsDialog("گزارش"));
        binding.content.cvDeviceStatus.setOnClickListener(view -> makeSmsDialog("وضعیت"));
        binding.content.cvPhoneList.setOnClickListener(view -> makeSmsDialog("لیست"));
        binding.content.cvAddBalance.setOnClickListener(view -> {
            Dialog d = new Dialog(MainActivity.this, R.style.AppDialogTheme);
            d.requestWindowFeature(Window.FEATURE_NO_TITLE);
            d.setCancelable(false);
            d.setContentView(R.layout.edittext_dialog);
            ImageView ivClose = d.findViewById(R.id.iv_close);
            TextView title = d.findViewById(R.id.tv_title);
            Button bSendOrder = d.findViewById(R.id.b_send_order);
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
                    Utils.showMessage("لطفا سریال شارژ را وارد نمایید!",MainActivity.this);
                }else {
                    makeSmsDialog("شارژ:" + balance);
                    d.dismiss();
                }
            });
            d.show();
        });
        binding.content.cvRealizeBalance.setOnClickListener(view -> makeSmsDialog("اعتبار"));
        binding.content.cvStopAlarm.setOnClickListener(view -> makeSmsDialog("توقف"));
        binding.content.cvManageOuts.setOnClickListener(view -> {
            Dialog d = new Dialog(MainActivity.this, R.style.AppDialogTheme);
            d.requestWindowFeature(Window.FEATURE_NO_TITLE);
            d.setCancelable(false);
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
        DrawerLayout drawer = binding.drawerLayout;
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, findViewById(R.id.toolbar), R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void makeSmsDialog(String smsCommand) {
        Utils.createAlertDialog(MainActivity.this, "آیا پیامک فرمان ارسال گردد؟", "بله", "خیر", view1 -> {
            AlarmDevice alarmDevice = binding.content.spinner.getSelectedItem();
            if (alarmDevice.getName().isEmpty()){
                Utils.showMessage("هیچ دستگاهی ثبت نام نشده است!",MainActivity.this);
            }else {
                Utils.sendSMS(MainActivity.this, alarmDevice.getPhoneNumber(), smsCommand);
            }
        });
    }
    @Override
    public boolean onSupportNavigateUp() {
        return super.onSupportNavigateUp();
    }
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }
    @Override
    public void onBackPressed() {
        Utils.createAlertDialog(MainActivity.this, "آیا میخواهید از نرم افزار خارج شوید؟", "بله", "خیر", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}