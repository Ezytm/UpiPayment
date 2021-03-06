package com.ezytmupi.ezytmupipayment.models

data class TransactionDetails(
		val transactionId: String?,
		val responseCode: String?,
		val approvalRefNo: String?,
		val transactionStatus: String?,
		val transactionRefId: String?,
		val amount: String?
)

enum class TransactionStatus {
	FAILURE, SUCCESS, SUBMITTED
}