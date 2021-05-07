package com.ezytmupi.ezytmupipayment.listener

import com.ezytmupi.ezytmupipayment.model.TransactionDetails

interface PaymentStatusListener {
	fun onTransactionCompleted(transactionDetails: TransactionDetails)
	fun onTransactionCancelled()
}