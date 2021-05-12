package com.ezytmupi.ezytmupipayment.models

data class WalletResponse(
        val ERROR: String?,
        val STATUS: String?,
        val MESSAGE: String?,
        val WalletStatus: String?,
        val WalletRefID: String?,
        val ImpsRefID: String?,
        val PaymentMsg: String?
)