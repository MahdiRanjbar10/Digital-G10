package com.esnplus.digitalg10;

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
import com.esnplus.digitalg10.databinding.ActivitySettingsBinding;
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

        binding.cvChangeSmsZone.setOnClickListener(view -> {
            Dialog d = new Dialog(Settings.this, R.style.AppDialogTheme);
            d.requestWindowFeature(Window.FEATURE_NO_TITLE);
            d.setCanceledOnTouchOutside(false);
            d.setContentView(R.layout.edittext_dialog);
            ImageView ivClose = d.findViewById(R.id.iv_close);
            TextView title = d.findViewById(R.id.tv_title);
            Button bSendOrder = d.findViewById(R.id.b_send_order);
            TextInputLayout inputLayoutSmsText = d.findViewById(R.id.input_layout_sms_text);
            TextInputLayout inputLayoutBalance = d.findViewById(R.id.input_layout_balance);
            EditText etSmsText = d.findViewById(R.id.et_sms_text);
            EditText etBalance = d.findViewById(R.id.et_balance);

            inputLayoutBalance.setVisibility(View.GONE);
            ivClose.setOnClickListener(view2 -> d.dismiss());
            title.setText("تغییر متن پیامک تحریک");
            bSendOrder.setOnClickListener(view4 -> {
                String smsText = etSmsText.getText().toString();
                if (smsText.isEmpty()){
                    Utils.showMessage("لطفا متن پیامک تحریک را وارد نمایید!",Settings.this);
                }else {
                    makeSmsDialog("TRIG:" + smsText);
                    d.dismiss();
                }
            });
            d.show();
        });

        binding.cvManagePhonelist.setOnClickListener(view -> makeCustomDialog("مدیریت شماره تلفن ها","حافظه","شماره تلفن را وارد نمایید","TEL","شماره تلفن",false));
        binding.cvNameZone.setOnClickListener(view -> makeCustomDialog("نام گذاری زون ها","زون","نام زون را وارد نمایید","زون","نام زون",false));
        binding.cvNameRemote.setOnClickListener(view -> makeCustomDialog("نام گذاری ریموت ها","ریموت","نام ریموت را وارد نمایید","ریموت","نام ریموت",false));
        binding.cvRemoveRemote.setOnClickListener(view -> makeCustomDialog("حذف ریموت","ریموت","","REMOVE","",true));
        binding.cvChangeDefaultSim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog d = new Dialog(Settings.this, R.style.AppDialogTheme);
                d.requestWindowFeature(Window.FEATURE_NO_TITLE);
                d.setCanceledOnTouchOutside(false);
                d.setContentView(R.layout.dialog_change_default_sim);
                ImageView ivClose = d.findViewById(R.id.iv_close);
                Button bSave = d.findViewById(R.id.b_save);
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
            }
        });

        binding.cvManagePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                passDialog = new Dialog(Settings.this, R.style.AppDialogTheme);
                passDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                passDialog.setCanceledOnTouchOutside(false);
                passDialog.setContentView(R.layout.dialog_password);

                etPass = passDialog.findViewById(R.id.et_password);
                textInputLayout = passDialog.findViewById(R.id.input_layout);
                bEditSavePass = passDialog.findViewById(R.id.button2);
                bCancel = passDialog.findViewById(R.id.button3);
                MaterialSwitch materialSwitch = passDialog.findViewById(R.id.switch1);
                ImageView ivClose = passDialog.findViewById(R.id.iv_close);

                SharedPreferences prefs = getSharedPreferences("storage", MODE_PRIVATE);
                editor = prefs.edit();
                pass = prefs.getString("pass","1234");
                boolean isPassEnabled = prefs.getBoolean("pass_enabled",true);

                ivClose.setOnClickListener(view1 -> passDialog.dismiss());
                materialSwitch.setOnCheckedChangeListener((compoundButton,isChecked) -> {
                    if (compoundButton.isPressed()) {
                        materialSwitch.setChecked(!isChecked);
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
                            materialSwitch.setChecked(isChecked);
                            editor.putBoolean("pass_enabled", isChecked);
                            editor.apply();
                        });
                    }
                });

                if (isPassEnabled){
                    textInputLayout.setEnabled(true);
                    bEditSavePass.setEnabled(true);
                    materialSwitch.setChecked(true);
                }else{
                    textInputLayout.setEnabled(false);
                    bEditSavePass.setEnabled(false);
                    materialSwitch.setChecked(false);
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
    private void makeCustomDialog(String txtTitle,String txt1,String txt2,String command,String error,boolean onlySliderShow) {
        Dialog d = new Dialog(Settings.this, R.style.AppDialogTheme);
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        d.setCanceledOnTouchOutside(false);
        d.setContentView(R.layout.dialog_custom_settings);
        ImageView ivClose = d.findViewById(R.id.ivClose);
        TextView current = d.findViewById(R.id.current);
        TextView guideText = d.findViewById(R.id.guide_text);
        TextView title = d.findViewById(R.id.tv_title);
        Slider slider = d.findViewById(R.id.slider);
        Button bSendOrder = d.findViewById(R.id.bSendOrder);
        Button bDelete = d.findViewById(R.id.bDelete);
        EditText et = d.findViewById(R.id.et);
        TextInputLayout inputLayout = d.findViewById(R.id.inputLayout);
        bSendOrder.setOnClickListener(view -> {
            String etText = et.getText().toString();
            if (etText.isEmpty()){
                Utils.showMessage("لطفا " + error + " را وارد نمایید!",Settings.this);
            }else {
                makeSmsDialog(command + ((int) slider.getValue()) + ":" + etText);
                d.dismiss();
            }
        });
        bDelete.setOnClickListener(view -> {
            makeSmsDialog(command + ((int) slider.getValue()) + ":");
            d.dismiss();
        });
        ivClose.setOnClickListener(view -> d.dismiss());
        slider.addOnChangeListener((slider1, value, fromUser) -> current.setText(txt1 + " انتخابی : " + ((int) value)));
        et.setHint(txt2);
        current.setText(txt1 + " انتخابی : " + ((int) slider.getValue()));
        title.setText(txtTitle);
        guideText.setText("جهت تغییر " + txt1 + " انتخابی،نوار بالا را به راست یا چپ بکشید");
        if (onlySliderShow){
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
            Utils.sendSMS(Settings.this, selectedPhoneNumber, smsCommand);
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