package com.esnplus.digitalg10;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.esnplus.digitalg10.databinding.ActivityMainBinding;
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
        FragmentGD10 fragmentGD10 = new FragmentGD10(binding.content.spinner);
        FragmentM52 fragmentM52 = new FragmentM52(binding.content.spinner);

        if (binding.content.spinner.getSelectedItem().getType() == AlarmDevice.GD10){
            switchFragmentTo(fragmentGD10);
        }else {
            switchFragmentTo(fragmentM52);
        }
        binding.content.spinner.setOnItemClickListener(position -> {
            if (Utils.getAlarmDeviceList(MainActivity.this).get(position).getType() == AlarmDevice.GD10) {
                switchFragmentTo(fragmentGD10);
            }else {
                switchFragmentTo(fragmentM52);
            }
        });

        DrawerLayout drawer = binding.drawerLayout;
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, findViewById(R.id.toolbar), R.string.content_des, R.string.content_des);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }
    private void switchFragmentTo(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(binding.content.fragmentHolder.getId(),fragment);
        ft.commit();
    }
    @Override
    public boolean onSupportNavigateUp() {
        return super.onSupportNavigateUp();
    }
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }
    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        Utils.createAlertDialog(MainActivity.this, "آیا می\u200Cخواهید از نرم افزار خارج شوید؟", "بله", "خیر", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}