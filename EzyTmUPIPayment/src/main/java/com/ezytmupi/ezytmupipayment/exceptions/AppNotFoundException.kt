package com.ezytmupi.ezytmupipayment.exception

class AppNotFoundException(appPackage: String?) : Exception(
        "No UPI app exists on this device to perform this transaction.")