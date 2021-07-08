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
import com.ezytmupi.ezytmupipayment.EzytmUpiPayment.Builder
import com.ezytmupi.ezytmupipayment.exception.AppNotFoundException
import com.ezytmupi.ezytmupipayment.listeners.PaymentUpiStatusListener
import com.ezytmupi.ezytmupipayment.models.*
import com.ezytmupi.ezytmupipayment.uiactivity.PaymentUpiActivity


/**
 * Class to implement Easy UPI Payment
 * Use [Builder] to create a new instance.
 */
@Suppress("unused")
class EzytmUpiPayment constructor(private val mActivity: Activity, private val mPayment: WalletRequestValue) {

	@VisibleForTesting
	@get:JvmSynthetic
	internal lateinit var activityLifecycleObserver: LifecycleObserver

	init {
		if (mActivity is AppCompatActivity) {
			activityLifecycleObserver = object : LifecycleObserver {
				@OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
				fun onDestroyed() {
					Log.d(TAG, "onDestroyed")
					Singleton.listener = null
				}
			}

			registerLifecycleObserver(mActivity)
		} else {
			Log.w(TAG, """
                Current Activity isn't AppCompatActivity.
                You'll need to call EzytmUpiPayment#detachListener() to remove listener.
            """.trimIndent())
		}
	}

	/**
	 * Starts the payment transaction. Calling this method launches the Payment Menu
	 * and shows installed UPI apps in device and let user choose one of them to pay.
	 */
	fun startPayment() {
		// Create Payment Activity Intent
		val payIntent = Intent(mActivity, PaymentUpiActivity::class.java).apply {
			putExtra(PaymentUpiActivity.EXTRA_KEY_PAYMENTREQUEST, mPayment) }

		// Start Payment Activity
		mActivity.startActivity(payIntent)
	}




	/**
	 * Sets the PaymentStatusListener.
	 *
	 * @param mListener Implementation of PaymentStatusListener
	 */
	fun setPaymentStatusListener(mListener: PaymentUpiStatusListener) {
		Singleton.listener = mListener
	}

	/**
	 * Removes the [PaymentStatusListener] which is already registered.
	 */
	fun removePaymentStatusListener() {
		Singleton.listener = null
	}

	/**
	 * Registers lifecycle observer for [mLifecycleOwner]
	 */
	private fun registerLifecycleObserver(mLifecycleOwner: LifecycleOwner) {
		mLifecycleOwner.lifecycle.addObserver(activityLifecycleObserver)
	}

	/**
	 * Builder for [EzytmUpiPayment].
	 */
	class Builder(private val activity: Activity) {

		@set:JvmSynthetic
		var paymentApp: PaymentApp = PaymentApp.ALL

		@set:JvmSynthetic
		var userid: String? = null

		@set:JvmSynthetic
		var Token: String? = null

		@set:JvmSynthetic
		var ClientRefId: String? = null

		@set:JvmSynthetic
		var RetailerUserID: String? = null

		@set:JvmSynthetic
		var CustomerName: String? = null

		@set:JvmSynthetic
		var RetailerUpiID: String? = null

		@set:JvmSynthetic
		var amount: String? = null

		@JvmOverloads
		fun with(paymentApp: PaymentApp = PaymentApp.ALL): Builder = apply {
			this.paymentApp = paymentApp
		}

		fun setuserid(id: String): Builder = apply { userid = id }

		fun setToken(token: String): Builder = apply { Token = token }

		fun setClientRefId(clientRefId: String): Builder = apply { this.ClientRefId = clientRefId }

		fun setRetailerUserID(retailerUserID: String): Builder = apply { this.RetailerUserID = retailerUserID }

		fun setCustomerName(customername: String): Builder = apply { this.CustomerName = customername }

		fun setRetailerUpiID(retailerupiid: String): Builder = apply { this.RetailerUpiID = retailerupiid }

//		fun setPhoneInfo(phoneInfo: String): Builder = apply { this.PhoneInfo = phoneInfo }
//
//		fun setIPadd(iPadd: String): Builder = apply { this.IPadd = iPadd }

		fun setAmount(amount: String): Builder = apply { this.amount = amount }

		@Throws(IllegalStateException::class, AppNotFoundException::class)
		fun build(): EzytmUpiPayment {
			validate()

			val payment = WalletRequestValue(
					userid = userid!!,
					UToken = Token!!,
					ClientRefId = ClientRefId!!,
					RetailerUserID = RetailerUserID!!,
					CustomerName =CustomerName!!,
					RetailerUpiID = RetailerUpiID!!,
					amount = amount!!,
					defaultPackage = if (paymentApp != PaymentApp.ALL) paymentApp.packageName else null
			)
			return EzytmUpiPayment(activity, payment)
		}


		private fun validate() {
			if (paymentApp != PaymentApp.ALL) {
				if (!isPackageInstalled(paymentApp.packageName)) {
					throw AppNotFoundException(paymentApp.packageName)
				}
			}

			userid.run {
				checkNotNull(this) { "Must call setuserid() before build()." }
				check(this.isNotBlank()) { "User Id should be valid (For e.g. example@vpa)" }
			}

			Token.run {
				checkNotNull(this) { "Must call setToken() before build" }
				check(this.isNotBlank()) { "Token Should be Valid!" }
			}

			ClientRefId.run {
				checkNotNull(this) { "Must call setClientRefId() before build" }
				check(this.isNotBlank()) { "ClientRefId Should be Valid!" }
			}

			CustomerName.run {
				checkNotNull(this) { "Must call setCustomerName() before build()." }
				check(this.isNotBlank()) { "Customer name Should be Valid!" }
			}
			amount.run {
				checkNotNull(this) { "Must call setAmount() before build()." }
				check(this.matches("""\d+\.\d*""".toRegex())) {
					"Amount should be valid positive number and in decimal format (For e.g. 100.00)"
				}
			}


		}

		/**
		 * Check Whether UPI App is installed on device or not
		 *
		 * @return true if app exists, otherwise false.
		 */
		@VisibleForTesting
		@JvmSynthetic
		internal fun isPackageInstalled(packageName: String): Boolean = runCatching {
			activity.packageManager.getPackageInfo(packageName, 0)
			true
		}.getOrDefault(false)
	}

	companion object {
		const val TAG = "EzytmUpiPayment"
	}
}

@Suppress("FunctionName")
@JvmSynthetic
fun EzytmUpiPayment(activity: Activity, initializer: Builder.() -> Unit): EzytmUpiPayment {
	return Builder(activity).apply(initializer).build()
}