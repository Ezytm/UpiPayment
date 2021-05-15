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
	lateinit var mservice: IGoogleApi
	private lateinit var wallet: WalletRequestValue
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_upipay)

		tvtext = findViewById(R.id.tvtext)

		wallet = (intent.getSerializableExtra(PaymentUpiActivity.EXTRA_KEY_PAYMENTREQUEST) as WalletRequestValue?)
				?: throw IllegalStateException("Unable to parse payment details")

		WalletRequest()
		getimei()
		ipaddress = getIPAddress(true)
		Log.e("check", "       " + imei+"   yy   "+ipaddress)


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

	private fun WalletRequest() {

		if(imei.equals("")||imei==null){
			imei = ""
		}
		if(ipaddress.equals("")||ipaddress==null||ipaddress.isEmpty()){
			ipaddress = ""
		}
		mservice = CommonUrl.getGoogleApi()
		Log.e("check", "  Kishan11       " + wallet.userid + "  " + wallet.UToken)
		val loginCall: Call<WalletRequestResponse> = mservice.WalletRequest(wallet.userid, wallet.UToken, wallet.amount, wallet.ClientRefId,
				wallet.RetailerUserID,wallet.CustomerName, wallet.RetailerUpiID, imei, ipaddress)

		loginCall.enqueue(object : Callback<WalletRequestResponse> {

			override fun onResponse(call: Call<WalletRequestResponse>, response: Response<WalletRequestResponse>) {

				if (response != null) {

					val jsonobject: JSONObject = JSONObject(Gson().toJson(response.body()))

					Log.e("jsonobject","              "+jsonobject)

					if (jsonobject.getString("ERROR").equals("0")) {

						val transactionDetails = response.body()
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
					}

					else if (jsonobject.getString("ERROR").equals("5")) {
						val transactionDetails = response.body()
						//	callbackTransactionCompleted(transactionDetails!!)
						//finish()
					}

					else {

					}

				}
				else {
					Log.e("check", "  server error       " + response)
				}

			}

			override fun onFailure(call: Call<WalletRequestResponse>, t: Throwable) {
				Log.e("check", "  error       " + t.message)
			}

		})

	}

	private fun upicall(upiid: String, txnid: String, amt: String, paymentmode: String,name:String){

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
			finish()
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
					Log.d(TAG, "Payment Response is null")
				} else {
					runCatching {
						// Get transactions details from response.
						val transactionDetails = getTransactionDetails(response)

						// Update Listener onTransactionCompleted()
						callbackTransactionCompleted(transactionDetails)
					}.getOrElse {
						callbackTransactionCancelled()
					}
				}
			} else {
				Log.e(TAG, "Intent Data is null. User cancelled")
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
					transactionStatus = TransactionStatus.valueOf(
							get("Status")?.toUpperCase(Locale.getDefault())
									?: TransactionStatus.FAILURE.name
					)
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
		Log.e("check", "  ef       " + payment.defaultPackage)
		Log.e(TAG, "No UPI app found on device.")
		callbackappnotfoundCancelled()
		//throw AppNotFoundException(payment.defaultPackage)
	}

	@JvmSynthetic
	internal fun callbackTransactionCancelled() {
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
		walletResponse(transactionDetails.transactionId!!, res)


	}


	private fun walletResponse(txn: String, res: String) {
		tvtext.text = res+"       "+wallet.RetailerUpiID+"        "+wallet.ClientRefId
		val loginCall: Call<WalletResponse> = mservice.WalletResponse(wallet.userid, wallet.UToken, wallet.amount, wallet.RetailerUpiID, wallet.ClientRefId,
				wallet.RetailerUpiID, res, txn)
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