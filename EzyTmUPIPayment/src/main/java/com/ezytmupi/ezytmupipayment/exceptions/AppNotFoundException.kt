package com.ezytmupi.ezytmupipayment.exceptions

class AppNotFoundException(appPackage: String?) : Exception("""
    No UPI app${appPackage?.let { " with package name '$it'" } ?: ""} exists on this device to perform this transaction.
""".trimIndent())