package com.esnplus.digitalG10;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.esnplus.digitalG10.databinding.ActivitySettingsBinding;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.slider.Slider;
import com.google.android.material.textfield.TextInputLayout;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class Settings extends AppCompatActivity {
    private ActivitySettingsBinding binding;
    String selectedPhoneNumber,selectedName;
    String firstTimePassEntering,pass;
    EditText etPass;
    TextInputLayout textInputLayout;
    Button bEditSavePass,bCancel;
    SharedPreferences.Editor editor;
    Dialog passDialog;
    private final int orderManagePhoneList = 1;
    private final int orderNameRemotes = 2;
    private final int orderRemoveRemotes = 3;
    private final int orderNameZones = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle extras = getIntent().getExtras();
        selectedName = extras.getString("name");
        selectedPhoneNumber = extras.getString("phone");
        binding.tvShowSelectedDevice.setText("دستگاه انتخابی : " + selectedName);

        int selectedType = extras.getInt("type");
        if (selectedType == AlarmDevice.GD10) {
            binding.cvChangeSmsZone.setVisibility(View.GONE);
            binding.cvRemoveRemote.setVisibility(View.GONE);
            binding.cvNameRemote.setVisibility(View.GONE);
            binding.cvNameZone.setVisibility(View.GONE);
        }else {
            binding.cvSmsZoneNegative.setVisibility(View.GONE);
            binding.cvSmsZonePositive.setVisibility(View.GONE);
        }

        binding.cvChangeSmsZone.setOnClickListener(view -> {
            createChangeSmsZoneDialog("تغییر متن پیامک تحریک","تحریک:");
        });
        binding.cvSmsZonePositive.setOnClickListener(view -> {
            createChangeSmsZoneDialog("تغییر متن تحریک +","تحریک+:");
        });
        binding.cvSmsZoneNegative.setOnClickListener(view -> {
            createChangeSmsZoneDialog("تغییر متن تحریک -","تحریک-:");
        });

        binding.cvManagePhonelist.setOnClickListener(view -> makeCustomDialog(orderManagePhoneList,20,"مدیریت شماره تلفن\u200Cها","حافظه","شماره تلفن را وارد نمایید","شماره تلفن"));
        binding.cvNameZone.setOnClickListener(view -> makeCustomDialog(orderNameZones,8,"نام گذاری زون\u200Cها","زون","نام زون را وارد نمایید","نام زون"));
        binding.cvNameRemote.setOnClickListener(view -> makeCustomDialog(orderNameRemotes,8,"نام گذاری ریموت\u200Cها","ریموت","نام ریموت را وارد نمایید","نام ریموت"));
        binding.cvRemoveRemote.setOnClickListener(view -> makeCustomDialog(orderRemoveRemotes,8,"حذف ریموت","ریموت","",""));

        binding.cvChangeDefaultSim.setOnClickListener(view -> {
            Dialog d = new Dialog(Settings.this, R.style.AppDialogTheme);
            d.requestWindowFeature(Window.FEATURE_NO_TITLE);
            d.setContentView(R.layout.dialog_change_default_sim);
            ImageView ivClose = d.findViewById(R.id.ivClose);
            Button bSave = d.findViewById(R.id.bSave);
            MaterialButton sim1Option = d.findViewById(R.id.sim1_option);
            MaterialButton sim2Option = d.findViewById(R.id.sim2_option);
            ivClose.setOnClickListener(view2 -> d.dismiss());

            SharedPreferences prefs = getSharedPreferences("storage", MODE_PRIVATE);
            editor = prefs.edit();
            int defaultSim = prefs.getInt("defaultSim",1);

            if (defaultSim == 1){
                sim1Option.setChecked(true);
            }else {
                sim2Option.setChecked(true);
            }
            bSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    editor.putInt("defaultSim",sim1Option.isChecked() ? 1 : 2);
                    editor.commit();
                    d.dismiss();
                }
            });
            d.show();
        });

        binding.cvManagePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                passDialog = new Dialog(Settings.this, R.style.AppDialogTheme);
                passDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                passDialog.setContentView(R.layout.dialog_password);

                etPass = passDialog.findViewById(R.id.et_password);
                textInputLayout = passDialog.findViewById(R.id.input_layout);
                bEditSavePass = passDialog.findViewById(R.id.button2);
                bCancel = passDialog.findViewById(R.id.button3);
                MaterialSwitch switch1 = passDialog.findViewById(R.id.switch1);
                MaterialSwitch switch2 = passDialog.findViewById(R.id.switch2);
                ImageView ivClose = passDialog.findViewById(R.id.ivClose);

                SharedPreferences prefs = getSharedPreferences("storage", MODE_PRIVATE);
                editor = prefs.edit();
                pass = prefs.getString("pass","1234");
                boolean isPassEnabled = prefs.getBoolean("pass_enabled",true);
                boolean isPassIncluded = prefs.getBoolean("pass_included",false);

                switch2.setChecked(isPassIncluded);
                switch2.setOnCheckedChangeListener((compoundButton, isChecked) -> {
                    editor.putBoolean("pass_included", isChecked);
                    editor.apply();
                });

                ivClose.setOnClickListener(view1 -> passDialog.dismiss());
                switch1.setOnCheckedChangeListener((compoundButton,isChecked) -> {
                    if (compoundButton.isPressed()) {
                        switch1.setChecked(!isChecked);
                        Utils.createAlertDialog(Settings.this, "آیا از تغییر وضعیت رمز عبور نرم افزار مطمئن می باشید؟", "بله", "خیر", view119 -> {
                            if (isChecked) {
                                textInputLayout.setEnabled(true);
                                bEditSavePass.setEnabled(true);
                                bCancel.setEnabled(true);
                            } else {
                                textInputLayout.setEnabled(false);
                                bEditSavePass.setEnabled(false);
                                bCancel.setEnabled(false);
                                bCancel.performClick();
                            }
                            switch1.setChecked(isChecked);
                            editor.putBoolean("pass_enabled", isChecked);
                            editor.apply();
                        });
                    }
                });

                if (isPassEnabled){
                    textInputLayout.setEnabled(true);
                    bEditSavePass.setEnabled(true);
                    switch1.setChecked(true);
                }else{
                    textInputLayout.setEnabled(false);
                    bEditSavePass.setEnabled(false);
                    switch1.setChecked(false);
                }

                if (pass.isEmpty()){
                    readyForNewPass();
                }else {
                    readyForEditPass();
                }

                bCancel.setOnClickListener(view7 -> {
                    if (pass.isEmpty()){// we have no password
                        bCancel.setVisibility(View.GONE);
                        readyForNewPass();
                    }else {
                        bCancel.setVisibility(View.GONE);
                        readyForEditPass();
                    }
                });

                passDialog.show();
            }
        });
    }

    private void createChangeSmsZoneDialog(String title,String command) {
        Dialog d = new Dialog(Settings.this, R.style.AppDialogTheme);
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        d.setContentView(R.layout.dialog_edit_text);
        ImageView ivClose = d.findViewById(R.id.ivClose);
        TextView tvTitle = d.findViewById(R.id.tv_title);
        Button bSendOrder = d.findViewById(R.id.bSendOrder);
        TextInputLayout inputLayoutBalance = d.findViewById(R.id.input_layout_balance);
        EditText etSmsText = d.findViewById(R.id.et_sms_text);

        inputLayoutBalance.setVisibility(View.GONE);
        ivClose.setOnClickListener(view2 -> d.dismiss());
        tvTitle.setText(title);
        bSendOrder.setOnClickListener(view4 -> {
            String smsText = etSmsText.getText().toString();
            if (smsText.isEmpty()){
                Utils.showMessage("لطفا متن پیامک تحریک را وارد نمایید!",Settings.this);
            }else {
                makeSmsDialog(command + smsText);
                d.dismiss();
            }
        });
        d.show();
    }

    private void makeCustomDialog(int order,int valueTo,String title,String sliderCurrentTxt,String hint,String errorWord) {
        Dialog d = new Dialog(Settings.this, R.style.AppDialogTheme);
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        d.setContentView(R.layout.dialog_custom_settings);
        ImageView ivClose = d.findViewById(R.id.ivClose);
        TextView tvSliderCurrent = d.findViewById(R.id.current);
        TextView tvGuideText = d.findViewById(R.id.guide_text);
        TextView tvTitle = d.findViewById(R.id.tv_title);
        Slider slider = d.findViewById(R.id.slider);
        Button bSendOrder = d.findViewById(R.id.bSendOrder);
        Button bDelete = d.findViewById(R.id.bDelete);
        EditText editText = d.findViewById(R.id.et);
        TextInputLayout inputLayout = d.findViewById(R.id.inputLayout);

        bSendOrder.setOnClickListener(view -> {
            String etText = editText.getText().toString();
            if (etText.isEmpty()){
                Utils.showMessage("لطفا " + errorWord + " را وارد نمایید!",Settings.this);
            }else {
                String sms = "";
                switch (order){
                    case orderManagePhoneList:
                        sms = "TEL" + ((int) slider.getValue()) + ":" + etText;
                        break;
                    case orderNameRemotes:
                        sms = "ریموت" + Utils.replaceWithPersian$(((int) slider.getValue())) + ":" + etText;
                        break;
                    case orderRemoveRemotes:
                        sms = "";
                        break;
                    case orderNameZones:
                        sms = "زون" + "۰" + Utils.replaceWithPersian$(((int) slider.getValue())) + ":" + etText;
                        break;
                }
                makeSmsDialog(sms);
                d.dismiss();
            }
        });
        bDelete.setOnClickListener(view -> {
            String sms = "";
            switch (order){
                case orderManagePhoneList:
                    sms = "TEL" + ((int) slider.getValue()) + ":";
                    break;
                case orderNameRemotes:
                    sms = "ریموت" + Utils.replaceWithPersian$(((int) slider.getValue())) + ":";
                    break;
                case orderRemoveRemotes:
                    sms = "REMOVE:" + ((int) slider.getValue());
                    break;
                case orderNameZones:
                    sms = "زون" + "۰" + Utils.replaceWithPersian$(((int) slider.getValue())) + ":";
                    break;
            }
            makeSmsDialog(sms);
            d.dismiss();
        });
        ivClose.setOnClickListener(view -> d.dismiss());
        slider.addOnChangeListener((slider1, value, fromUser) -> tvSliderCurrent.setText(sliderCurrentTxt + " انتخابی : " + ((int) value)));
        editText.setHint(hint);
        slider.setValueTo(valueTo);
        tvSliderCurrent.setText(sliderCurrentTxt + " انتخابی : " + ((int) slider.getValue()));
        tvTitle.setText(title);
        tvGuideText.setText("جهت تغییر " + sliderCurrentTxt + " انتخابی، نوار بالا را به راست یا چپ بکشید");
        if (order == orderRemoveRemotes){
            bSendOrder.setVisibility(View.GONE);
            inputLayout.setVisibility(View.GONE);
        }
        d.show();
    }
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }
    private void makeSmsDialog(String smsCommand) {
        Utils.createAlertDialog(Settings.this, "آیا پیامک فرمان ارسال گردد؟", "بله", "خیر", view1 -> {
            SharedPreferences prefs = getSharedPreferences("storage", MODE_PRIVATE);
            String pass = prefs.getString("pass","1234");
            boolean isPassIncluded = prefs.getBoolean("pass_included",false);
            Utils.sendSMS(Settings.this, selectedPhoneNumber,isPassIncluded ? pass + smsCommand : smsCommand);
        });
    }
    private void readyForEditPass() {
        textInputLayout.setVisibility(View.GONE);
        bEditSavePass.setText("تغییر رمز عبور");
        bEditSavePass.setOnClickListener(view -> {
            makeSaveButtonFlexible();
        });
    }
    private void makeSaveButtonFlexible(){
        bEditSavePass.setText("بعدی");
        bCancel.setVisibility(View.VISIBLE);
        textInputLayout.setVisibility(View.VISIBLE);
        textInputLayout.setHint("ابتدا رمز عبور فعلی را وارد نمایید");
        etPass.setText("");
        bEditSavePass.setOnClickListener(view12 -> {
            String val = etPass.getText().toString();
            if (val.isEmpty()){
                Utils.showMessage("لطفا رمز عبور فعلی نرم افزار را وارد نمایید",Settings.this);
            }else {
                if (val.equals(pass)){
                    readyForNewPass();
                }else {
                    Utils.showMessage("رمز عبور به درستی وارد نشده است!",Settings.this);
                }
            }
        });
    }
    private void readyForNewPass() {
        textInputLayout.setHint("رمز عبور جدید را وارد نمایید");
        bEditSavePass.setText("بعدی");
        firstTimePassEntering = "";
        etPass.setText("");
        bEditSavePass.setOnClickListener(view -> setListenerSave());
    }
    private void setListenerSave(){
        String et1stTimeVal = etPass.getText().toString();
        if (et1stTimeVal.isEmpty()){
            Utils.showMessage("لطفا رمز عبور جدید را وارد نمایید!",Settings.this);
        }else if (et1stTimeVal.length() < 4){
            Utils.showMessage("طول رمز عبور نمی تواند کمتر از 4 رقم باشد!",Settings.this);
        }else {
            bEditSavePass.setText("ذخیره");
            bCancel.setVisibility(View.VISIBLE);
            firstTimePassEntering = et1stTimeVal;
            etPass.setText("");
            textInputLayout.setHint("رمز عبور خود را دوباره وارد نمایید");
            bEditSavePass.setOnClickListener(view1 -> {
                String et2ndTimeVal = etPass.getText().toString();
                if (et2ndTimeVal.isEmpty()){
                    Utils.showMessage("لطفا ابتدا رمز عبور خود را دوباره وارد نمایید",Settings.this);
                }else {
                    if (et2ndTimeVal.equals(firstTimePassEntering)){
                        editor.putString("pass", et2ndTimeVal);
                        editor.apply();
                        Utils.showMessage("رمز عبور با موفقیت ذخیره گردید", Settings.this);
                        passDialog.dismiss();
                    }else {
                        Utils.showMessage("رمز عبور با مقدار وارد شده قبلی یکسان نیست!",Settings.this);
                    }
                }
            });
        }
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}