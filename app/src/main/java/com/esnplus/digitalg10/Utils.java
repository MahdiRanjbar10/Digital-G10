package com.esnplus.digitalg10;

import static android.content.Context.MODE_PRIVATE;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.telephony.SmsManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import android.Manifest;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
public class Utils {
    public static List<AlarmDevice> getAlarmDeviceList(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("storage", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("alarmDeviceList", null);
        Type type = new TypeToken<ArrayList<AlarmDevice>>(){}.getType();
        List<AlarmDevice> alarmDeviceList = gson.fromJson(json, type);
        if (alarmDeviceList == null) {
            return new ArrayList<>();
        }
        return alarmDeviceList;
    }
    public static void saveAlarmDeviceList(Context context, List<AlarmDevice> list) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("storage", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString("alarmDeviceList", json);
        editor.apply();
    }
    public static void createAlertDialog(Context context, String message, String b1text, String b2text, View.OnClickListener listener) {
        final Dialog d = new Dialog(context,R.style.AppDialogTheme);
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        d.setCancelable(false);
        d.setContentView(R.layout.custom_alert_dialog);
        TextView tv = d.findViewById(R.id.textView);
        CheckBox bYes = d.findViewById(R.id.bYes);
        TextView bNo = d.findViewById(R.id.bNo);
        tv.setText(message);
        bYes.setText(b1text);
        bNo.setText(b2text);
        bYes.setOnClickListener(listener);
        bYes.setOnCheckedChangeListener((compoundButton, b) -> d.dismiss());
        bNo.setOnClickListener(view -> d.dismiss());
        d.show();
    }
    @SuppressLint("MissingPermission")
    public static int getSim1SubscriptionId(Context context) {
        SubscriptionManager subscriptionManager = (SubscriptionManager) context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
        List<SubscriptionInfo> subscriptionInfoList = subscriptionManager.getActiveSubscriptionInfoList();
        if (subscriptionInfoList != null && !subscriptionInfoList.isEmpty()) {
            for (SubscriptionInfo subscriptionInfo : subscriptionInfoList) {
                if (subscriptionInfo.getSimSlotIndex() == 0) {
                    return subscriptionInfo.getSubscriptionId();
                }
            }
        }
        return -1; // SIM1 not found
    }
    @SuppressLint("MissingPermission")
    public static int getSim2SubscriptionId(Context context) {
        SubscriptionManager subscriptionManager = (SubscriptionManager) context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
        List<SubscriptionInfo> subscriptionInfoList = subscriptionManager.getActiveSubscriptionInfoList();
        if (subscriptionInfoList != null && !subscriptionInfoList.isEmpty()) {
            for (SubscriptionInfo subscriptionInfo : subscriptionInfoList) {
                if (subscriptionInfo.getSimSlotIndex() == 1) {
                    return subscriptionInfo.getSubscriptionId();
                }
            }
        }
        return -1; // SIM2 not found
    }
    public static int getSelectedSimCard(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("storage", MODE_PRIVATE);
        return prefs.getInt("defaultSim",1);
    }
    public static boolean sendSMS(Activity context,String phoneNo,String msg) {
        String[] smsPermissions = new String[] {Manifest.permission.SEND_SMS,Manifest.permission.READ_SMS,Manifest.permission.RECEIVE_SMS,Manifest.permission.READ_PHONE_STATE};
        if (checkIfPermissionGranted(context,smsPermissions)) {
            int subscriptionId;
            if (getSelectedSimCard(context) == 1){
                subscriptionId = getSim1SubscriptionId(context);
                if (subscriptionId == -1){
                    showMessage("سیم 1 در گوشی نمی باشد!",context);
                    return false;
                }else {
                    return sMSG(context,phoneNo,msg,subscriptionId);
                }
            }else {
                subscriptionId = getSim2SubscriptionId(context);
                if (subscriptionId == -1){
                    showMessage("سیم 2 در گوشی نمی باشد!",context);
                    return false;
                }else {
                    return sMSG(context,phoneNo,msg,subscriptionId);
                }
            }
        }else {
            createAlertDialog(context, "جهت ارسال فرمان ها میبایست دسترسی ارسال و دریافت پیامک را به اپلیکیشن بدهید.آیا مایل به این کار می باشید؟", "بله", "خیر", v -> {
                requestPermission(context, smsPermissions, 1111);
            });
            return false;
        }
    }
    private static boolean sMSG(Activity context,String phoneNo,String msg,int subscriptionId){
        try{
            //SmsManager smsManager = SmsManager.getDefault(); //this used for normal sending message without mattering to choose simCard
            SmsManager smsManager = SmsManager.getSmsManagerForSubscriptionId(subscriptionId);
            smsManager.sendTextMessage(phoneNo, null, msg, null, null);
            Utils.showMessage("پیام فرمان ارسال شد",context);
            Log.d("SmsDataSend",msg);
            return true;
        }catch (Exception ex) {
            ex.printStackTrace();
            Utils.showMessage("خطا در ارسال اس ام اس!!!",context);
            return false;
        }
    }
    public static void showMessage(String msg, Activity context) {
        LayoutInflater inflater = context.getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast_layout,context.findViewById(R.id.toast_layout_root));

        TextView text = layout.findViewById(R.id.tv_toast);
        text.setText(msg);

        Toast toast = new Toast(context);
        toast.setGravity(Gravity.BOTTOM, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }
    public static void requestPermission(Activity context, String[] permissionArray,int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(context, permissionArray, requestCode);
        }
    }
    public static boolean checkIfPermissionGranted(Activity context, String[] permissionArray) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (String s : permissionArray) {
                if (ContextCompat.checkSelfPermission(context, s) != PackageManager.PERMISSION_GRANTED)
                    return false;
            }
            return true;
        }
        return true;
    }
}
