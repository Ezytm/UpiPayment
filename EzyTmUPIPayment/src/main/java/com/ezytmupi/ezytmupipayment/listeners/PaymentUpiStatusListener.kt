package com.ezytmupi.ezytmupipayment.listeners

import com.ezytmupi.ezytmupipayment.models.TransactionDetails

interface PaymentUpiStatusListener {
	fun onTransactionCompleted(transactionDetails: TransactionDetails)
	fun onTransactionCancelled()
}