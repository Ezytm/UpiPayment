package com.ezytmupi.ezytmupipayment

import com.ezytmupi.ezytmupipayment.listeners.PaymentUpiStatusListener

internal object Singleton {
	@set:JvmSynthetic
	@get:JvmSynthetic
	internal var listener: PaymentUpiStatusListener? = null
}