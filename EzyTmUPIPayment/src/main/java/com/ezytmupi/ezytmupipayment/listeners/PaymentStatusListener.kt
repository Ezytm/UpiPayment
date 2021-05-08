package com.ezytmupi.ezytmupipayment.listeners

import com.ezytmupi.ezytmupipayment.model.TransactionDetails

interface PaymentStatusListener {
	fun onTransactionCompleted(transactionDetails: TransactionDetails)
	fun onTransactionCancelled()
}