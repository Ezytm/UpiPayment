package com.ezytmupi.ezytmupipayment.models

data class WalletRequestResponse(
        val ERROR: String?,
        val STATUS: String?,
        val MESSAGE: String?,
        val VendorUpiID: String?,
        val OurRefID: String?,
        val PaymentMode: String?,
        val Name:String?,
        val PaymentQRCode: String?
)
