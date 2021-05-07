package com.ezytmupi.ezytmupipayment.testutils

import org.mockito.Mockito

inline fun <reified T> mock() = Mockito.mock(T::class.java)