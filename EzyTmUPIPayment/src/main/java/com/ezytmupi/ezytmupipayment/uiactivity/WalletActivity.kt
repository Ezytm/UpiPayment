package com.ezytmupi.ezytmupipayment.uiactivity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.ezytmupi.ezytmupipayment.Network.CommonUrl
import com.ezytmupi.ezytmupipayment.Network.IGoogleApi
import com.ezytmupi.ezytmupipayment.R
import com.ezytmupi.ezytmupipayment.Singleton
import com.ezytmupi.ezytmupipayment.models.*
import com.google.gson.Gson
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class WalletActivity : AppCompatActivity() {
    private lateinit var wallet: WalletValue
    lateinit var mservice: IGoogleApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wallet)

        mservice = CommonUrl.getGoogleApi()
        wallet = (intent.getSerializableExtra(EXTRA_KEY_WALLET) as WalletValue?)
                ?: throw IllegalStateException("Unable to parse payment details")

        walletResponse()


    }

    private fun walletResponse() {
        val loginCall: Call<WalletResponse> = mservice.WalletResponse(wallet.userid, wallet.UToken, wallet.VendorUpiID, wallet.OurRefID,
                wallet.RetailerUpiID, wallet.UpiResponse, wallet.UpiTxnStatus, wallet.amount)
        loginCall.enqueue(object : Callback<WalletResponse> {
            override fun onResponse(call: Call<WalletResponse>, response: Response<WalletResponse>) {
                if (response != null) {
                    val jsonobject: JSONObject = JSONObject(Gson().toJson(response.body()))

                    if (jsonobject.getString("ERROR").equals("0")) {

                        val transactionDetails = response.body()
                        callbackTransactionCompleted(transactionDetails!!)
                    } else if (jsonobject.getString("ERROR").equals("5")) {
                        val transactionDetails = response.body()
                        callbackTransactionCompleted(transactionDetails!!)
                        finish()
                    } else {

                    }
                }
                else {
                    Log.e("check", "  server error       " + response)
                }
            }

            override fun onFailure(call: Call<WalletResponse>, t: Throwable) {
                Log.e("check", "  error       " + t.message)
            }
        })
    }



    @JvmSynthetic
    internal fun callbackTransactionCancelled() {
        Singleton.listener?.onTransactionCancelled()
    }

    @JvmSynthetic
    internal fun callbackTransactionCompleted(transactionDetails: WalletResponse) {
        Singleton.listener?.onwalletCompleted(transactionDetails)
    }

    companion object {
        const val TAG = "WalletValue"
        const val PAYMENT_REQUEST = 4400
        const val EXTRA_KEY_WALLET = "Wallet"
    }
}