package com.ezytmupi.ezytmupipayment.testutils

import com.ezytmupi.ezytmupipayment.EzytmUpiPayment
import com.ezytmupi.ezytmupipayment.listeners.PaymentUpiStatusListener
import com.ezytmupi.ezytmupipayment.models.PaymentUpi
import com.ezytmupi.ezytmupipayment.models.PaymentApp
import com.ezytmupi.ezytmupipayment.models.TransactionDetails

const val validVpa = "example@vpa"
const val invalidVpa = "emample"

const val validName = "John Doe"
const val invalidName = " "

const val validTransactionId = "dh829h38ry84y248yt98498"
const val invalidTransactionId = " "

const val validAmount = "11000.50"
const val invalidAmount1 = "-10000"
const val invalidAmount2 = "10000"
const val invalidAmount3 = ".1000"

const val validDescription = "Lorem"
const val invalidDescription = " "

fun validEasyUpiPaymentBuilderFixture(builder: EzytmUpiPayment.Builder) {
	builder.setPayeeVpa(validVpa)
		.setPayeeName(validName)
		.setTransactionId(validTransactionId)
		.setTransactionRefId(validTransactionId)
		.setDescription(validDescription)
		.setAmount(validAmount)
}

fun paymentStatusListener() = object : PaymentUpiStatusListener {
	override fun onTransactionCompleted(transactionDetails: TransactionDetails) {}
	override fun onTransactionCancelled() {}
}

fun paymentWith(paymentApp: PaymentApp? = null) = PaymentUpi(
	"INT",
	validVpa,
	validName,
	"",
	validTransactionId,
	validTransactionId,
	validDescription,
	validAmount,
	paymentApp?.packageName
)
