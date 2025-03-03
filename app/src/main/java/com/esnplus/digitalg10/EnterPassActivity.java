package com.esnplus.digitalg10;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import com.esnplus.digitalg10.databinding.ActivityEnterPassBinding;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class EnterPassActivity extends AppCompatActivity {
    String pass,strShowPass;
    private ActivityEnterPassBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = ActivityEnterPassBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        pass = getSharedPreferences("storage", MODE_PRIVATE).getString("pass","1234");
        strShowPass = "";

        binding.b0.setOnClickListener(view -> clickAndCheckPass("0"));
        binding.b1.setOnClickListener(view -> clickAndCheckPass("1"));
        binding.b2.setOnClickListener(view -> clickAndCheckPass("2"));
        binding.b3.setOnClickListener(view -> clickAndCheckPass("3"));
        binding.b4.setOnClickListener(view -> clickAndCheckPass("4"));
        binding.b5.setOnClickListener(view -> clickAndCheckPass("5"));
        binding.b6.setOnClickListener(view -> clickAndCheckPass("6"));
        binding.b7.setOnClickListener(view -> clickAndCheckPass("7"));
        binding.b8.setOnClickListener(view -> clickAndCheckPass("8"));
        binding.b9.setOnClickListener(view -> clickAndCheckPass("9"));

        binding.delete.setOnClickListener(view -> {
            if (!strShowPass.isEmpty()) {
                strShowPass = strShowPass.substring(0, strShowPass.length() - 1);
                switch (strShowPass.length()){
                    case 0:
                        binding.c1.setChecked(false);
                        binding.c2.setChecked(false);
                        binding.c3.setChecked(false);
                        binding.c4.setChecked(false);
                        break;
                    case 1:
                        binding.c1.setChecked(true);
                        binding.c2.setChecked(false);
                        binding.c3.setChecked(false);
                        binding.c4.setChecked(false);
                        break;
                    case 2:
                        binding.c1.setChecked(true);
                        binding.c2.setChecked(true);
                        binding.c3.setChecked(false);
                        binding.c4.setChecked(false);
                        break;
                    case 3:
                        binding.c1.setChecked(true);
                        binding.c2.setChecked(true);
                        binding.c3.setChecked(true);
                        binding.c4.setChecked(false);
                        break;
                    case 4:
                        binding.c1.setChecked(true);
                        binding.c2.setChecked(true);
                        binding.c3.setChecked(true);
                        binding.c4.setChecked(true);
                        break;
                }
            }
        });
    }
    private void clickAndCheckPass(String ch){
        strShowPass = strShowPass + ch;
        switch (strShowPass.length()){
            case 0:
                binding.c1.setChecked(false);
                binding.c2.setChecked(false);
                binding.c3.setChecked(false);
                binding.c4.setChecked(false);
                break;
            case 1:
                binding.c1.setChecked(true);
                binding.c2.setChecked(false);
                binding.c3.setChecked(false);
                binding.c4.setChecked(false);
                break;
            case 2:
                binding.c1.setChecked(true);
                binding.c2.setChecked(true);
                binding.c3.setChecked(false);
                binding.c4.setChecked(false);
                break;
            case 3:
                binding.c1.setChecked(true);
                binding.c2.setChecked(true);
                binding.c3.setChecked(true);
                binding.c4.setChecked(false);
                break;
            case 4:
                binding.c1.setChecked(true);
                binding.c2.setChecked(true);
                binding.c3.setChecked(true);
                binding.c4.setChecked(true);
                break;
        }
        new Handler().postDelayed(() -> {
            if (strShowPass.length() == 4){
                if (strShowPass.equals(pass)){
                    if (Utils.getAlarmDeviceList(EnterPassActivity.this).isEmpty())
                        startActivity(new Intent(EnterPassActivity.this, ManageAlarmDevicesActivity.class));
                    else startActivity(new Intent(EnterPassActivity.this, MainActivity.class));
                    finish();
                }else {
                    Animation an = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_wrongpass);
                    binding.passBar.startAnimation(an);

                    strShowPass = "";
                    binding.c1.setChecked(false);
                    binding.c2.setChecked(false);
                    binding.c3.setChecked(false);
                    binding.c4.setChecked(false);
                    Utils.showMessage("رمز عبور صحیح نمیباشد!",EnterPassActivity.this);
                }
            }
        }, 50);
    }
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }
}