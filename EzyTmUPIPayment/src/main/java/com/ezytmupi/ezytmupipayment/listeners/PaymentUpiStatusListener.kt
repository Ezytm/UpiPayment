package com.ezytmupi.ezytmupipayment.listeners

import com.ezytmupi.ezytmupipayment.models.TransactionDetails
import com.ezytmupi.ezytmupipayment.models.WalletResponse

interface PaymentUpiStatusListener  {
	fun onTransactionCompleted(transactionDetails: TransactionDetails)
	fun onwalletCompleted(transactionDetails: WalletResponse)
	fun onTransactionCancelled()

	fun onappnotfoundCancelled()
}