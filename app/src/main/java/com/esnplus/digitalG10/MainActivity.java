package com.esnplus.digitalG10;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.esnplus.digitalG10.databinding.ActivityMainBinding;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;

import java.io.ByteArrayOutputStream;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private DbxClientV2 client;
    private static final String ACCESS_TOKEN = "uhzCoz1eewAAAAAAAAAAC5CQ4KzQuxLalhZ_nSzyj4k2HiVQb01qN5y9xpZnLNA0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.content.toolbar);
        checkForUpdate(false);

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

        binding.navView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.menu_about_us) {
                startActivity(new Intent(MainActivity.this, AboutUsActivity.class));
            }else if (id == R.id.menu_vote_bazaar){
                Intent intent = new Intent(Intent.ACTION_EDIT);
                intent.setData(Uri.parse("bazaar://details?id=com.digital_alarm.G10"));
                intent.setPackage("com.farsitel.bazaar");
                startActivity(intent);
            }else if (id == R.id.menu_update){
                if (Utils.isNetworkAvailable(MainActivity.this)){
                    Utils.showMessage("در حال برسی...",MainActivity.this);
                    checkForUpdate(true);
                }else {
                    Utils.showMessage("لطفا ابتدا دستگاه خود را به اینترنت متصل کنید!",MainActivity.this);
                }
            }
            drawer.closeDrawer(GravityCompat.START);
            return false;
        });
    }
    private void checkForUpdate(boolean isCheckByUser){
        if (Utils.isNetworkAvailable(MainActivity.this)) {
            DbxRequestConfig config = DbxRequestConfig.newBuilder("dropbox/digitalG10").build();
            client = new DbxClientV2(config, ACCESS_TOKEN);
            new Thread(() -> {
                try {
                    FileMetadata fileMetadata = (FileMetadata) client.files().getMetadata("/application/updateDigitalG10.txt");
                    new DownloadFileTask(client, new DownloadFileTask.Callback() {
                        @Override
                        public void onDownloadComplete(ByteArrayOutputStream bos) {
                            String str = bos.toString();
                            int latestVersionCode = Integer.parseInt(Utils.getLine(str, 1).split("=")[1]);
                            if (latestVersionCode != getResources().getInteger(R.integer.versionCode)) {
                                try {
                                    Utils.createAlertDialog(MainActivity.this, Utils.getLine(str, 3), "بله", "خیر", view -> {
                                        Intent intent = new Intent(Intent.ACTION_VIEW);
                                        intent.setData(Uri.parse(Utils.getLine(str, 2)));
                                        startActivity(intent);
                                    });
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }else {
                                if (isCheckByUser) {
                                    runOnUiThread(() -> Utils.showMessage("شما در حال حاضر از جدیدترین ورژن نرم افزار استفاده میکنید.", MainActivity.this));
                                }
                            }
                        }
                        @Override
                        public void onError(Exception e) {
                            System.out.println(e.getMessage());
                        }
                    }).execute(fileMetadata);
                } catch (DbxException dbxException) {
                    Log.e("Network Issue", dbxException.toString());
                }
            }).start();
        }
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