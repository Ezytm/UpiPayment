package com.ezytmupi.ezytmupipayment.models

import java.io.Serializable

data class PaymentUpi(
		var currency: String,
		var vpa: String,
		var name: String,
		var payeeMerchantCode: String?,
		var txnId: String,
		var txnRefId: String,
		var description: String,
		var amount: String,
		var defaultPackage: String?
) : Serializable