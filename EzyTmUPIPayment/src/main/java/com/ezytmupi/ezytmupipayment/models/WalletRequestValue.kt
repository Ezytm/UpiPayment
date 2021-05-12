package com.ezytmupi.ezytmupipayment.models

import java.io.Serializable

data class WalletRequestValue(
		var userid: String,
		var UToken: String,
		var ClientRefId: String,
		var RetailerUserID: String?,
		var RetailerUpiID: String,
		var PhoneInfo: String,
		var IPadd: String,
		var amount: String,
		var defaultPackage: String?
) : Serializable