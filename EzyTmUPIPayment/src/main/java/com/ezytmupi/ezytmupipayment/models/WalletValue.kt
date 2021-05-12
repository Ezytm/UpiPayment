package com.ezytmupi.ezytmupipayment.models

import java.io.Serializable

data class WalletValue(
		var userid: String,
		var UToken: String,
		var VendorUpiID: String,
		var OurRefID: String?,
		var RetailerUpiID: String,
		var UpiResponse: String,
		var UpiTxnStatus: String,
		var amount: String
) : Serializable