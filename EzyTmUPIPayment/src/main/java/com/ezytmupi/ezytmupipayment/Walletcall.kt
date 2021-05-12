package com.ezytmupi.ezytmupipayment

import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.annotation.VisibleForTesting
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.ezytmupi.ezytmupipayment.exception.AppNotFoundException
import com.ezytmupi.ezytmupipayment.listeners.PaymentUpiStatusListener


import com.ezytmupi.ezytmupipayment.models.WalletValue
import com.ezytmupi.ezytmupipayment.uiactivity.WalletActivity


class Walletcall constructor(private val mActivity: Activity, private val mwallet: WalletValue) {

    @VisibleForTesting
    @get:JvmSynthetic
    internal lateinit var activityLifecycleObserver: LifecycleObserver

    init {
        if (mActivity is AppCompatActivity) {
            activityLifecycleObserver = object : LifecycleObserver {
                @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
                fun onDestroyed() {
                    Singleton.listener = null
                }
            }
            registerLifecycleObserver(mActivity)
        } else {
        }
    }


    fun startPayment() {
        val payIntent = Intent(mActivity, WalletActivity::class.java).apply {
            putExtra(WalletActivity.EXTRA_KEY_WALLET,mwallet)
        }

        // Start Payment Activity
        mActivity.startActivity(payIntent)
    }

    fun setPaymentStatusListener(mListener: PaymentUpiStatusListener) {
        Singleton.listener = mListener
    }

    fun removePaymentStatusListener() {
        Singleton.listener = null
    }

    private fun registerLifecycleObserver(mLifecycleOwner: LifecycleOwner) {
        mLifecycleOwner.lifecycle.addObserver(activityLifecycleObserver)
    }

    /**
     * Builder for [EzytmUpiPayment].
     */
    class Builder(private val activity: Activity) {

        @set:JvmSynthetic
        var userid: String? = null

        @set:JvmSynthetic
        var Token: String? = null

        @set:JvmSynthetic
        var VendorUpiID: String? = null

        @set:JvmSynthetic
        var OurRefID: String? = null

        @set:JvmSynthetic
        var RetailerUpiID: String? = null

        @set:JvmSynthetic
        var UpiResponse: String? = null

        @set:JvmSynthetic
        var UpiTxnStatus: String? = null

        @set:JvmSynthetic
        var amount: String? = null

        fun setuserid(id: String): Builder = apply { userid = id }

        fun setToken(token: String): Builder = apply { Token = token }

        fun setVendorUpiID(vendorupi: String): Builder = apply { this.VendorUpiID = vendorupi }

        fun setOurRefID(refid: String): Builder = apply { this.OurRefID = refid }

        fun setRetailerUpiID(retailerupiid: String): Builder = apply { this.RetailerUpiID = retailerupiid }

        fun setUpiResponse(upiresponse: String): Builder = apply { this.UpiResponse = upiresponse }

        fun setUpiTxnStatus(upitxnstatus: String): Builder = apply { this.UpiTxnStatus = upitxnstatus }

        fun setAmount(amount: String): Builder = apply { this.amount = amount }

        /**
         * Build the [EzytmUpiPayment] object.
         */
        @Throws(IllegalStateException::class, AppNotFoundException::class)
        fun build(): Walletcall {
            validate()

            val wallet = WalletValue(
                    userid = userid!!,
                    UToken= Token!!,
                    VendorUpiID = VendorUpiID!!,
                    OurRefID = OurRefID!!,
                    RetailerUpiID = RetailerUpiID!!,
                    UpiResponse = UpiResponse!!,
                    UpiTxnStatus = UpiTxnStatus!!,
                    amount = amount!!,
            )
            return Walletcall(activity, wallet)
        }

        private fun validate() {
            userid.run {
                checkNotNull(this) { "Must call userid() before build" }
                check(this.isNotBlank()) { "User ID Should be Valid!" }
            }

            VendorUpiID?.let {
                check(it.isNotBlank()) { "Vendor Upi Id Should be Valid!" }
            }

            OurRefID.run {
                checkNotNull(this) { "Must call OurRefID() before build" }
                check(this.isNotBlank()) { "Transaction ID Should be Valid!" }
            }

            RetailerUpiID.run {
                checkNotNull(this) { "Must call RetailerUpiID() before build" }
                check(this.isNotBlank()) { "RefId Should be Valid!" }
            }

            UpiTxnStatus.run {
                checkNotNull(this) { "Must call UpiTxnStatus() before build()." }
                check(this.isNotBlank()) { "Payee name Should be Valid!" }
            }

            amount.run {
                checkNotNull(this) { "Must call setAmount() before build()." }
                check(this.isNotBlank()) { "Amount should be valid positive number" }
            }

        }

    }

    companion object {
        const val TAG = "Walletcall"
    }
}

@Suppress("FunctionName")
@JvmSynthetic
fun Walletcall(activity: Activity, initializer: Walletcall.Builder.() -> Unit): Walletcall {
    return Walletcall.Builder(activity).apply(initializer).build()
}