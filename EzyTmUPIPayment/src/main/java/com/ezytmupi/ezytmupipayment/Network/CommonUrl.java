package com.ezytmupi.ezytmupipayment.Network;

public class CommonUrl {
   // public static final String baseUrl="http://www.codingwithjks.tech/data.php/";
    public static String baseUrl = "https://ezytm.net/Upi/webservice/";
    public static IGoogleApi getGoogleApi(){
        return RetrofitClient.getClient(baseUrl).create(IGoogleApi.class);
    }
}
