package com.ezytmupi.ezytmupi;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;


import android.Manifest;
import android.annotation.SuppressLint;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;


import com.ezytmupi.ezytmupipayment.EzytmUpiPayment;

import com.ezytmupi.ezytmupipayment.listeners.PaymentUpiStatusListener;

import com.ezytmupi.ezytmupipayment.models.TransactionDetails;
import com.ezytmupi.ezytmupipayment.models.PaymentApp;
import com.ezytmupi.ezytmupipayment.models.WalletResponse;


import org.jetbrains.annotations.NotNull;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Calendar;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Random;



public class MainActivity extends AppCompatActivity implements PaymentUpiStatusListener {

    private ImageView imageView;

    private TextView statusView;

    private Button payButton;

    private EditText eduserid;
    private EditText edtoken;
    private EditText edclientrefid;
    private EditText edupiid;
    private EditText edamount;

    String imei = "";
    String ipaddress = "";


    private EzytmUpiPayment easyUpiPayment;
    //private Walletcall walletcall;
    private static final int MY_CAMERA_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();

        payButton.setOnClickListener(v -> pay());
    }


    @SuppressLint("MissingPermission")
    private void initViews() {

        edupiid = findViewById(R.id.edupiid);
        eduserid = findViewById(R.id.eduserid);
        edclientrefid = findViewById(R.id.edclientrefid);
        edtoken = findViewById(R.id.edtoken);
        edamount = findViewById(R.id.edamount);

        eduserid.setText(getResources().getString(R.string.userid));
        edtoken.setText(getResources().getString(R.string.tokenid));
        edclientrefid.setText(getResources().getString(R.string.clientrefid));
        edupiid.setText(getResources().getString(R.string.upiId));
        edamount.setText("1");


        imageView = findViewById(R.id.imageView);
        statusView = findViewById(R.id.textView_status);
        payButton = findViewById(R.id.button_pay);

        String transactionId = "TID" + System.currentTimeMillis();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            imei = Settings.Secure.getString(
                    getApplicationContext().getContentResolver(),
                    Settings.Secure.ANDROID_ID);
        }
        else {
            final TelephonyManager mTelephony = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);

            if (ActivityCompat.checkSelfPermission(MainActivity.this,Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    Activity#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for Activity#requestPermissions for more details.
                return;
            }
            if (mTelephony.getDeviceId() != null) {
                imei = mTelephony.getDeviceId();
            } else {
                imei = Settings.Secure.getString(
                        getApplicationContext().getContentResolver(),
                        Settings.Secure.ANDROID_ID);
            }
        }


        ipaddress = getIPAddress(true);



    }

//    public String getLocalIpAddress() {
//        try {
//            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
//                NetworkInterface intf = en.nextElement();
//                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
//                    InetAddress inetAddress = enumIpAddr.nextElement();
//                    if (!inetAddress.isLoopbackAddress()) {
//                        String ip = Formatter.formatIpAddress(inetAddress.hashCode());
//
//                        return ip;
//                    }
//                }
//            }
//        } catch (SocketException ex) {
//          //  Log.e(TAG, ex.toString());
//        }
//        return null;
//    }

    public static String getIPAddress(boolean useIPv4) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress();
                        //boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
                        boolean isIPv4 = sAddr.indexOf(':')<0;

                        if (useIPv4) {
                            if (isIPv4)
                                return sAddr;
                        } else {
                            if (!isIPv4) {
                                int delim = sAddr.indexOf('%'); // drop ip6 zone suffix
                                return delim<0 ? sAddr.toUpperCase() : sAddr.substring(0, delim).toUpperCase();
                            }
                        }
                    }
                }
            }
        } catch (Exception ignored) { } // for now eat exceptions
        return "";
    }


    @SuppressLint("NonConstantResourceId")
    private void pay() {
        Calendar c = Calendar.getInstance();
        int time = c.get(Calendar.MILLISECOND) * 60 * 60 * 60;
        String transactionId = "TID" + time + generateID();
        PaymentApp paymentApp = PaymentApp.ALL;
        EzytmUpiPayment.Builder builder = new EzytmUpiPayment.Builder(MainActivity.this)
                .with(paymentApp)
                .setuserid(eduserid.getText().toString().trim())
                .setToken(edtoken.getText().toString().trim())
                .setClientRefId(edclientrefid.getText().toString().trim())
                .setRetailerUpiID(edupiid.getText().toString().trim())
                .setRetailerUserID("7976155877")
                .setPhoneInfo(imei)
                .setIPadd(ipaddress)
                .setAmount(edamount.getText().toString().trim() + ".00");
        //     END INITIALIZATION

        try {
            easyUpiPayment = builder.build();
            // Register Listener for Events
            easyUpiPayment.setPaymentStatusListener(this);
            // Start payment / transaction
            easyUpiPayment.startPayment();
        } catch (Exception exception) {
            exception.printStackTrace();
            toast("Error: " + exception.getMessage());
        }


    }

    @Override
    public void onappnotfoundCancelled() {
        // Payment Cancelled by User
        toast("No UPI app exists on this device to perform this transaction.");

    }

    @Override
    public void onTransactionCancelled() {
        // Payment Cancelled by User
        toast("Cancelled by user");
        imageView.setImageResource(R.drawable.ic_failed);
    }

    private void onTransactionSuccess() {
        // Payment Success
        toast("Success");
        imageView.setImageResource(R.drawable.ic_success);
    }

    private void onTransactionSubmitted() {
        // Payment Pending
        toast("Pending | Submitted");
        imageView.setImageResource(R.drawable.ic_success);
    }

    private void onTransactionFailed() {
        // Payment Failed
        toast("Failed");
        imageView.setImageResource(R.drawable.ic_failed);
    }

    private void toast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public long generateID() {
        Random rnd = new Random();
        char[] digits = new char[11];
        digits[0] = (char) (rnd.nextInt(9) + '1');
        for (int i = 1; i < digits.length; i++) {
            digits[i] = (char) (rnd.nextInt(10) + '0');
        }
        return Long.parseLong(new String(digits));
    }


    @Override
    public void onTransactionCompleted(@NotNull TransactionDetails transactionDetails) {

    }


    @Override
    public void onwalletCompleted(@NotNull WalletResponse transactionDetails) {
        Toast.makeText(MainActivity.this, ""+transactionDetails.getMESSAGE(), Toast.LENGTH_SHORT).show();
    }
}