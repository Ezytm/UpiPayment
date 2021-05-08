package com.ezytmupi.ezytmupipayment

import com.ezytmupi.ezytmupipayment.listeners.PaymentStatusListener

internal object Singleton {
	@set:JvmSynthetic
	@get:JvmSynthetic
	internal var listener: PaymentStatusListener? = null
}