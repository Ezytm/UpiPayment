package com.ezytmupi.ezytmupi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.format.Formatter;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Splash extends AppCompatActivity {
    Snackbar snackbar, snackbar1;
    LinearLayout rlmain;
    ImageView logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        logo= findViewById(R.id.logo);

        rlmain= findViewById(R.id.rlmain);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            permission();
        }else {
            functionStart();
        }


    }

    private void functionStart() {

        Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent intent=new Intent(Splash.this,MainActivity.class);
                startActivity(intent);
                finish();

            }
        },2500);

    }

    public void permission() {
        if (ActivityCompat.checkSelfPermission(Splash.this, Manifest.permission.READ_PHONE_STATE ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(Splash.this,Manifest.permission.WRITE_EXTERNAL_STORAGE ) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(Splash.this,Manifest.permission.CALL_PHONE ) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(Splash.this,Manifest.permission.ACCESS_WIFI_STATE ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(Splash.this,Manifest.permission.ACCESS_NETWORK_STATE ) != PackageManager.PERMISSION_GRANTED) {
            snackbar=Snackbar.make(rlmain,"Without permission Unable to use this Application.\nAre you sure you want to deny this permission",Snackbar.LENGTH_INDEFINITE);
            snackbar.show();
            ActivityCompat.requestPermissions(Splash.this, new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_CONTACTS,Manifest.permission.CALL_PHONE,Manifest.permission.ACCESS_WIFI_STATE,Manifest.permission.ACCESS_NETWORK_STATE}, 1);
        }else {

            functionStart();
        }

    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==1){

            if (ActivityCompat.checkSelfPermission(Splash.this,Manifest.permission.READ_PHONE_STATE ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(Splash.this,Manifest.permission.WRITE_EXTERNAL_STORAGE ) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(Splash.this,Manifest.permission.CALL_PHONE ) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(Splash.this,Manifest.permission.ACCESS_WIFI_STATE ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(Splash.this,Manifest.permission.ACCESS_NETWORK_STATE ) != PackageManager.PERMISSION_GRANTED) {

                for (int i=0;i<permissions.length;i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_DENIED)
                    {
                        boolean showRationale=shouldShowRequestPermissionRationale(permissions[i]);
                        if (!showRationale){
                            // user also CHECKED "never ask again"
                            // you can either enable some fall back,
                            // disable features of your app
                            // or open another dialog explaining
                            // again the permission and directing to
                            // the app setting
                            snackbar.dismiss();
                            snackbar1= snackbar=Snackbar.make(rlmain,"Without permission Unable to use \nthis Application Feature....",Snackbar.LENGTH_INDEFINITE);
                            snackbar1.setAction("SHOW", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    startInstalledAppDetailsActivity(Splash.this);
                                }
                            });
                            snackbar1.setActionTextColor(Color.parseColor("#26499A"));
                            snackbar1.show();

                        }else {

                            permission();
                        }

                    }
                }
            }else {
                snackbar.dismiss();
                permission();
            }
        }
    }

    public static void startInstalledAppDetailsActivity(Activity context) {
        if (context == null) {
            return;
        }
        final Intent i = new Intent();
        i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        i.addCategory(Intent.CATEGORY_DEFAULT);
        i.setData(Uri.parse("package:" + context.getPackageName()));
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        context.startActivity(i);
    }

}
