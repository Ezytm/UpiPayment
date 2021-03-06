package com.ezytmupi.ezytmupipayment.uiactivity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.telephony.TelephonyManager
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
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
import java.net.InetAddress
import java.net.NetworkInterface
import java.util.*

class PaymentUpiActivity : AppCompatActivity() {

	lateinit var payment: PaymentUpi
	lateinit var tvtext: TextView
	var imei = ""
	var ipaddress = ""
	var venderupiId = ""
	var ourrefid = ""
	lateinit var mservice: IGoogleApi
	private lateinit var wallet: WalletRequestValue
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_upipay)
		tvtext = findViewById(R.id.tvtext)

		wallet = (intent.getSerializableExtra(PaymentUpiActivity.EXTRA_KEY_PAYMENTREQUEST) as WalletRequestValue?)
				?: throw IllegalStateException("Unable to parse payment details")

		getimei()
		ipaddress = getIPAddress(true)
		Log.e("check", "       " + imei + "   yy   " + ipaddress)
		if(imei.equals("")|| imei ==null|| imei.isEmpty()){
			imei = ""
		}
		if(ipaddress.equals("")|| ipaddress ==null|| ipaddress.isEmpty()){
			ipaddress= ""
		}
		WalletRequest(imei, ipaddress)
	}


	fun getimei() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
			imei = Settings.Secure.getString(
					applicationContext.contentResolver,
					Settings.Secure.ANDROID_ID)
		} else {
			val mTelephony = applicationContext.getSystemService(TELEPHONY_SERVICE) as TelephonyManager
			if (ActivityCompat.checkSelfPermission(this@PaymentUpiActivity, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {

				// here to request the missing permissions, and then overriding
				//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
				//                                          int[] grantResults)
				// to handle the case where the user grants the permission. See the documentation
				// for Activity#requestPermissions for more details.
				return
			}
			if (mTelephony.deviceId != null) {
				imei = mTelephony.deviceId
			} else {
				imei = Settings.Secure.getString(
						applicationContext.contentResolver,
						Settings.Secure.ANDROID_ID)
			}
		}
	}

	fun getIPAddress(useIPv4: Boolean): String {
		try {
			val interfaces: List<NetworkInterface> = Collections.list(NetworkInterface.getNetworkInterfaces())
			for (intf in interfaces) {
				val addrs: List<InetAddress> = Collections.list(intf.inetAddresses)
				for (addr in addrs) {
					if (!addr.isLoopbackAddress) {
						val sAddr = addr.hostAddress
						//boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
						val isIPv4 = sAddr.indexOf(':') < 0
						if (useIPv4) {
							if (isIPv4) return sAddr
						} else {
							if (!isIPv4) {
								val delim = sAddr.indexOf('%') // drop ip6 zone suffix
								return if (delim < 0) sAddr.toUpperCase() else sAddr.substring(0, delim).toUpperCase()
							}
						}
					}
				}
			}
		} catch (ignored: Exception) {
		} // for now eat exceptions
		return ""
	}

	private fun WalletRequest(imei1: String, ipaddress1: String) {

		//Log.e("check", "        " + imei1 + "       ccc           " + ipaddress1)

		mservice = CommonUrl.getGoogleApi()

		val loginCall: Call<WalletRequestResponse> = mservice.WalletRequest(wallet.userid, wallet.UToken, wallet.amount, wallet.ClientRefId,
				wallet.RetailerUserID, wallet.CustomerName, wallet.RetailerUpiID, imei1, ipaddress1)

		loginCall.enqueue(object : Callback<WalletRequestResponse> {

			override fun onResponse(call: Call<WalletRequestResponse>, response: Response<WalletRequestResponse>) {

				if (response != null) {

					val jsonobject: JSONObject = JSONObject(Gson().toJson(response.body()))

					Log.e("jsonobject", "              " + jsonobject)

					if (jsonobject.getString("ERROR").equals("0")) {

						val transactionDetails = response.body()
						if (response.body()!!.VendorUpiID == null || response.body()!!.VendorUpiID!!.isEmpty() || response.body()!!.VendorUpiID.equals("")) {

						} else {
							venderupiId = response.body()!!.VendorUpiID!!
							ourrefid = response.body()!!.OurRefID!!!!
						}

						payment = PaymentUpi(
								currency = "INR",
								vpa = response.body()!!.VendorUpiID!!,
								name = response.body()!!.PaymentMode!!!!,
								payeeMerchantCode = null,
								txnId = response.body()!!.OurRefID!!,
								txnRefId = response.body()!!.OurRefID!!!!,
								description = response.body()!!.PaymentMode!!!!,
								amount = wallet.amount!!,
								defaultPackage = wallet.defaultPackage
						)

						upicall(response.body()!!.VendorUpiID!!, response.body()!!.OurRefID!!, wallet.amount!!, response.body()!!.PaymentMode!!, response.body()!!.Name.toString())
						//	callbackTransactionCompleted(transactionDetails!!)
					} else if (jsonobject.getString("ERROR").equals("5")) {
						val transactionDetails = response.body()
						//	callbackTransactionCompleted(transactionDetails!!)
						//finish()
					} else {

					}

				} else {
					Log.e("check", "  server error       " + response)
				}

			}

			override fun onFailure(call: Call<WalletRequestResponse>, t: Throwable) {
				Log.e("check", "  error       " + t.message)
			}

		})

	}

	private fun upicall(upiid: String, txnid: String, amt: String, paymentmode: String, name: String){

//		payment = (this@PaymentUpiActivity.intent.getSerializableExtra(PaymentUpiActivity.EXTRA_KEY_PAYMENT) as PaymentUpi?)
//		?: throw IllegalStateException("Unable to parse payment details")

		// Set Parameters for UPI
		val paymentUri = Uri.Builder().apply {
			with(payment) {
				scheme("upi").authority("pay")
				appendQueryParameter("pa", upiid)
				appendQueryParameter("pn", name)
				appendQueryParameter("tid", txnid)
				payeeMerchantCode?.let { appendQueryParameter("mc", it) }
				appendQueryParameter("tr", txnid)
				appendQueryParameter("tn", name)
				appendQueryParameter("am", amt)
				appendQueryParameter("cu", "INR")
			}
		}.build()

		// Set Data Intent
		val paymentIntent = Intent(Intent.ACTION_VIEW).apply {
			data = paymentUri

			// Check for Default package
			payment.defaultPackage?.let {
				`package` = it
			}
		}

		// Show Dialog to user
		val appChooser = Intent.createChooser(paymentIntent, "Pay using")

		// Check if other UPI apps are exists or not.
		if (paymentIntent.resolveActivity(packageManager) != null) {
			startActivityForResult(appChooser, PAYMENT_REQUEST)
		} else {
			//Toast.makeText(this, "No UPI app found! Please Install to Proceed!", Toast.LENGTH_SHORT).show()
			throwOnAppNotFound()

		}
	}


	public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		super.onActivityResult(requestCode, resultCode, data)
		if (requestCode == PAYMENT_REQUEST) {
			if (data != null) {
				// Get Response from activity intent
				val response = data.getStringExtra("response")

				if (response == null) {

					callbackTransactionCancelled()

				} else {
					val transactionDetails = getTransactionDetails(response)

					// Update Listener onTransactionCompleted()
				//	Toast.makeText(this@PaymentUpiActivity,""+transactionDetails, Toast.LENGTH_LONG).show()
					callbackTransactionCompleted(transactionDetails)
//					runCatching {
//						// Get transactions details from response.
//
//					}.getOrElse {
//
//						callbackTransactionCompleted(transactionDetails)
//					//	callbackTransactionCancelled()
//					}
				}
			} else {

				callbackTransactionCancelled()
			}
			finish()
		}
	}

	// Make TransactionDetails object from response string
	@JvmSynthetic
	internal fun getTransactionDetails(response: String): TransactionDetails {
		return with(getMapFromQuery(response)) {
			TransactionDetails(
					transactionId = get("txnId"),
					responseCode = get("responseCode"),
					approvalRefNo = get("ApprovalRefNo"),
					transactionRefId = get("txnRef"),
					amount = payment.amount,
					transactionStatus = get("Status")?.toUpperCase(Locale.getDefault())
							?: TransactionStatus.FAILURE.name

			)
		}
	}

	@JvmSynthetic
	internal fun getMapFromQuery(queryString: String): Map<String, String> {
		val map = mutableMapOf<String, String>()
		val keyValuePairs = queryString
				.split("&")
				.map { param ->
					param.split("=").let { Pair(it[0], it[1]) }
				}
		map.putAll(keyValuePairs)
		return map
	}

	@JvmSynthetic
	internal fun throwOnAppNotFound() {
		callbackappnotfoundCancelled()
		finish()
		//throw AppNotFoundException(payment.defaultPackage)
	}

	@JvmSynthetic
	internal fun callbackTransactionCancelled() {
		walletResponse("0", "0", "Cancelled by user", "0")
		Singleton.listener?.onTransactionCancelled()
	}

	@JvmSynthetic
	internal fun callbackappnotfoundCancelled() {
		Singleton.listener?.onappnotfoundCancelled()
	}

	@JvmSynthetic
	internal fun callbackTransactionCompleted(transactionDetails: TransactionDetails) {
		//Singleton.listener?.onTransactionCompleted(transactionDetails)
		val res:String = transactionDetails.toString()
		var bankrefnumber:String


		if(transactionDetails.approvalRefNo==null||transactionDetails.approvalRefNo.equals("")){
			bankrefnumber = ""
			walletResponse(transactionDetails.transactionId!!, res, transactionDetails.transactionStatus!!, bankrefnumber)
		}else{
			walletResponse(transactionDetails.transactionId!!, res, transactionDetails.transactionStatus!!, transactionDetails.approvalRefNo!!)
		}


	}

	private fun walletResponse(txn: String, res: String, status: String, bankrefnumber: String) {
		tvtext.text = res+"     1     "+wallet.RetailerUpiID+"   2     "+venderupiId+"     3       "+bankrefnumber
		val loginCall: Call<WalletResponse> = mservice.WalletResponse(wallet.userid, wallet.UToken, wallet.amount, venderupiId, ourrefid,
				wallet.RetailerUpiID, res, status, bankrefnumber)

		loginCall.enqueue(object : Callback<WalletResponse> {
			override fun onResponse(call: Call<WalletResponse>, response: Response<WalletResponse>) {
				if (response != null) {
					val jsonobject: JSONObject = JSONObject(Gson().toJson(response.body()))

					if (jsonobject.getString("ERROR").equals("0")) {
						val transactionDetails = response.body()
						callbackTransactionCompleted(transactionDetails!!)
						finish()
					} else if (jsonobject.getString("ERROR").equals("5")) {
						val transactionDetails = response.body()
						callbackTransactionCompleted(transactionDetails!!)
						finish()
					} else {
						val transactionDetails = response.body()
						callbackTransactionCompleted(transactionDetails!!)
						finish()
					}
				} else {
					Log.e("check", "  server error       " + response)
				}
			}

			override fun onFailure(call: Call<WalletResponse>, t: Throwable) {
				Log.e("check", "  error       " + t.message)
			}
		})
	}

	@JvmSynthetic
	internal fun callbackTransactionCompleted(transactionDetails: WalletResponse) {
		Singleton.listener?.onwalletCompleted(transactionDetails)
	}

	companion object {
		const val TAG = "PaymentUiActivity"
		const val PAYMENT_REQUEST = 4400
		const val EXTRA_KEY_PAYMENTREQUEST = "payment"
		const val EXTRA_KEY_PAYMENT = "payment"
	}

}