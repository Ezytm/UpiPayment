package com.ezytmupi.ezytmupi;

import androidx.appcompat.app.AppCompatActivity;


import android.annotation.SuppressLint;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;


import com.ezytmupi.ezytmupipayment.EzytmUpiPayment;

import com.ezytmupi.ezytmupipayment.listeners.PaymentStatusListener;
import com.ezytmupi.ezytmupipayment.models.PaymentApp;
import com.ezytmupi.ezytmupipayment.model.TransactionDetails;


import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.Random;



public class MainActivity extends AppCompatActivity implements PaymentStatusListener {

    private ImageView imageView;

    private TextView statusView;

    private Button payButton;

    private RadioGroup radioAppChoice;

    private EditText fieldPayeeVpa;
    private EditText fieldPayeeName;
    private EditText fieldPayeeMerchantCode;
    private EditText fieldTransactionId;
    private EditText fieldTransactionRefId;
    private EditText fieldDescription;
    private EditText fieldAmount;




    private EzytmUpiPayment easyUpiPayment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();

        payButton.setOnClickListener(v -> pay());
    }

    private void initViews() {
        imageView = findViewById(R.id.imageView);
        statusView = findViewById(R.id.textView_status);
        payButton = findViewById(R.id.button_pay);

        String transactionId = "TID" + System.currentTimeMillis();

        radioAppChoice = findViewById(R.id.radioAppChoice);
    }

    @SuppressLint("NonConstantResourceId")
    private void pay() {


        Calendar c = Calendar.getInstance();
        int time = c.get(Calendar.MILLISECOND) * 60 * 60 * 60;
        String transactionId = "TID" + time + generateID();
        PaymentApp paymentApp = PaymentApp.ALL;
        EzytmUpiPayment.Builder builder = new EzytmUpiPayment.Builder(MainActivity.this)
                .with(paymentApp)
                .setPayeeVpa("in.kishanchoudhary@okicici")
                .setPayeeName("name")
                .setTransactionId(transactionId)
                .setTransactionRefId(transactionId)
                .setDescription("remak")
                .setAmount(1 + ".00");
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

//
//        // START PAYMENT INITIALIZATION
//        EzytmUpiPayment.Builder builder = new EzytmUpiPayment.Builder(this)
//                .with(paymentApp)
//                .setPayeeVpa("in.kishanchoudhary@okicici")
//                .setPayeeName("payment")
//                .setTransactionId(transactionId)
//                .setTransactionRefId(transactionRefId)
//              //  .setPayeeMerchantCode(payeeMerchantCode)
//                .setDescription("test")
//                .setAmount("1.00");
//        // END INITIALIZATION
//
//        try {
//            // Build instance
//            ezytmUpiPayment = builder.build();
//
//            // Register Listener for Events
//            ezytmUpiPayment.setPaymentStatusListener(this);
//
//            // Start payment / transaction
//            ezytmUpiPayment.startPayment();
//        } catch (Exception exception) {
//            exception.printStackTrace();
//            toast("Error: " + exception.getMessage());
//        }
    }

//    @Override
//    public void onTransactionCompleted(TransactionDetails transactionDetails) {
//        // Transaction Completed
//        Log.d("TransactionDetails", transactionDetails.toString());
//        statusView.setText(transactionDetails.toString());
//
//        switch (transactionDetails.getTransactionStatus()) {
//            case SUCCESS:
//                onTransactionSuccess();
//                break;
//            case FAILURE:
//                onTransactionFailed();
//                break;
//            case SUBMITTED:
//                onTransactionSubmitted();
//                break;
//        }
//    }

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
}