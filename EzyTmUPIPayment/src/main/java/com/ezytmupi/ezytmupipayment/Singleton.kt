package com.ezytmupi.ezytmupipayment

import com.ezytmupi.ezytmupipayment.listener.PaymentStatusListener

internal object Singleton {
	@set:JvmSynthetic
	@get:JvmSynthetic
	internal var listener: PaymentStatusListener? = null
}