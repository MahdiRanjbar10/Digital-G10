package com.esnplus.digitalg10;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.esnplus.digitalg10.databinding.ActivityManageAlarmDevicesBinding;
import com.google.android.material.button.MaterialButton;
import java.util.List;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class ManageAlarmDevicesActivity extends AppCompatActivity {
    private List<AlarmDevice> list;
    DevicesListAdapter adapter;
    private ActivityManageAlarmDevicesBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityManageAlarmDevicesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        list = Utils.getAlarmDeviceList(ManageAlarmDevicesActivity.this);
        adapter = new DevicesListAdapter();
        binding.rv.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        binding.rv.setLayoutManager(mLayoutManager);
        binding.rv.setAdapter(adapter);

        checkIfListIsEmpty();

        binding.fabAddDevice.setOnClickListener(view -> {
            Dialog d = new Dialog(ManageAlarmDevicesActivity.this, R.style.AppDialogTheme);
            d.requestWindowFeature(Window.FEATURE_NO_TITLE);
            d.setCancelable(false);
            d.setContentView(R.layout.dialog_add_device);
            ImageView ivClose = d.findViewById(R.id.ivClose);
            EditText etName = d.findViewById(R.id.et_name);
            EditText etPhoneNumber = d.findViewById(R.id.et_phone_number);
            MaterialButton M52Option = d.findViewById(R.id.M52_option);
            MaterialButton GD10Option = d.findViewById(R.id.GD10_option);
//            MaterialButton faOption = d.findViewById(R.id.fa_option);
//            MaterialButton enOption = d.findViewById(R.id.en_option);
            MaterialButton secOption1 = d.findViewById(R.id.secOption1);
            MaterialButton secOption2 = d.findViewById(R.id.secOption2);
            MaterialButton secOption3 = d.findViewById(R.id.secOption3);
            MaterialButton secOption4 = d.findViewById(R.id.secOption4);
            MaterialButton secOption5 = d.findViewById(R.id.secOption5);
            Button bReset = d.findViewById(R.id.bReset);
            Button bSaveDevice = d.findViewById(R.id.bSaveDevice);
            ivClose.setOnClickListener(view2 -> d.dismiss());
            bReset.setOnClickListener(view1 -> {
                etName.setText("");
                etPhoneNumber.setText("");
                M52Option.setChecked(false);
                GD10Option.setChecked(false);
//                faOption.setChecked(false);
//                enOption.setChecked(false);
                secOption1.setChecked(false);
                secOption2.setChecked(false);
                secOption3.setChecked(false);
                secOption4.setChecked(false);
                secOption5.setChecked(false);
            });
            bSaveDevice.setOnClickListener(view3 -> {
                if (etName.getText().toString().isEmpty())
                    Utils.showMessage("نام دستگاه نمی تواند خالی باشد!",ManageAlarmDevicesActivity.this);
                else if (etPhoneNumber.getText().toString().isEmpty())
                    Utils.showMessage("شماره سیمکارت دستگاه نمی تواند خالی باشد!",ManageAlarmDevicesActivity.this);
                else if (!M52Option.isChecked() && !GD10Option.isChecked())
                    Utils.showMessage("نوع دستگاه باید مشخص شود!",ManageAlarmDevicesActivity.this);
                //else if (!faOption.isChecked() && !enOption.isChecked())
                //    Utils.showMessage("زبان ارتباطی دستگاه باید مشخص شود!",ManageAlarmDevicesActivity.this);
                else if (!secOption1.isChecked() && !secOption2.isChecked() && !secOption3.isChecked() && !secOption4.isChecked() && !secOption5.isChecked())
                    Utils.showMessage("لطفا آیکون دستگاه را انتخاب کنید!",ManageAlarmDevicesActivity.this);
                else {
                    AlarmDevice alarmDevice = new AlarmDevice();
                    alarmDevice.setName(etName.getText().toString());
                    alarmDevice.setPhoneNumber(etPhoneNumber.getText().toString());
                    alarmDevice.setType(M52Option.isChecked() ? AlarmDevice.M52 : AlarmDevice.GD10);
                    //alarmDevice.setLanguage(faOption.isChecked() ? AlarmDevice.faLanguage : AlarmDevice.enLanguage);
                    alarmDevice.setIcon(retrieveIcon(secOption1, secOption2, secOption3, secOption4, secOption5));
                    adapter.addItem(alarmDevice);
                    Utils.saveAlarmDeviceList(ManageAlarmDevicesActivity.this, list);
                    checkIfListIsEmpty();
                    d.dismiss();
                }
            });
            d.show();
        });
    }

    private void checkIfListIsEmpty() {
        if (list.isEmpty()){
            binding.tvShowNoDevice.setVisibility(View.VISIBLE);
            binding.rv.setVisibility(View.GONE);
        }else {
            binding.tvShowNoDevice.setVisibility(View.GONE);
            binding.rv.setVisibility(View.VISIBLE);
        }
    }

    private int retrieveIcon(MaterialButton secOption1, MaterialButton secOption2, MaterialButton secOption3, MaterialButton secOption4, MaterialButton secOption5) {
        if (secOption1.isChecked())
            return R.drawable.sec_ic1;
        else if (secOption2.isChecked())
            return R.drawable.sec_ic2;
        else if (secOption3.isChecked())
            return R.drawable.sec_ic3;
        else if (secOption4.isChecked())
            return R.drawable.sec_ic4;

        else return R.drawable.sec_ic5;
    }
    class DevicesListAdapter extends RecyclerView.Adapter<View_Holder> {
        @NonNull
        @Override
        public View_Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.alarm_device_item, parent, false);
            return new View_Holder(view);
        }
        @Override
        public void onBindViewHolder(final View_Holder holder, final int position) {
            AlarmDevice alarmDevice = list.get(position);
            holder.name.setText(alarmDevice.getName());
            holder.phNumber.setText(alarmDevice.getPhoneNumber());
            holder.icon.setImageResource(alarmDevice.getIcon());
            String lang = alarmDevice.getLanguage() == AlarmDevice.faLanguage ? "فارسی" : "انگلیسی";
            String type = alarmDevice.getType() == AlarmDevice.GD10 ? "GD10" : "M52";
            //holder.info.setText(" نوع دستگاه : " + type + " | زبان ارتباطی : " + lang);
            holder.info.setText(" نوع دستگاه : " + type);
            holder.itemView.setOnClickListener(view -> {
                Intent i = new Intent(ManageAlarmDevicesActivity.this,MainActivity.class);
                i.putExtra("position",holder.getAdapterPosition());
                startActivity(i);
                finish();
            });
            holder.delete.setOnClickListener(view -> Utils.createAlertDialog(ManageAlarmDevicesActivity.this, "آیا از حذف این دستگاه مطمئن هستید؟", "بله", "خیر", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    removeItem(holder.getAdapterPosition());
                    Utils.saveAlarmDeviceList(ManageAlarmDevicesActivity.this, list);
                    checkIfListIsEmpty();
                }
            }));
            holder.edit.setOnClickListener(view -> {
                Dialog d = new Dialog(ManageAlarmDevicesActivity.this, R.style.AppDialogTheme);
                d.requestWindowFeature(Window.FEATURE_NO_TITLE);
                d.setCancelable(false);
                d.setContentView(R.layout.dialog_add_device);
                ImageView ivClose = d.findViewById(R.id.ivClose);
                EditText etName = d.findViewById(R.id.et_name);
                EditText etPhoneNumber = d.findViewById(R.id.et_phone_number);
                MaterialButton M52Option = d.findViewById(R.id.M52_option);
                MaterialButton GD10Option = d.findViewById(R.id.GD10_option);
//                MaterialButton faOption = d.findViewById(R.id.fa_option);
//                MaterialButton enOption = d.findViewById(R.id.en_option);
                MaterialButton secOption1 = d.findViewById(R.id.secOption1);
                MaterialButton secOption2 = d.findViewById(R.id.secOption2);
                MaterialButton secOption3 = d.findViewById(R.id.secOption3);
                MaterialButton secOption4 = d.findViewById(R.id.secOption4);
                MaterialButton secOption5 = d.findViewById(R.id.secOption5);
                Button bReset = d.findViewById(R.id.bReset);
                Button bSaveDevice = d.findViewById(R.id.bSaveDevice);
                ivClose.setOnClickListener(view2 -> d.dismiss());
                etName.setText(alarmDevice.getName());
                etPhoneNumber.setText(alarmDevice.getPhoneNumber());
                if (alarmDevice.getType() == AlarmDevice.GD10) GD10Option.setChecked(true);
                else M52Option.setChecked(true);
                //if (alarmDevice.getLanguage() == AlarmDevice.faLanguage) faOption.setChecked(true);
                //else enOption.setChecked(true);
                if (alarmDevice.getIcon() == R.drawable.sec_ic1) secOption1.setChecked(true);
                else if (alarmDevice.getIcon() == R.drawable.sec_ic2) secOption2.setChecked(true);
                else if (alarmDevice.getIcon() == R.drawable.sec_ic3) secOption3.setChecked(true);
                else if (alarmDevice.getIcon() == R.drawable.sec_ic4) secOption4.setChecked(true);
                else secOption5.setChecked(true);
                bReset.setOnClickListener(view1 -> {
                    etName.setText("");
                    etPhoneNumber.setText("");
                    M52Option.setChecked(false);
                    GD10Option.setChecked(false);
//                    faOption.setChecked(false);
//                    enOption.setChecked(false);
                    secOption1.setChecked(false);
                    secOption2.setChecked(false);
                    secOption3.setChecked(false);
                    secOption4.setChecked(false);
                    secOption5.setChecked(false);
                });
                bSaveDevice.setOnClickListener(view3 -> {
                    if (etName.getText().toString().isEmpty())
                        Utils.showMessage("نام دستگاه نمی تواند خالی باشد!",ManageAlarmDevicesActivity.this);
                    else if (etPhoneNumber.getText().toString().isEmpty())
                        Utils.showMessage("شماره سیمکارت دستگاه نمی تواند خالی باشد!",ManageAlarmDevicesActivity.this);
                    else if (!M52Option.isChecked() && !GD10Option.isChecked())
                        Utils.showMessage("نوع دستگاه باید مشخص شود!",ManageAlarmDevicesActivity.this);
                    //else if (!faOption.isChecked() && !enOption.isChecked())
                     //   Utils.showMessage("زبان ارتباطی دستگاه باید مشخص شود!",ManageAlarmDevicesActivity.this);
                    else if (!secOption1.isChecked() && !secOption2.isChecked() && !secOption3.isChecked() && !secOption4.isChecked() && !secOption5.isChecked())
                        Utils.showMessage("لطفا آیکون دستگاه را انتخاب کنید!",ManageAlarmDevicesActivity.this);
                    else {
                        AlarmDevice alarmDevice1 = new AlarmDevice();
                        alarmDevice1.setName(etName.getText().toString());
                        alarmDevice1.setPhoneNumber(etPhoneNumber.getText().toString());
                        alarmDevice1.setType(M52Option.isChecked() ? AlarmDevice.M52 : AlarmDevice.GD10);
                        //alarmDevice1.setLanguage(faOption.isChecked() ? AlarmDevice.faLanguage : AlarmDevice.enLanguage);
                        alarmDevice1.setIcon(retrieveIcon(secOption1, secOption2, secOption3, secOption4, secOption5));
                        adapter.setItem(holder.getAdapterPosition(), alarmDevice1);
                        Utils.saveAlarmDeviceList(ManageAlarmDevicesActivity.this, list);
                        d.dismiss();
                    }
                });
                d.show();
            });
        }
        @Override
        public int getItemCount() {
            return list.size();
        }
        public void removeItem(int position){
            list.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(0,list.size());
        }
        public void setItem(int position,AlarmDevice alarmDevice){
            list.set(position,alarmDevice);
            notifyItemChanged(position);
        }
        public void addItem(AlarmDevice alarmDevice){
            list.add(alarmDevice);
            notifyItemInserted(list.size() - 1);
        }
    }
    class View_Holder extends RecyclerView.ViewHolder {
        TextView name,phNumber,info;
        ImageView delete,edit,icon;

        public View_Holder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tv_name);
            phNumber = itemView.findViewById(R.id.tv_phone_number);
            info = itemView.findViewById(R.id.tv_info);
            delete = itemView.findViewById(R.id.iv_delete);
            edit = itemView.findViewById(R.id.iv_edit);
            icon = itemView.findViewById(R.id.iv_icon);
        }
    }
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    @Override
    public void onBackPressed() {
        startActivity(new Intent(ManageAlarmDevicesActivity.this,MainActivity.class));
        finish();
    }
}